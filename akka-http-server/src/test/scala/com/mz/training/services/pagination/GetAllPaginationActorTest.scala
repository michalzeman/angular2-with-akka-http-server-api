package com.mz.training.services.pagination

import com.mz.training.AbstractActorTest
import com.mz.training.common.services.{GetAllPagination, GetAllPaginationResult}
import com.mz.training.common.services.pagination.GetAllPaginationActor
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

  test("GetAllPagination OK") {
    val userRepository = system.actorOf(UserRepositoryActor.props(jdbcConActor))
    val actStub = system.actorOf(GetAllPaginationActor.props[User](userRepository))
    actStub ! GetAllPagination[User](1, 6)
    expectMsgType[GetAllPaginationResult[User]]
  }

}
