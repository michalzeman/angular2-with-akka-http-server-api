package com.mz.training.domains.item

import akka.actor.Props
import com.mz.training.common.services.AbstractDomainServiceActor

/**
  * Created by zemi on 10/08/16.
  */
class ItemServiceActor(itemRepositoryProps: Props) extends AbstractDomainServiceActor[Item](itemRepositoryProps) {

}

object ItemServiceActor {
  def props(itemRepositoryProps: Props): Props = Props(classOf[ItemServiceActor], itemRepositoryProps)
}
