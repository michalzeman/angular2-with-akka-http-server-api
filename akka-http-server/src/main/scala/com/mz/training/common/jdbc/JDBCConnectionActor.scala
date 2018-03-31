package com.mz.training.common.jdbc

import java.sql.{Connection, ResultSet, SQLException, Statement}
import java.util.UUID

import akka.actor._
import com.mz.training.common.factories.jdbc.DataSourceActorFactory
import com.mz.training.common.jdbc.DataSourceActor.{ConnectionResult, GetConnection}
import com.mz.training.common.jdbc.JDBCConnectionActor._
import com.mz.training.common.messages.UnsupportedOperation
import com.typesafe.config.Config

import scala.util.{Failure, Success, Try}

/**
  * Created by zemi on 1. 10. 2015.
  */
class JDBCConnectionActor extends Actor with ActorLogging with DataSourceActorFactory with Stash {

  import DataSourceActor.SCHEMA

  private val sysConfig: Config = context.system.settings.config
  private val defaultSchema = sysConfig.getString(SCHEMA)

  private val correlationId = UUID.randomUUID()
  selectDataSourceActor ! Identify(correlationId)

  private var dataSourceActor: Option[ActorRef] = None

  private var connection: Option[Connection] = None
  private val conInterceptorActor: ActorRef = context.actorOf(ConnectionInterceptorActor.props)

  override def receive: Receive = {
    case ActorIdentity(`correlationId`, Some(ref)) =>
      context.watch(ref)
      dataSourceActor = Some(ref)
      context.become(connectionClosed)
      unstashAll()
    case ActorIdentity(`correlationId`, None) =>
      log.error("Storage not ready, unable to process requests")
      context.stop(self)
    case _ => {
      stash()
    }
  }

  private def connectionClosed: Receive = {
    case _ => {
      log.info(s"Connection is closed! Going to ask new connection!")
      askForConnection()
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
    case JdbcSelect(query, mapper) => select(query, mapper, sender)
    case Commit => commit()
    case Rollback => rollback()
    case UnsupportedOperation => log.debug(s"sender sent UnsupportedOperation $sender")
    case obj: Any => {
      log.warning(s"connectionReady => Unsupported operation object ${obj.getClass}")
      sender() ! UnsupportedOperation
    }
  }

  /**
    * Ask for new connection from DataSourceActor
    */
  private def askForConnection(): Unit = {
    log.info(s"${getClass.getCanonicalName} askForConnection ->")
    stash()
    context.become(waitingForConnection)
    dataSourceActor.foreach(dataSource => dataSource ! GetConnection)
    conInterceptorActor ! GetConnection
  }

  /**
    * Execute select
    *
    * @param query
    * @return
    */
  private def select[E](query: String, mapper: ResultSet => E, senderOrg: ActorRef): Unit = {
    connection.foreach(con => {
      log.info(s"Select query = $query")
      Try(con.prepareStatement(query)) match {
        case Success(prtStatement) => {
          try {
            senderOrg ! JdbcSelectResult(mapper(prtStatement.executeQuery()))
          } catch {
            case e: SQLException => {
              log.error(e.getMessage, e)
              senderOrg ! akka.actor.Status.Failure(e)
            }
          } finally {
            prtStatement.close()
          }
        }
        case Failure(e) => {
          log.error(e.getMessage, e)
          senderOrg ! akka.actor.Status.Failure(e)
        }
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
    connection.foreach(con => {
      log.info(s"Insert query = $query")
      Try(con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) match {
        case Success(prtStatement) => {
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
            prtStatement.close()
          }
        }
        case Failure(e) => {
          log.error(e, e.getMessage)
          senderOrg ! akka.actor.Status.Failure(e)
        }
      }
    })
  }

  /**
    * Execute Commit
    */
  private def commit(): Unit = {
    log.debug("JDBCConnectionActor.Commit")
    connection = connection.flatMap(con => {
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
        if (!con.isClosed) con.close()
      }
      None
    })
  }

  /**
    * execute rollback
    */
  private def rollback(): Unit = {
    log.debug("JDBCConnectionActor.Rollback")
    connection = connection.flatMap(con => {
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
        if (!con.isClosed) con.close()
      }
      None
    })
  }

  private def executeUpdate(query: String, senderOrg: ActorRef): Unit = {
    connection.foreach(con => {
      Try(con.prepareStatement(query)) match {
        case Success(prtStatement) => {
          try {
            prtStatement.executeUpdate()
            senderOrg ! true
          } catch {
            case e: SQLException => {
              log.error(e.getMessage, e)
              senderOrg ! akka.actor.Status.Failure(e)
            }
          } finally {
            prtStatement.close()
          }
        }
        case Failure(e) => {
          log.error(e.getMessage, e)
          senderOrg ! akka.actor.Status.Failure(e)
        }
      }
    })
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    connection.foreach(con => {
      try {
        con.rollback()
      } catch {
        case e: SQLException => {
          log.error(e, e.getMessage)
        }
      } finally {
        if (!con.isClosed) {
          log.debug("JDBCConnectionActor.connection.close()")
          con.close()
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
