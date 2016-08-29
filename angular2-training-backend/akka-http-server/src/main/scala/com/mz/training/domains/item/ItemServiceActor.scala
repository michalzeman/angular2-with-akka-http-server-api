package com.mz.training.domains.item

import akka.actor.{ActorContext, ActorRef, Props}
import com.mz.training.common.services.AbstractDomainServiceActor

/**
  * Created by zemi on 10/08/16.
  */
class ItemServiceActor(itemRepositoryBuilder: (ActorContext) => ActorRef) extends AbstractDomainServiceActor[Item](itemRepositoryBuilder) {

}

object ItemServiceActor {

  def props(itemRepositoryProps: Props): Props = Props(classOf[ItemServiceActor], (context: ActorContext) => context.actorOf(itemRepositoryProps))

  def props(jdbcConActor: ActorRef): Props = {
    val itemRepositoryProps = ItemRepositoryActor.props(jdbcConActor)
    props(itemRepositoryProps)
  }
}
