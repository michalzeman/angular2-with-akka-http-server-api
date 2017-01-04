package com.mz.training.domains.user

import akka.actor.{ActorRef, ActorSystem}
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.rest.AbstractRestService
import com.mz.training.domains.address.{AddressRepositoryActor, AddressServiceActor}
import spray.json.RootJsonFormat

import scala.concurrent.Future

class UserRestService(implicit system: ActorSystem) extends AbstractRestService[User] {

  def getServiceActor: Future[(ActorRef, ActorRef)] = {
    Future {
      val jdbcConActor = system.actorOf(JDBCConnectionActor.props)
      val userRepositoryProps = UserRepositoryActor.props(jdbcConActor)
      val addressRepositoryProps = AddressRepositoryActor.props(jdbcConActor)
      val addressService = AddressServiceActor.props(userRepositoryProps, addressRepositoryProps)
      val userService = system.actorOf(UserServiceActor.props(userRepositoryProps, addressService))
      (userService, jdbcConActor)
    }
  }

  override def getUriPath: String = "users"

  override protected def getFormat: RootJsonFormat[User] = User.format
}
