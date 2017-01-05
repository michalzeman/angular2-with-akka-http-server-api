package com.mz.training.common.services.pagination

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.mz.training.common.repositories.{SelectCount, SelectPaging}
import com.mz.training.common.services.{GetAllPagination, GetAllPaginationResult}
import com.mz.training.domains.EntityId

import scala.concurrent.duration._


case object TimeOutMsg

/**
  * Created by zemi on 28/12/2016.
  */
class GetAllPaginationActor[E <: EntityId](repository: ActorRef) extends Actor with ActorLogging {

  protected implicit val executorService = context.dispatcher

  val timeout = 1.7 seconds

  var senderAct: Option[ActorRef] = None

  var msg: Option[GetAllPagination[E]] = None

  var size: Option[Long] = None

  var result: List[E] = Nil

  override def receive: Receive = {
    case msg: GetAllPagination[E] => startProcess(msg)
    case msg: List[E] => processSelectResult(msg)
    case msg: Option[Long] => processCount(msg)
    case TimeOutMsg => {
      log.warning(s"${getClass.getCanonicalName} -> Timeout reached!")
      size = Some(0)
      result = List()
      msg.foreach(msg =>
        senderAct.foreach(actor => {
          log.debug(s"${getClass.getCanonicalName} -> processResult going to return result")
          actor ! GetAllPaginationResult[E](msg.page, msg.sizePerPage, 0, result)
        }))
      context.become(processed)
    }
    case _: Any => log.error("Unsupported operation!!!")
  }

  /**
    * GetAllPagination is in the processed state
    *
    * @return
    */
  def processed: Receive = {
    case _: Any => log.error("Unsupported operation!!!")
  }

  /**
    * handle result of selecting
    *
    * @param msg - result
    */
  private def processSelectResult(msg: List[E]): Unit = {
    log.info(s"${getClass.getCanonicalName}:processResult(msg) ->")
    result = msg
    processResult(result, size)
  }

  /**
    * handle count result
    *
    * @param msg
    */
  private def processCount(msg: Option[Long]): Unit = {
    log.info(s"${getClass.getCanonicalName}:processCount() -> msg: ${msg.toString}")
    msg match {
      case s: Some[Long] => size = s
      case None => size = Some(0)
    }
    processResult(result, size)
  }

  /**
    * Start selecting process
    *
    * @param msg
    */
  private def startProcess(msg: GetAllPagination[E]): Unit = {
    senderAct = Some(sender)
    this.msg = Some(msg)
    val offset = if (msg.page > 0) (msg.page - 1) * getSizeParPage(msg.sizePerPage) else 0
    val itemsPerPage = getSizeParPage(msg.sizePerPage)
    repository ! SelectCount()
    repository ! SelectPaging(offset, itemsPerPage)
    context.system.scheduler.scheduleOnce(timeout, self, TimeOutMsg)
  }

  /**
    * process result if all params are collected, result is sent back to the sender actor
    */
  private def processResult(result: List[E], size: Option[Long]): Unit = {
    if (result.nonEmpty) {
      size.foreach(sz => msg.foreach(msg =>
        senderAct.foreach(actor => {
          log.debug(s"${getClass.getCanonicalName} -> processResult going to return result")
          actor ! GetAllPaginationResult[E](msg.page, msg.sizePerPage, sz, result)
          context.become(processed)
        })
      ))
    }
  }

  /**
    * resolve sizePerPage. If is size negative or zero it is returned 1
    *
    * @param size
    * @return
    */
  private def getSizeParPage(size: Int): Int = if (size <= 0) 1 else size
}

object GetAllPaginationActor {
  def props[E](repository: ActorRef): Props = Props(classOf[GetAllPaginationActor[E]], repository)
}
