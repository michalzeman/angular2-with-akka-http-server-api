package com.mz.training.common.jdbc

import java.sql.{Connection, SQLException}
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import com.mz.training.common.messages._
import com.typesafe.config.Config
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

class DataSourceActor extends Actor with ActorLogging {

  import DataSourceActor._

  private val sysConfig: Config = context.system.settings.config
  private val dataSource = initDataSource

  override def receive: Receive = {
    case GetConnection => getConnection
    case _ => sender ! UnsupportedOperation
  }

  /**
    * Return connection from the connection pool
    */
  private def getConnection: Unit = {
    log.info(s"${getClass.getCanonicalName} getConnection ->")
    try {
      val con = dataSource.getConnection
      sender ! ConnectionResult(con)
    } catch {
      case e: SQLException => {
        log.error(e, e.getMessage)
        throw e
      }
    }
  }

  private def configCon: HikariConfig = {
    log.debug("dataPool creating")
    val config = new HikariConfig()
    config.setMinimumIdle(sysConfig.getInt(DATASOURCE_MINIMUMIDLE))
    config.setMaximumPoolSize(sysConfig.getInt(DATASOURCE_MAXIMUMPOOLSIZE))
    config.setDriverClassName(sysConfig.getString(DRIVER))
    config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(sysConfig.getInt(DATASOURCE_CONNECTIONTIMEOUT)))
    config.setIdleTimeout(TimeUnit.SECONDS.toMillis(10))
    config.setValidationTimeout(TimeUnit.SECONDS.toMillis(sysConfig.getInt(DATASOURCE_VALIDATIONTIMEOUT)))
    config.setJdbcUrl(sysConfig.getString(CONNECTION_URL))
    config.setUsername(sysConfig.getString(DB_USER));
    config.setPassword(sysConfig.getString(DB_PASSWORD))
    config.setAutoCommit(sysConfig.getBoolean(DATASOURCE_AUTOCOMMIT))
    config
  }

  /**
    * Init data source
    *
    * @return HikariDataSource object
    */
  private def initDataSource: HikariDataSource = {
    new HikariDataSource(configCon)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    if (!dataSource.isClosed) dataSource.close()
    super.postStop()
  }
}

/**
  * Created by zemi on 1. 10. 2015.
  * Actor for creating and handling connection to the DB
  */
object DataSourceActor {

  /**
    * Requesting of connection
    */
  case object GetConnection

  /**
    * Message with the connection
    *
    * @param con @java.sql.Connection
    */
  case class ConnectionResult(con: Connection)

  val DRIVER = "akka.dataSource.driver"
  val SCHEMA = "akka.dataSource.schema"
  val CONNECTION_URL = "akka.dataSource.connection.url"
  val DB_USER = "akka.dataSource.user"
  val DB_PASSWORD = "akka.dataSource.password"
  val DATASOURCE_CLASSNAME = "akka.dataSource.dataSourceClassName"
  val DATASOURCE_DATABASENAME = "akka.dataSource.databaseName"
  val DATASOURCE_PORTNUMBER = "akka.dataSource.portNumber"
  val DATASOURCE_SERVERNAME = "akka.dataSource.serverName"
  val DATASOURCE_MINIMUMIDLE = "akka.dataSource.minimumIdle"
  val DATASOURCE_MAXLIFETIME = "akka.dataSource.maxLifetime"
  val DATASOURCE_AUTOCOMMIT = "akka.dataSource.autoCommit"
  val DATASOURCE_CONNECTIONTIMEOUT = "akka.dataSource.connectionTimeout"
  val DATASOURCE_MAXIMUMPOOLSIZE = "akka.dataSource.maximumPoolSize"
  val DATASOURCE_VALIDATIONTIMEOUT = "akka.dataSource.validationTimeout"

  val actorName = "dataSource"

  /**
    * Create Props for an actor of this type
    *
    * @return a Props
    */
  //  def props: Props = FromConfig.props(Props[DataSourceActor])
  def props: Props = Props[DataSourceActor].withDispatcher("akka.dataSource-dispatcher")
}
