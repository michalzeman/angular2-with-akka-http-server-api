package com.mz.training.domains.user

import akka.actor.{ActorContext, ActorRef, Props}
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
class UserServiceActor(userRepBuilder: (ActorContext) => ActorRef, addressServiceBuilder: (ActorContext) => ActorRef) extends AbstractDomainServiceActor[User](userRepBuilder) {

  import UserServiceActor._

  val addressService = addressServiceBuilder(context)

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

    val createAddress = (addressService ? services.Create(address)).mapTo[services.Created]
    val createUser = createAddress.flatMap(address => {
      log.debug(s"${this.getClass.getCanonicalName}.registrateUser -> address created!")
      (self ? services.Create(User(-1, user.firstName, user.lastName, Some(address.id), None)))
        .mapTo[services.Created]
    })
    createUser.map(userCreated => {
      log.debug(s"${this.getClass.getCanonicalName}.registrateUser -> user created!")
      UserRegistrated()
    })
  }
}

object UserServiceActor {

  case class RegistrateUser(user: User, address: Address)

  case class UserRegistrated()

  /**
    * Create Props
    *
    * @param userRepProps
    * @param addressServiceProps
    * @return Props
    */
  def props(userRepProps: Props, addressServiceProps: Props): Props = Props(classOf[UserServiceActor],
    (context: ActorContext) => context.actorOf(userRepProps),
    (context: ActorContext) => context.actorOf(addressServiceProps))
}
