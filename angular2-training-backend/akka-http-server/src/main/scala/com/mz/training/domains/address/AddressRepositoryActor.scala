package com.mz.training.domains.address

import akka.actor.{ActorRef, Props}
import com.mz.training.common.repositories.AbstractRepositoryActor
import com.mz.training.domains.address.Address


/**
 * Created by zemo on 17/10/15.
 */
class AddressRepositoryActor(jdbcActor: ActorRef)
  extends AbstractRepositoryActor[Address](jdbcActor) with AddressMapper {

}

object AddressRepositoryActor {

  /**
   * Create Props for an actor of this type
   * @return a Props
   */
  def props(jdbcConRef: ActorRef): Props = Props(classOf[AddressRepositoryActor], jdbcConRef)
}
