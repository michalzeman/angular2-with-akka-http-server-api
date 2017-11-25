package com.mz.training.common.repositories

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern._
import akka.util.Timeout
import com.mz.training.common.jdbc.JDBCConnectionActor._
import com.mz.training.common.mappers.SqlDomainMapper
import com.mz.training.common.messages.UnsupportedOperation
import com.mz.training.domains.EntityId
import com.typesafe.config.Config

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * Created by zemi on 12/06/16.
  */
abstract class AbstractRepositoryActor[E <: EntityId](jdbcActor: ActorRef)
  extends Actor
    with ActorLogging
    with SqlDomainMapper[E] {

  context.watch(jdbcActor)

  protected val sysConfig: Config = context.system.settings.config

  protected implicit val timeout: Timeout = DurationInt(sysConfig.getInt("akka.actor.timeout.repository")).millisecond

  override def receive: Receive = {
    case SelectById(id) => selectById(id) pipeTo sender
    case u: Update[E] => update(u.entity) pipeTo sender
    case Delete(id) => delete(id) pipeTo sender
    case i: Insert[E] => insert(i.entity) pipeTo sender
    case SelectAll => selectAll pipeTo sender
    case msg: SelectByIdList => selectByIdList(msg) pipeTo sender
    case UnsupportedOperation => log.debug(s"sender sent UnsupportedOperation $sender")
    case SelectCount() => selectCount pipeTo sender
    case msg: SelectPaging => selectPaging(msg) pipeTo sender
    case _ => sender ! UnsupportedOperation
  }


  /**
    * Select all users
    *
    * @return
    */
  protected def selectAll: Future[List[E]] = {
    log.debug("selectAll")
    (jdbcActor ? JdbcSelect(s"select $sqlProjection " +
      s"from $tableName ", mapResultSetList)).mapTo[JdbcSelectResult[mutable.MutableList[E]]].map(result => {
      log.debug("selectAll - success!")
      result.result.toList
    })
  }

  /**
    * Select by ids
    *
    * @param msg
    * @return Future of result List
    */
  protected def selectByIdList(msg: SelectByIdList): Future[List[E]] = {
    log.debug(s"${getClass.getCanonicalName} going to selectId id = " + Thread.currentThread.getId)
    val p = Promise[List[E]]
    Future {
      val sqlQuery = s"select $sqlProjection from $tableName where $ID_COL in ${mapListIds(msg.ids)}"
      (jdbcActor ? JdbcSelect(sqlQuery, mapResultSetList)).mapTo[JdbcSelectResult[mutable.MutableList[E]]].map(result => {
        log.debug(s"${getClass.getCanonicalName} future execution of selectById id = " + Thread.currentThread.getId)
        result.result.toList
      }) onComplete {
        case Success(s: List[E]) => p.success(s)
        case Failure(exc) => p.failure(exc)
      }
    }
    p.future
  }

  /**
    * Select pagination
    *
    * @param msg
    * @return Future of result List
    */
  protected def selectPaging(msg: SelectPaging): Future[List[E]] = {
    log.debug(s"${getClass.getCanonicalName} going to selectPaging")
    val sqlQuery = s"select $sqlProjection from $tableName order by $ID_COL LIMIT ${msg.itemsPerPage} OFFSET ${msg.offset}"
    (jdbcActor ? JdbcSelect(sqlQuery, mapResultSetList)).mapTo[JdbcSelectResult[mutable.MutableList[E]]].map(result => {
      log.debug(s"${getClass.getCanonicalName} future execution of selectById id = " + Thread.currentThread.getId)
      result.result.toList
    })
  }

  /**
    * Select user by id
    *
    * @param id
    * @return
    */
  protected def selectById(id: Long): Future[Option[E]] = {
    log.debug(s"${getClass.getCanonicalName} going to selectId id = " + Thread.currentThread.getId)
    (jdbcActor ? JdbcSelect(s"select $sqlProjection " +
      s"from $tableName where $ID_COL = $id", mapResultSet)).mapTo[JdbcSelectResult[Option[E]]].map(result => {
      log.debug(s"${getClass.getCanonicalName} future execution of selectById id = " + Thread.currentThread.getId)
      result.result
    })
  }

  protected def selectCount: Future[Option[Long]] = {
    log.debug(s"${getClass.getCanonicalName} going to selectCount = " + Thread.currentThread.getId)
    (jdbcActor ? JdbcSelect(s"select count(*) from $tableName ", mapCount)).mapTo[JdbcSelectResult[Option[Long]]]
      .map(result => {
        log.debug(s"${getClass.getCanonicalName} future execution of selectCount thread id = " + Thread.currentThread.getId)
        result.result
      })
  }

  /**
    * Update user
    *
    * @param entity
    */
  protected def update(implicit entity: E): Future[Boolean] = {
    log.debug(s"${getClass.getCanonicalName} going to update id = " + Thread.currentThread.getId)
    val updateQuery = s"""UPDATE $tableName SET $setValues"""
    val whereClause = s""" WHERE $ID_COL = ${entity.id}"""
    (jdbcActor ? JdbcUpdate(updateQuery.concat(whereClause))).mapTo[Boolean].map(result => {
      log.debug(s"${getClass.getCanonicalName} future execution of update id = " + Thread.currentThread.getId)
      result
    })
  }

  /**
    * Delete user by id
    *
    * @param id - id of user
    * @return Future of boolean
    */
  protected def delete(id: Long): Future[Boolean] = {
    log.debug("delete")
    log.debug(s"${getClass.getCanonicalName} going to delete id = " + Thread.currentThread.getId)
    (jdbcActor ? JdbcDelete(s"DELETE FROM $tableName WHERE $ID_COL = $id")).mapTo[Boolean].map(result => {
      log.debug(s"${getClass.getCanonicalName} future execution of delete id = " + Thread.currentThread.getId)
      result
    })
  }

  /**
    * Insert User
    *
    * @param entity
    * @return
    */
  protected def insert(implicit entity: E): Future[Inserted] = {
    log.debug("insert")
    log.debug(s"${getClass.getCanonicalName} going to insert id = " + Thread.currentThread.getId)
    (jdbcActor ? JdbcInsert(s"""INSERT INTO $tableName ($columns) VALUES ($values)""")).mapTo[GeneratedKeyRes].map(result => {
      Inserted(result.id)
    })
  }

  /**
    * Map list of params to (param, param1, ....)
    *
    * @param params
    * @return
    */
  protected def mapListIds(params: List[Long]): String = {
    @tailrec
    def map(query: String, params: List[Long]): String = {
      params match {
        case item :: items => {
          val queryStart = if (query == "(") "" else ","
          map(query.concat(queryStart).concat(item.toString), items)
        }
        case Nil => query.concat(")")
      }
    }

    map("(", params)
  }

}
