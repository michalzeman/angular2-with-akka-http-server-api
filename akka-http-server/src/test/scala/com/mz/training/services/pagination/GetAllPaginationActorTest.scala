package com.mz.training.services.pagination

import com.mz.training.AbstractActorTest
import com.mz.training.common.services.{GetAllPagination, GetAllPaginationResult}
import com.mz.training.common.services.pagination.GetAllPaginationActor
import com.mz.training.domains.address.{Address, AddressRepositoryActor}
import com.mz.training.domains.user.{User, UserRepositoryActor}

/**
  * Created by zemi on 28/12/2016.
  */
class GetAllPaginationActorTest extends AbstractActorTest("test-jdbc-demo-GetAllPaginationActorTest") {

  test("GetAllPagination OK -> page=0") {
    val userRepository = system.actorOf(UserRepositoryActor.props(jdbcConActor))
    val actStub = system.actorOf(GetAllPaginationActor.props[User](userRepository))
    actStub ! GetAllPagination[User](0, 6)
    expectMsgType[GetAllPaginationResult[User]]
  }


  test("GetAllPagination OK -> page=-1") {
    val userRepository = system.actorOf(UserRepositoryActor.props(jdbcConActor))
    val actStub = system.actorOf(GetAllPaginationActor.props[User](userRepository))
    actStub ! GetAllPagination[User](-1, -6)
    expectMsgType[GetAllPaginationResult[User]]
  }

  test("GetAllPagination user OK") {
    val userRepository = system.actorOf(UserRepositoryActor.props(jdbcConActor))
    val actStub = system.actorOf(GetAllPaginationActor.props[User](userRepository))
    actStub ! GetAllPagination[User](1, 6)
    val result = expectMsgType[GetAllPaginationResult[User]]
    result.result.size should be(6)
  }

  test("GetAllPagination address OK") {
    val addressRepository = system.actorOf(AddressRepositoryActor.props(jdbcConActor))
    val actStub = system.actorOf(GetAllPaginationActor.props[Address](addressRepository))
    actStub ! GetAllPagination[Address](1, 6)
    val result = expectMsgType[GetAllPaginationResult[Address]]
    result.result.size should be(6)
  }

}
