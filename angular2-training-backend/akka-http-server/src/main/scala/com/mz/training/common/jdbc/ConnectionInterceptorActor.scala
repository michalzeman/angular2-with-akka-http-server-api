package com.mz.training.common.jdbc

import akka.actor._
import com.mz.training.common.jdbc.DataSourceActor.{GetConnection}

import scala.concurrent.duration._

case class ReceiveTimeoutInterceptor(orgSender: ActorRef)

case object ActorStop

/**
 * Created by zemi on 4. 11. 2015.
 */
class ConnectionInterceptorActor extends Actor with ActorLogging {

  import context.dispatcher

  val timeout = 1.5 seconds

  override def receive: Receive = {
    case GetConnection => {
      log.debug("GetConnection intercepting start")
      context.system.scheduler.scheduleOnce(timeout, self, ReceiveTimeoutInterceptor(sender))
    }
    case ReceiveTimeoutInterceptor(orgSender) => {
      log.debug("GetConnection intercepting timeout -> going to kill JDBCConnectionActor")
      orgSender ! PoisonPill
    }
    case ActorStop => {
      log.debug("ConnectionInterceptorActor -> going to kill self!")
      context.stop(self)
//      self ! PoisonPill
    }
  }

}

object ConnectionInterceptorActor {
  def props: Props = Props[ConnectionInterceptorActor]
}
