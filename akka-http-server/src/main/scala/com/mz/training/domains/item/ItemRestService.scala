package com.mz.training.domains.item

import akka.actor.{ActorRef, ActorSystem}
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.rest.AbstractRestService
import spray.json.RootJsonFormat

import scala.concurrent.Future

/**
  * Created by zemi on 10/08/16.
  */
class ItemRestService(implicit system: ActorSystem) extends AbstractRestService[Item] {

  override def getServiceActor: Future[(ActorRef, ActorRef)] = {
    Future {
      val jdbcConActor = system.actorOf(JDBCConnectionActor.props)
      val itemService = system.actorOf(ItemServiceActor.props(jdbcConActor))
      (itemService, jdbcConActor)
    }
  }

  override def getUriPath: String = "items"

  override protected def getFormat: RootJsonFormat[Item] = Item.format
}
