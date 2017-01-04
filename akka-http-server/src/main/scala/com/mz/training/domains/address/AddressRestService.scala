package com.mz.training.domains.address

import akka.actor.{ActorRef, ActorSystem}
import com.mz.training.common.jdbc.JDBCConnectionActor
import com.mz.training.common.rest.AbstractRestService
import spray.json.RootJsonFormat

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

  override def getUriPath: String = "addresses"

  override protected def getFormat: RootJsonFormat[Address] = Address.format
}
