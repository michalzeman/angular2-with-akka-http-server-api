package com.mz.training.domains.item

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.rest.AbstractRestService

import scala.concurrent.Future

/**
  * Created by zemi on 10/08/16.
  */
class ItemRestService(system: ActorSystem) extends AbstractRestService[Item](system) {

  override def getServiceActor: Future[(ActorRef, ActorRef)] = {
    Future {
      val jdbcConActor = system.actorOf(JDBCConnectionActor.props)
      val itemService = system.actorOf(ItemServiceActor.props(jdbcConActor))
      (itemService, jdbcConActor)
    }
  }

  override def getUriPath: String = "items"

  val routes =
    cors {
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
            entity(as[Item]) { item => complete(update(item))}
          }
        } ~
        path(getUriPath / LongNumber) { id =>
          delete {
            complete(deleteById(id))
          }
        } ~
      path(getUriPath) {
        post {
          entity(as[Item]) {item => complete(create(item))}
        }
      }
    }
}
