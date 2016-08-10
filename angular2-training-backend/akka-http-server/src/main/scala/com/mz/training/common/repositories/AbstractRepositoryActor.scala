package com.mz.training.common.repositories

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern._
import akka.util.Timeout
import com.mz.training.common.messages.UnsupportedOperation
import com.mz.training.common.jdbc.JDBCConnectionActor._
import com.mz.training.domains.EntityId
import com.mz.training.common.mappers.SqlDomainMapper

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Future
/**
  * Created by zemi on 12/06/16.
  */
abstract class AbstractRepositoryActor[E <: EntityId](jdbcActor: ActorRef)
  extends Actor
    with ActorLogging
    with SqlDomainMapper[E] {

  context.watch(jdbcActor)

  protected implicit val timeout: Timeout = 2000.milliseconds

  override def receive: Receive = {
    case SelectById(id) => selectById(id) pipeTo sender
    case u:Update[E] => update(u.entity) pipeTo sender
    case Delete(id) => delete(id) pipeTo sender
    case i:Insert[E] => insert(i.entity) pipeTo sender
    case SelectAll => selectAll pipeTo sender
    case UnsupportedOperation => log.debug(s"sender sent UnsupportedOperation $sender")
    case _ => sender ! UnsupportedOperation
  }


  /**
    * Select all users
    *
    * @return
    */
  private def selectAll: Future[List[E]] = {
    log.debug("selectAll")
    (jdbcActor ? JdbcSelect(s"select $sqlProjection " +
      s"from $tableName ", mapResultSetList)).mapTo[JdbcSelectResult[mutable.MutableList[E]]].map(result => {
      log.debug("selectAll - success!")
      result.result.toList
    })
  }

  /**
    * Select user by id
    *
    * @param id
    * @return
    */
  private def selectById(id: Long): Future[Option[E]] = {
    log.debug(s"${getClass.getCanonicalName} going to selectId id = "+Thread.currentThread().getId())
    (jdbcActor ? JdbcSelect(s"select $sqlProjection " +
          s"from $tableName where $ID_COL = $id", mapResultSet)).mapTo[JdbcSelectResult[Option[E]]].map(result => {
      log.debug(s"${getClass.getCanonicalName} future execution of selectById id = "+Thread.currentThread().getId())
      result.result
    })
  }

  /**
    * Update user
    *
    * @param entity
    */
  private def update(implicit entity: E): Future[Boolean] = {
    log.debug(s"${getClass.getCanonicalName} going to update id = "+Thread.currentThread().getId())
    val updateQuery = s"""UPDATE $tableName SET $setValues"""
      val whereClause = s""" WHERE $ID_COL = ${entity.id}"""
    (jdbcActor ? JdbcUpdate(updateQuery.concat(whereClause))).mapTo[Boolean].map(result => {
      log.debug(s"${getClass.getCanonicalName} future execution of update id = "+Thread.currentThread().getId())
      result
    })
  }

  /**
    * Delete user by id
    *
    * @param id - id of user
    * @return Future of boolean
    */
  private def delete(id: Long): Future[Boolean] = {
    log.debug("delete")
    log.debug(s"${getClass.getCanonicalName} going to delete id = "+Thread.currentThread().getId())
    (jdbcActor ? JdbcDelete(s"DELETE FROM $tableName WHERE $ID_COL = $id")).mapTo[Boolean].map(result => {
      log.debug(s"${getClass.getCanonicalName} future execution of delete id = "+Thread.currentThread().getId())
      result
    })
  }

  /**
    * Insert User
    *
    * @param entity
    * @return
    */
  private def insert(implicit entity: E): Future[Inserted] = {
    log.debug("insert")
    log.debug(s"${getClass.getCanonicalName} going to insert id = "+Thread.currentThread().getId())
    (jdbcActor ? JdbcInsert(s"""INSERT INTO $tableName ($columns) VALUES ($values)""")).mapTo[GeneratedKeyRes].map(result => {
      Inserted(result.id)
    })
  }

}
