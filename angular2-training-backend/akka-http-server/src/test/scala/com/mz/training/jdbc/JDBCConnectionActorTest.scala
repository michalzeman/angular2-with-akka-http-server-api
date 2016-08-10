package com.mz.training.jdbc

import java.sql.ResultSet

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.jdbc.JDBCConnectionActor.{JdbcSelect, JdbcSelectResult}
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import com.mz.training.domains.user.User
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}
import org.scalautils.ConversionCheckedTripleEquals

import scala.concurrent.duration._

/**
 * Created by zemo on 04/10/15.
 */
class JDBCConnectionActorTest extends TestKit(ActorSystem("test-jdbc-demo-JDBCConnectionActorTest")) with FunSuiteLike
with BeforeAndAfterAll
with Matchers
with ConversionCheckedTripleEquals
with ImplicitSender
with MockitoSugar {

  implicit val timeOut: akka.util.Timeout = 2000.millisecond

  override def afterAll(): Unit = {
    system.shutdown()
  }

  test("GetConnection timeout") {
    val jdbcActor = system.actorOf(JDBCConnectionActor.props)
    val query = "Select from users where id = 0"
    def mapper (resultSet: ResultSet): Option[User] = {None}
    jdbcActor ! JdbcSelect(query, mapper)
    expectNoMsg(2 seconds)
  }

  test("select operation") {
    val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)
    val jdbcActor = system.actorOf(JDBCConnectionActor.props)
    val query = "Select from users where id = 0"
    def mapper (resultSet: ResultSet): Option[User] = {None}
    jdbcActor ! JdbcSelect(query, mapper)
    expectMsgAnyOf(JdbcSelectResult(None))
  }
}
