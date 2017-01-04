package com.mz.training.jdbc

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.mz.training.common.factories.jdbc.DataSourceActorFactory
import com.mz.training.common.jdbc.DataSourceActor.{ConnectionResult, GetConnection}
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

import scala.concurrent.duration._

/**
 * Created by zemo on 05/10/15.
 */
class DataSourceActorTest extends TestKit(ActorSystem("test-jdbc-demo-DataSourceActorTest"))
with FunSuiteLike
with BeforeAndAfterAll
with Matchers
with ImplicitSender
with DataSourceActorFactory {

  implicit val timeOut: akka.util.Timeout = 2000.millisecond

  override protected def afterAll(): Unit = {
    system.shutdown()
  }

  test("init dataSource") {
    val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)
    selectDataSourceActor(system) ! GetConnection
    expectMsgType[ConnectionResult](5 seconds)
  }
}
