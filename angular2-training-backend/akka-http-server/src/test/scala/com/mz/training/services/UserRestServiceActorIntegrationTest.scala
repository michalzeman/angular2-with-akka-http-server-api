package com.mz.training.services

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.jdbc.JDBCConnectionActor.{Commit, Committed, Rollback}
import com.mz.training.common.services._
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import com.mz.training.domains.address.{Address, AddressRepositoryActor, AddressServiceActor}
import com.mz.training.domains.user.UserServiceActor.{RegistrateUser, UserRegistrated}
import com.mz.training.domains.user.{User, UserRepositoryActor, UserServiceActor}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}
import org.scalautils.ConversionCheckedTripleEquals

import scala.concurrent.duration._

/**
 * Created by zemi on 22. 10. 2015.
 */
class UserRestServiceActorIntegrationTest extends TestKit(ActorSystem("test-jdbc-demo-UserServiceActorTest"))
with FunSuiteLike
with BeforeAndAfterAll
with Matchers
with ConversionCheckedTripleEquals
with ImplicitSender
with MockitoSugar {

  implicit val timeOut: akka.util.Timeout = 10000.millisecond

  val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)

  val jdbcConActor = system.actorOf(JDBCConnectionActor.props)

  test("Create user") {

    val userRepositoryProps = UserRepositoryActor.props(jdbcConActor)

    val addressRepositoryProps = AddressRepositoryActor.props(jdbcConActor);

    val addressService = AddressServiceActor.props(userRepositoryProps, addressRepositoryProps)

    val userService = system.actorOf(UserServiceActor.props(userRepositoryProps, addressService))

    userService ! Create(User(0,"FirstNameTest", "LastNameTest", None, None))

    val result = expectMsgType[Created]

    result.id should not be(0)

    jdbcConActor ! Rollback
  }

  test("Update user") {
    val userRepositoryProps = UserRepositoryActor.props(jdbcConActor)

    val addressRepositoryProps = AddressRepositoryActor.props(jdbcConActor);

    val addressService = AddressServiceActor.props(userRepositoryProps, addressRepositoryProps)

    val userService = system.actorOf(UserServiceActor.props(userRepositoryProps, addressService))

    userService ! Create(User(0,"FirstNameTest", "LastNameTest", None, None))

    val result = expectMsgType[Created]

    userService ! Update(User(result.id, "FirstNameUpdated", "LastNameUpdated", None, None))
    expectMsgType[Updated[User]]

    userService ! FindById(result.id)
    val resultAfterUpdate = expectMsgType[Found[User]]

    resultAfterUpdate.results.size should not be 0

//    resultAfterUpdate.results.head.firstName shouldBe "FirstNameUpdated"

    jdbcConActor ! Rollback
  }

  test("Delete user") {
    val userRepositoryProps = UserRepositoryActor.props(jdbcConActor)

    val addressRepositoryProps = AddressRepositoryActor.props(jdbcConActor);

    val addressService = AddressServiceActor.props(userRepositoryProps, addressRepositoryProps)

    val userService = system.actorOf(UserServiceActor.props(userRepositoryProps, addressService))

    userService ! Create(User(0,"FirstNameTest", "LastNameTest", None, None))
    val result = expectMsgType[Created]

    userService ! Delete(User(result.id, "FirstNameTest", "LastNameTest", None, None))
    expectMsgType[Deleted]

    jdbcConActor ! Commit
    expectMsg(Committed)

    userService ! FindById(result.id)
    val resultAfterDelete = expectMsgType[Found[User]]

    resultAfterDelete.results.size shouldBe 0
  }

  test("Register user") {
    val userRepositoryProps = UserRepositoryActor.props(jdbcConActor)

    val addressRepositoryProps = AddressRepositoryActor.props(jdbcConActor);

    val addressService = AddressServiceActor.props(userRepositoryProps, addressRepositoryProps)

    val userService = system.actorOf(UserServiceActor.props(userRepositoryProps, addressService))

    userService ! RegistrateUser(User(0,"FirstNameTest", "LastNameTest", None, None),
      Address(0, "test", "82109", "9A", "testCity"))
    expectMsgType[UserRegistrated]

    jdbcConActor ! Rollback
  }

  override protected def afterAll(): Unit = {
    jdbcConActor ! Rollback
    system.shutdown()
  }
}
