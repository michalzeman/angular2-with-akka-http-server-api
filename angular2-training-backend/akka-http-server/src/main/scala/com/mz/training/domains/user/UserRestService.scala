package com.mz.training.domains.user

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.domains.address.{AddressRepositoryActor, AddressServiceActor}
import com.mz.training.common.rest.AbstractRestService
import spray.json.DefaultJsonProtocol

import scala.concurrent.{Future, Promise}

//@Named
class UserRestService(system: ActorSystem) extends AbstractRestService[User](system) with DefaultJsonProtocol with SprayJsonSupport {

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

  val userRoutes =
    path(getUriPath) {
      get {
        //        complete(userRepository.getUsers)
        complete(getAll)
      }
    } ~
      path(getUriPath / IntNumber) { id =>
        complete(getById(id))
      } ~
      path(getUriPath) {
        post {
          entity(as[User]) { user => {
            println("User: " + user)
            complete(null)
          }
          }
        }
      }

  override def getUriPath: String = "users"
}
