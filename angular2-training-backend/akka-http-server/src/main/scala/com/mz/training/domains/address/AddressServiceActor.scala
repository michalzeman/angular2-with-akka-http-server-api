package com.mz.training.domains.address

import akka.actor.Props
import akka.pattern._
import com.mz.training.common._
import com.mz.training.common.services.AbstractDomainServiceActor
import com.mz.training.domains.address.AddressServiceActor.FindOrCreateAddress

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by zemi on 23. 10. 2015.
 */
class AddressServiceActor(userRepProps: Props, addressRepProps: Props) extends AbstractDomainServiceActor[Address](addressRepProps)  {


  val userRepository = context.actorOf(userRepProps)

  override def receive: Receive = addressServiceReceive orElse super.receive

  def addressServiceReceive: Receive = {
    case FindOrCreateAddress(address) => findOrCreate(address) pipeTo sender
  }

  private def findOrCreate(address: Address): Future[services.Found[Address]] = {
    log.debug("findOrCreate")
    (self ? services.FindById(address.id)).mapTo[services.Found[Address]].flatMap(result => {
      log.debug("findOrCreate - success!")
      if (!result.results.isEmpty) Future(result)
      else (self ? services.Create(address)).mapTo[services.Created].flatMap(result =>
        (self ? services.FindById(result.id)).mapTo[services.Found[Address]]
      )
    })
  }
}

object AddressServiceActor {

  case class FindOrCreateAddress(address: Address)

  /**
  * Create Props
  * @param userRepProps
  * @param addressRepProps
  * @return Props
  */
  def props(userRepProps: Props, addressRepProps: Props): Props = Props(classOf[AddressServiceActor], userRepProps, addressRepProps)
}
