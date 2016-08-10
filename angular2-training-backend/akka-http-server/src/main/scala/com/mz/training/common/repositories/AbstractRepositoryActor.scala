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
    val p = Promise[List[E]]
    (jdbcActor ? JdbcSelect(s"select $sqlProjection " +
      s"from $tableName ", mapResultSetList)).mapTo[JdbcSelectResult[mutable.MutableList[E]]] onComplete {
      case Success(result) => {
        log.debug("selectAll - success!")
        p.success(result.result.toList)
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
  }

  /**
    * Select user by id
    *
    * @param id
    * @return
    */
  private def selectById(id: Long): Future[Option[E]] = {
    log.debug("SelectById")
    val p = Promise[Option[E]]
    log.debug(s"${getClass.getCanonicalName} going to selectId id = "+Thread.currentThread().getId())
    (jdbcActor ? JdbcSelect(s"select $sqlProjection " +
      s"from $tableName where $ID_COL = $id", mapResultSet)).mapTo[JdbcSelectResult[Option[E]]] onComplete {
      case Success(s) => {
        log.debug(s"${getClass.getCanonicalName} future execution of selectById id = "+Thread.currentThread().getId())
        p.success(s.result)
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
  }

  /**
    * Update user
    *
    * @param entity
    */
  private def update(implicit entity: E): Future[Boolean] = {
    log.debug("update")
    log.debug(s"${getClass.getCanonicalName} going to update id = "+Thread.currentThread().getId())
    val p = Promise[Boolean]
    Future {

      val updateQuery =
        s"""UPDATE $tableName SET $setValues"""
      val whereClause = s""" WHERE $ID_COL = ${entity.id}"""
      (jdbcActor ? JdbcUpdate(updateQuery.concat(whereClause))).mapTo[Boolean] onComplete {
        case Success(s) => {
          log.debug(s"${getClass.getCanonicalName} future execution of update id = "+Thread.currentThread().getId())
          p.success(s)
        }
        case Failure(f) => {
          log.error(f, f.getMessage)
          p.failure(f)
        }
      }
    }
    p.future
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
    val p = Promise[Boolean]
    (jdbcActor ? JdbcDelete(s"DELETE FROM $tableName WHERE $ID_COL = $id")).mapTo[Boolean] onComplete {
      case Success(s) => {
        log.debug(s"${getClass.getCanonicalName} future execution of delete id = "+Thread.currentThread().getId())
        p.success(s)
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
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
    val p = Promise[Inserted]
    Future {
      log.debug(s"${getClass.getCanonicalName} future execution of insert id = "+Thread.currentThread().getId())
      (jdbcActor ? JdbcInsert(
        s"""INSERT INTO $tableName ($columns)
           VALUES ($values)""")).mapTo[GeneratedKeyRes] onComplete {
        case Success(result) => p.success(Inserted(result.id))
        case Failure(f) => {
          log.error(f, f.getMessage)
          p.failure(f)
        }
      }
    }
    p.future
  }

}
