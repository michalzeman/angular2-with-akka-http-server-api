package com.mz.training.common.jdbc

import java.sql.{Connection, ResultSet, SQLException, Statement}

import akka.actor._
import com.mz.training.common.messages.UnsupportedOperation
import com.mz.training.common.factories.jdbc.DataSourceActorFactory
import com.mz.training.common.jdbc.DataSourceActor.{ConnectionResult, GetConnection}
import com.mz.training.common.jdbc.JDBCConnectionActor._
import com.typesafe.config.Config

import scala.concurrent.duration._

/**
 * Created by zemi on 1. 10. 2015.
 */
class JDBCConnectionActor extends Actor with ActorLogging with DataSourceActorFactory with Stash {

  import DataSourceActor.SCHEMA

  private val sysConfig: Config = context.system.settings.config
  private val defaultSchema = sysConfig.getString(SCHEMA)

  var connection: Option[Connection] = None
  val conInterceptorActor = context.actorOf(ConnectionInterceptorActor.props)

  context.become(connectionClosed)

  @throws[RuntimeException](classOf[RuntimeException])
  override def preStart(): Unit = {
//    log.info("init of Actor")
//    askForConnection
  }

  override def receive: Receive = {
    case UnsupportedOperation => log.debug(s"Receive => sender sent UnsupportedOperation $sender")
    case obj: Any => {
      log.warning(s"receive => Unsupported operation object ${obj.getClass}")
      sender() ! UnsupportedOperation
    }
  }

  private def connectionClosed: Receive = {
//    case RetryOperation(opr, orgSender) => retryOperation(opr, orgSender)
    case obj: Any => {
      log.info(s"Connection is closed! Going to ask new connection!")
//      retryOperation(obj, sender)
      askForConnection
    }
  }

  private def waitingForConnection: Receive = {
    case ConnectionResult(con) => {
      log.debug("Connection returned!")
      conInterceptorActor ! ActorStop
      con.setSchema(defaultSchema)
      connection = Some(con)
      context.become(connectionReady)
      unstashAll()
    }
    case obj: Any => {
      log.warning(s"receive => waitingForConnection ${obj.getClass}")
      stash()
    }
  }

  private def connectionReady: Receive = {
    case JdbcInsert(query) => insert(query, sender)
    case JdbcUpdate(query) => update(query, sender)
    case JdbcDelete(query) => delete(query, sender)
    case JdbcSelect(query, mapper) =>  select(query, mapper, sender)
    case Commit => commit
    case Rollback => rollback
    case UnsupportedOperation => log.debug(s"sender sent UnsupportedOperation $sender")
    case obj: Any => {
      log.warning(s"connectionReady => Unsupported operation object ${obj.getClass}")
      sender() ! UnsupportedOperation
    }
  }

  /**
   * Ask for new connection from DataSourceActor
   */
  private def askForConnection: Unit = {
    log.info(s"${getClass.getCanonicalName} askForConnection ->")
    stash()
    context.become(waitingForConnection)
    selectDataSourceActor ! GetConnection
    conInterceptorActor ! GetConnection
  }

  /**
   * Execute select
 *
   * @param query
   * @return
   */
  private def select[E](query: String, mapper: ResultSet => E, senderOrg: ActorRef): Unit = {
    connection.map(con => {
      log.info(s"Select query = $query")
      val prtStatement = con.prepareStatement(query)
      try {
        senderOrg ! JdbcSelectResult(mapper(prtStatement.executeQuery()))
      } catch {
        case e: SQLException => {
          log.error(e.getMessage, e)
          senderOrg ! akka.actor.Status.Failure(e)
        }
      } finally {
        prtStatement.close
      }
    })
  }

  /**
   * execute delete
 *
   * @param query
   * @return Future
   */
  private def delete(query: String, senderOrg: ActorRef): Unit = {
    log.info(s"Delete query = $query")
    executeUpdate(query, senderOrg)
  }

  /**
   * execute update
 *
   * @param query
   * @return Future
   */
  private def update(query: String, senderOrg: ActorRef): Unit = {
    log.info(s"Update query = $query")
    executeUpdate(query, senderOrg)
  }

  /**
   * Insert
 *
   * @param query
   * @return
   */
  private def insert(query: String, senderOrg: ActorRef): Unit = {
    connection.map(con => {
      log.info(s"Insert query = $query")
      val prtStatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
      try {
        prtStatement.executeUpdate()
        val keys = prtStatement.getGeneratedKeys
        if (keys.next) {
          log.debug("inserted successful!")
          senderOrg ! GeneratedKeyRes(keys.getLong(1))
        } else {
          senderOrg ! GeneratedKeyRes(0)
        }
      } catch {
        case e: SQLException => {
          log.error(e, e.getMessage)
          senderOrg ! akka.actor.Status.Failure(e)
        }
      } finally {
        prtStatement.close
      }
    })
  }

  /**
   * Execute Commit
   */
  private def commit: Unit = {
    log.debug("JDBCConnectionActor.Commit")
    connection.map(con => {
      try {
        con.commit()
        sender() ! Committed
      } catch {
        case e: SQLException => {
          log.error(e, "JDBCConnectionActor.Commit")
          sender ! akka.actor.Status.Failure(e)
          throw e
        }
      } finally {
        context.become(connectionClosed)
        if (!con.isClosed) {
          con.close()
        }
        connection = None
      }
    })
  }

  /**
   * execute rollback
   */
  private def rollback: Unit = {
    log.debug("JDBCConnectionActor.Rollback")
    connection.map(con => {
      try {
        con.rollback()
      } catch {
        case e: SQLException => {
          log.error(e, "JDBCConnectionActor.Rollback")
          sender ! akka.actor.Status.Failure(e)
          throw e
        }
      } finally {
        context.become(connectionClosed)
        if (!con.isClosed) {
          con.close()
        }
        connection = None
      }
    })
  }

  private def executeUpdate(query: String, senderOrg: ActorRef): Unit = {
    connection.map(con => {
      val prtStatement = con.prepareStatement(query)
      try {
        prtStatement.executeUpdate()
        senderOrg ! true
      } catch {
        case e: SQLException => {
          log.error(e.getMessage, e)
          senderOrg ! akka.actor.Status.Failure(e)
        }
      } finally {
        prtStatement.close
      }
    })
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    connection.map(con => {
      try {
        con.rollback()
      } catch {
        case e: SQLException => {
          log.error(e, e.getMessage)
        }
      } finally {
        if (!con.isClosed) {
          log.debug("JDBCConnectionActor.connection.close()")
          con.close
        }
      }
    })
  }
}

/**
 * Companion object
 */
object JDBCConnectionActor {

  /**
   *
   */
  case class InitConnection(actor: ActorRef)

  /**
   * message for commit transaction
   */
  case object Commit

  /**
   * confirmation message for successful commit
   */
  case object Committed

  /**
   * message for rollback transaction
   */
  case object Rollback

  /**
   * confirmation message for successful rollback
   */
  case object RollbackSuccess

  /**
   * Insert statement
 *
   * @param query
    */
  case class JdbcInsert(query: String)

  /**
   * Update statement
 *
   * @param query
   */
  case class JdbcUpdate(query: String)

  /**
   * Delete statement
 *
   * @param query
   */
  case class JdbcDelete(query: String)

  /**
   * Select statement
 *
   * @param query
   */
  case class JdbcSelect[+E](query: String, mapper: ResultSet => E)

  /**
   * Result of select
 *
   * @param result - E
   */
  case class JdbcSelectResult[+E](result: E)

  /**
   * Generated key as a result after Insert
 *
   * @param id
   */
  case class GeneratedKeyRes(id: Long)

  /**
   * Create Props for an actor of this type
 *
   * @return a Props
   */
  def props: Props = Props[JDBCConnectionActor]
}
