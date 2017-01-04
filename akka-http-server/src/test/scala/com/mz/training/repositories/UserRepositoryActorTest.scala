package com.mz.training.repositories

import com.mz.training.common.jdbc.JDBCConnectionActor.Rollback
import com.mz.training.common.repositories._
import com.mz.training.domains.address.{Address, AddressRepositoryActor}
import com.mz.training.domains.user.{User, UserRepositoryActor}
import com.mz.training.repositories.common.AbstractRepositoryActorTest

import scala.concurrent.duration._

/**
 * Created by zemo on 12/10/15.
 */
class UserRepositoryActorTest extends AbstractRepositoryActorTest {

  implicit val timeOut: akka.util.Timeout = 10000.millisecond

  test("CRUD operations") {
    val userRepository = system.actorOf(UserRepositoryActor.props(jdbcConActor))
    val addressRepository = system.actorOf(AddressRepositoryActor.props(jdbcConActor))
    addressRepository ! Insert(Address(0, "test", "82109", "9A", "testCity"))
    val addrIdRes:Inserted = expectMsgType[Inserted]

    val user = User(0, "test", "Test 2", Option(addrIdRes.id), None)
    userRepository ! Insert(user)
    val result: Inserted = expectMsgType[Inserted]

    val userSel = User(result.id, "test", "Test 2", Option(addrIdRes.id), None)
    userRepository ! SelectById(result.id)
    expectMsg(Some(userSel))

    val user2 = User(result.id, "UpdateTest", "UpdateTest 2", Option(addrIdRes.id), None)
    userRepository ! Update(user2)
    expectMsg(true)
    userRepository ! SelectById(result.id)
    expectMsg(Some(user2))

    userRepository ! Delete(result.id)
    expectMsg(true)
    userRepository ! SelectById(result.id)
    expectMsgAnyOf(None)

    jdbcConActor ! Rollback
  }
  test("Select All") {
    val userRepository = system.actorOf(UserRepositoryActor.props(jdbcConActor))
    val addressRepository = system.actorOf(AddressRepositoryActor.props(jdbcConActor))
    addressRepository ! Insert(Address(0, "test", "82109", "9A", "testCity"))
    val addrIdRes:Inserted = expectMsgType[Inserted]

    val user = User(0, "test", "Test 2", Option(addrIdRes.id), None)
    userRepository ! Insert(user)
    val result: Inserted = expectMsgType[Inserted]

    val user3 = User(0, "test_3", "Test 3", Option(addrIdRes.id), None)
    userRepository ! Insert(user3)
    val result3: Inserted = expectMsgType[Inserted]

    val user4 = User(0, "test_4", "Test 4", Option(addrIdRes.id), None)
    userRepository ! Insert(user4)
    val result4: Inserted = expectMsgType[Inserted]

    userRepository ! SelectAll
    val resultList = expectMsgType[Seq[User]]
    (resultList.size > 0) shouldBe true
  }

  override protected def afterAll(): Unit = {
    jdbcConActor ! Rollback
    system.shutdown()
  }

}
