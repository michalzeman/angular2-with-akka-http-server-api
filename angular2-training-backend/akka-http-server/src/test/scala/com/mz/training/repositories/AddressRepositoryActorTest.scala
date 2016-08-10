package com.mz.training.repositories

import com.mz.training.common.jdbc.JDBCConnectionActor.Rollback
import com.mz.training.common.repositories._
import com.mz.training.domains.address.Address
import com.mz.training.domains.address.AddressRepositoryActor
import com.mz.training.repositories.common.AbstractRepositoryActorTest

import scala.concurrent.duration._

/**
 * Created by zemo on 17/10/15.
 */
class AddressRepositoryActorTest extends AbstractRepositoryActorTest {

  implicit val timeOut: akka.util.Timeout = 10000.millisecond

  test("CRUD operations") {
    val addressRepository = system.actorOf(AddressRepositoryActor.props(jdbcConActor))
    //Address(id: Long, street: String, zip: String, houseNumber: String, city: String)
     addressRepository ! Insert(Address(0, "test", "82109", "9A", "testCity"))
    val result = expectMsgType[Inserted]
    println(s"Id of inserted is ${result.id}")
    result.id should not be 0

    addressRepository ! SelectById(result.id)
    val resultSelect = expectMsgType[Some[Address]]
    resultSelect.get.street shouldBe("test")

    addressRepository ! Update(Address(result.id, "test 2", "83109", "10A", "testCityBA"))
    expectMsg(true)
    addressRepository ! SelectById(result.id)
    val resultSelectUpdated = expectMsgType[Some[Address]]
    resultSelectUpdated.get.street shouldBe("test 2")
    resultSelectUpdated.get.zip shouldBe("83109")

    addressRepository ! Delete(result.id)
    expectMsg(true)

    addressRepository ! SelectById(result.id)
    val resultSelectDeleted = expectMsgType[Option[Address]]
    resultSelectDeleted should not be isInstanceOf[Some[Address]]
  }

  override protected def afterAll(): Unit = {
    jdbcConActor ! Rollback
    system.shutdown()
  }

}
