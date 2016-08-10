package com.mz.training.domains.address

import akka.actor.Props
import akka.pattern._
import com.mz.training.common._
import com.mz.training.common.services.AbstractDomainServiceActor
import com.mz.training.domains.address.Address
import com.mz.training.domains.address.AddressServiceActor.FindOrCreateAddress

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

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
    val p = Promise[services.Found[Address]]
    (self ? services.FindById(address.id)).mapTo[services.Found[Address]] onComplete {
      case Success(s) => {
        log.debug("findOrCreate - success!")
        if (!s.results.isEmpty) p.success(s)
        else (self ? services.Create(address)).mapTo[services.Created] onComplete {
          case Success(s) => {
            //street: String, zip: String, houseNumber: String, city: String
//            p.success(Found(List(Address(s.id, address.street, address.zip, address.houseNumber, address.city))))
            (self ? services.FindById(s.id)).mapTo[services.Found[Address]] onComplete {
              case Success(result) => {
                p.success(result)
              }
              case Failure(f) => {
                log.error(f, f.getMessage)
                p.failure(f)
              }
            }
          }
          case Failure(f) => {
            log.error(f, f.getMessage)
            p.failure(f)
          }
        }
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
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
