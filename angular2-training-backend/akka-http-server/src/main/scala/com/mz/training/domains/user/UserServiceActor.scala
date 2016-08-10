package com.mz.training.domains.user

import akka.actor.Props
import akka.pattern._
import com.mz.training.common._
import com.mz.training.common.services.AbstractDomainServiceActor
import com.mz.training.domains.address.Address

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
 * Created by zemo on 18/10/15.
 */
class UserServiceActor(userRepProps: Props, addressServiceProps: Props) extends AbstractDomainServiceActor[User](userRepProps) {

  import UserServiceActor._

  val addressService = context.actorOf(addressServiceProps)

  override def receive = userReceive orElse super.receive

  def userReceive: Receive = {
    case RegistrateUser(user, address) => registrateUser(user, address) pipeTo sender
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug("Actor stop")
    super.postStop()
  }

  private def registrateUser(user: User, address: Address): Future[UserRegistrated] = {
    log.info("registrateUser ->")
    val p = Promise[UserRegistrated]
    (addressService ? services.Create(address)).mapTo[services.Created].onComplete {
      case Success(s) =>
        (self ? services.Create(User(0, user.firstName, user.lastName, Some(s.id), None)))
          .mapTo[services.Created].onComplete  {
          case Success(s) => {
            log.debug("registrateUser - success!")
            p.success(UserRegistrated())
          }
          case Failure(f) => {
            log.error(f, f.getMessage)
            p.failure(f)
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

object UserServiceActor {

  case class RegistrateUser(user: User, address: Address)

  case class UserRegistrated()

  /**
   * Create Props
   * @param userRepProps
   * @param addressServiceProps
   * @return Props
   */
  def props(userRepProps: Props, addressServiceProps: Props): Props = Props(classOf[UserServiceActor], userRepProps, addressServiceProps)
}
