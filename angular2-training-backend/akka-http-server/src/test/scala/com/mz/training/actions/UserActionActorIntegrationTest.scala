package com.mz.training.actions

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.pattern._
import akka.testkit.{ImplicitSender, TestKit}
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import com.mz.training.domains.address.Address
import com.mz.training.domains.user.User
import com.mz.training.domains.user.UserServiceActor.RegistrateUser
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}
import org.scalautils.ConversionCheckedTripleEquals

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by zemi on 29. 10. 2015.
 */
class UserActionActorIntegrationTest extends TestKit(ActorSystem("test-jdbc-demo-UserActionActorTest"))
with FunSuiteLike
with BeforeAndAfterAll
with Matchers
with ConversionCheckedTripleEquals
with ImplicitSender
with MockitoSugar {

  implicit val timeOut: akka.util.Timeout = 10000.millisecond

  val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)

  test("Registrate user") {
    val futures =
      for (i <- 1 to 10000) yield {
        Thread sleep 2
        val userAction = system.actorOf(Props[UserActionActor])
        (userAction ? RegistrateUser(User(0, "FirstNameTest", "LastNameTest", None, None),
          Address(0, "test", "82109", "9A", "testCity")))
      }

    for {future <- futures} yield Await.result(future, 1 minutes)

  }

  test("Parent acotr stop") {
    val userAction = system.actorOf(Props[UserActionActor])
    userAction ! PoisonPill
    expectNoMsg(200 microseconds)
    userAction ! RegistrateUser(User(0, "FirstNameTest", "LastNameTest", None, None)
      ,Address(0, "test", "82109", "9A", "testCity"))
    expectNoMsg(5 seconds)
  }

  override protected def afterAll(): Unit = {
    system.shutdown()
  }
}
