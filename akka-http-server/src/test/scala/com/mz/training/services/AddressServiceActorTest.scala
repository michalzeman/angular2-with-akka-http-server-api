package com.mz.training.services

import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.jdbc.JDBCConnectionActor._
import com.mz.training.common.repositories.{Insert, Inserted, SelectCount, SelectPaging}
import com.mz.training.common.services._
import com.mz.training.common.supervisors.DataSourceSupervisorActor
import com.mz.training.domains.address.Address
import com.mz.training.domains.address.AddressServiceActor.FindOrCreateAddress
import com.mz.training.domains.address.{AddressRepositoryActor, AddressServiceActor}
import com.mz.training.domains.user.UserRepositoryActor
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

import scala.collection.mutable

/**
 * Created by zemi on 13. 11. 2015.
 */
class AddressServiceActorTest extends TestKit(ActorSystem("test-jdbc-demo-AddressServiceActorTest"))
with FunSuiteLike
with BeforeAndAfterAll
with Matchers
with ImplicitSender {

  val dataSourceSupervisor = system.actorOf(DataSourceSupervisorActor.props, DataSourceSupervisorActor.actorName)

  val jdbcConActor = system.actorOf(JDBCConnectionActor.props)

  test("0. Create address") {

    def mockChild(contect: ActorSystem): ActorRef = {
//      contect.actorOf(new TestProbe())
      null
    }

    val userRepository = TestProbe()
    val addressRepository = TestProbe()
    val addressService = system.actorOf(Props(classOf[AddressServiceActor],
      (context: ActorContext) => userRepository.ref,
      (context: ActorContext) => addressRepository.ref))
    //street: String, zip: String, houseNumber: String, city: String
    addressService ! Create(Address(0, "StreetCreate", "zipCreate", "houseNumCreate", "CityCreate"))
    addressRepository.expectMsgType[Insert[Address]]
    addressRepository.reply(Inserted(999))
    expectMsgAnyOf(Created(999))
  }

  test("1. Create address") {
    val jdbcConA = TestProbe()
    val userRepository = Props(new UserRepositoryActor(jdbcConA.ref))
    val addressRepository = Props(new AddressRepositoryActor(jdbcConA.ref))
    val addressService = system.actorOf(AddressServiceActor.props(userRepository, addressRepository))
    //street: String, zip: String, houseNumber: String, city: String
    addressService ! Create(Address(0, "StreetCreate", "zipCreate", "houseNumCreate", "CityCreate"))
    jdbcConA.expectMsgType[JdbcInsert]
    jdbcConA.reply(GeneratedKeyRes(999))
    expectMsgAnyOf(Created(999))
  }

  test("2. delete address") {
    val jdbcConA = TestProbe()
    val userRepository = Props(new UserRepositoryActor(jdbcConA.ref))
    val addressRepository = Props(new AddressRepositoryActor(jdbcConA.ref))
    val addressService = system.actorOf(AddressServiceActor.props(userRepository, addressRepository))
    addressService ! Delete(Address(12, "Street_Find", "zip_Find", "houseNum_Find", "City_Find"))
    jdbcConA.expectMsgType[JdbcDelete]
    jdbcConA.reply(true)
    expectMsgType[Deleted]
  }

  test("3. Find address by all attributes") {
    val jdbcConA = TestProbe()
    val userRepository = Props(new UserRepositoryActor(jdbcConA.ref))
    val addressRepository = Props(new AddressRepositoryActor(jdbcConA.ref))
    val addressService = system.actorOf(AddressServiceActor.props(userRepository, addressRepository))
    addressService ! FindById(0)
    jdbcConA.expectMsgType[JdbcSelect[Address]]
    jdbcConA.reply(JdbcSelectResult[Option[Address]](Some(Address(3, "StreetFind", "zipFind", "houseNumFind", "CityFind"))))
    expectMsgType[Found[Address]]
  }

  test("4. Find or create address - create") {
    val jdbcConA = TestProbe()
    val userRepository = Props(new UserRepositoryActor(jdbcConA.ref))
    val addressRepository = Props(new AddressRepositoryActor(jdbcConA.ref))
    val addressService = system.actorOf(AddressServiceActor.props(userRepository, addressRepository))
    addressService ! FindOrCreateAddress(Address(0, "Street_Find", "zip_Find", "houseNum_Find", "City_Find"))
    jdbcConA.expectMsgType[JdbcSelect[Address]]
//    jdbcConA.reply(JdbcSelectResult[Option[Address]](Some(Address(3, "StreetFind", "zipFind", "houseNumFind", "CityFind"))))
    jdbcConA.reply(JdbcSelectResult(None))
    jdbcConA.expectMsgType[JdbcInsert]
    jdbcConA.reply(GeneratedKeyRes(12))
    val address = Address(12, "Street_Find", "zip_Find", "houseNum_Find", "City_Find")
    jdbcConA.expectMsgType[JdbcSelect[Address]]
    jdbcConA.reply(JdbcSelectResult(Some(address)))
    val addresses = mutable.MutableList(address)
    expectMsgAllOf(Found(addresses))
  }

  test("5. Find or create address - find") {
    val jdbcConA = TestProbe()
    val userRepository = Props(new UserRepositoryActor(jdbcConA.ref))
    val addressRepository = Props(new AddressRepositoryActor(jdbcConA.ref))
    val addressService = system.actorOf(AddressServiceActor.props(userRepository, addressRepository))
    addressService ! FindOrCreateAddress(Address(12, "Street_Find", "zip_Find", "houseNum_Find", "City_Find"))
    jdbcConA.expectMsgType[JdbcSelect[Address]]
    val address = Address(12, "Street_Find", "zip_Find", "houseNum_Find", "City_Find")
    jdbcConA.reply(JdbcSelectResult(Some(address)))
    val addresses = mutable.MutableList(Address(12, "Street_Find", "zip_Find", "houseNum_Find", "City_Find"))
    expectMsgAllOf(Found(addresses))
  }

  test("6. Pagination ") {
    val userRepository = TestProbe()
    val addressRepository = TestProbe()
    val addressService = system.actorOf(Props(classOf[AddressServiceActor],
      (context: ActorContext) => userRepository.ref,
      (context: ActorContext) => addressRepository.ref))

    addressService ! GetAllPagination[Address](2, 2)

    addressRepository.expectMsgType[SelectCount]
    addressRepository.reply(Some[Long](12234l))
    addressRepository.expectMsgType[SelectPaging]
    val resultList = List(Address(12, "Street_Find", "zip_Find", "houseNum_Find", "City_Find"))
    addressRepository.reply(resultList)

    val result = expectMsgType[GetAllPaginationResult[Address]]

    result.result should be(resultList)
  }

  test("7. Real pagination test") {
    val userRepository = system.actorOf(UserRepositoryActor.props(jdbcConActor))
    val addressRepository = system.actorOf(AddressRepositoryActor.props(jdbcConActor))
    val addressService = system.actorOf(Props(classOf[AddressServiceActor],
      (context: ActorContext) => userRepository,
      (context: ActorContext) => addressRepository))
    addressService ! GetAllPagination[Address](1, 16)

    val resultDoc = expectMsgType[GetAllPaginationResult[Address]]
    resultDoc.result.size should not be(0)
  }

 }
