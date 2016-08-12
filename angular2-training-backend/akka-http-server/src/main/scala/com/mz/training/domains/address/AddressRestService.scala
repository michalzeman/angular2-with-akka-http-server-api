package com.mz.training.domains.address

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.rest.AbstractRestService
import com.mz.training.domains.user.{User, UserRepositoryActor, UserServiceActor}

import scala.concurrent.Future

/**
  * Created by zemi on 10/08/16.
  */
class AddressRestService(system: ActorSystem) extends AbstractRestService[Address](system) {

  override def getServiceActor: Future[(ActorRef, ActorRef)] = {
    Future {
      val jdbcConActor = system.actorOf(JDBCConnectionActor.props)
      val addressService = system.actorOf(AddressServiceActor.props(jdbcConActor))
      (addressService, jdbcConActor)
    }
  }

  val routes =
    path(getUriPath) {
      get {
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

  override def getUriPath: String = "addresses"
}
