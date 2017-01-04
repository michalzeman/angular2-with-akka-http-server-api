package com.mz.training.common

import akka.actor.ActorRef

/**
 * Created by zemi on 2. 10. 2015.
 */
package object messages {
  /**
   * Message for unsupported operation
   */
  case object UnsupportedOperation

  /**
   *
   * @param message - Org message
   * @param senderOrg
   */
  case class RetryOperation(message: Any, senderOrg: ActorRef)

  /**
   * Generic message for operation done
   */
  case object OperationDone
}
