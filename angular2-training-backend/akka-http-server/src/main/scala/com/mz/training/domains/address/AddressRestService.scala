package com.mz.training.domains.address

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.rest.AbstractRestService

import scala.concurrent.Future

/**
  * Created by zemi on 10/08/16.
  */
class AddressRestService(implicit system: ActorSystem) extends AbstractRestService[Address] {

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
        get {
          complete(getById(id))
        }
      } ~
      path(getUriPath / IntNumber) { id =>
        put {
          entity(as[Address]) { entity => complete(update(entity)) }
        }
      } ~
      path(getUriPath / LongNumber) { id =>
        delete {
          complete(deleteById(id))
        }
      } ~
      path(getUriPath) {
        post {
          entity(as[Address]) { entity => complete(create(entity)) }
        }
      }

  override def getUriPath: String = "addresses"

  override def buildRoute(): Route = routes
}
