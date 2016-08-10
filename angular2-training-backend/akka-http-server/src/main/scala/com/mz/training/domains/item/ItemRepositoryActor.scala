package com.mz.training.domains.item

import akka.actor.{ActorRef, Props}
import com.mz.training.common.repositories.AbstractRepositoryActor

/**
  * Created by zemi on 10/08/16.
  */
class ItemRepositoryActor(jdbcActor: ActorRef) extends AbstractRepositoryActor[Item](jdbcActor) with ItemMapper {

}

object ItemRepositoryActor {

  /**
    * Create Props for an actor of this type
    * @return a Props
    */
  def props(jdbcConRef: ActorRef): Props = Props(classOf[ItemRepositoryActor], jdbcConRef)
}
