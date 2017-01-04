package com.mz.training

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.jdbc.JDBCConnectionActor.Rollback
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

/**
  * Created by zemi on 28/12/2016.
  */
abstract class AbstractActorTest(actorSystemName: String) extends TestKit(ActorSystem(actorSystemName))
  with FunSuiteLike
  with BeforeAndAfterAll
  with Matchers
  with ImplicitSender {


  val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)

  val jdbcConActor = system.actorOf(JDBCConnectionActor.props)


  override protected def beforeAll(): Unit = {
    super.beforeAll()
  }


  override protected def afterAll(): Unit = {
    jdbcConActor ! Rollback
    system.terminate
  }
}
