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
      val itemRepositoryProps = ItemRepositoryActor.props(jdbcConActor)
      val itemService = system.actorOf(ItemServiceActor.props(itemRepositoryProps))
      (itemService, jdbcConActor)
    }
  }

  override def getUriPath: String = "items"

  val routes =
    path(getUriPath) {
      cors {
        get {
          complete(getAll)
        }
      }
    } ~ path(getUriPath / IntNumber) { id =>
      cors {
        get {
          complete(getById(id))
        }
      }
    } ~ path(getUriPath / IntNumber) { id =>
      cors {
        put {
            entity(as[Item]) { item => {
              complete(update(item))
            }
          }
        }
      }
    }
}
