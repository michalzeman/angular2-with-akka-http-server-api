package com.mz.training.common.services.pagination

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.mz.training.common.repositories.{SelectCount, SelectPaging}
import com.mz.training.common.services.{GetAllPagination, GetAllPaginationResult}
import com.mz.training.domains.EntityId

/**
  * Created by zemi on 28/12/2016.
  */
class GetAllPaginationActor[E <: EntityId](repository: ActorRef) extends Actor with ActorLogging {

  var senderAct: Option[ActorRef] = None

  var msg: Option[GetAllPagination[E]] = None

  var offset: Option[Int] = None

  var itemsPerPage: Option[Int] = None

  var size: Option[Long] = None

  var result: List[E] = Nil

  override def receive: Receive = {
    case msg: GetAllPagination[E] => startProcess(msg)
    case msg: List[E] => processSelectResult(msg)
    case msg: Option[Long] => processCount(msg)
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
    processResult
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
    processResult
  }

  /**
    * Start selecting process
    *
    * @param msg
    */
  private def startProcess(msg: GetAllPagination[E]): Unit = {
    senderAct = Some(sender)
    this.msg = Some(msg)
    offset = if (msg.page > 0) Some((msg.page - 1) * getSizeParPage(msg.sizePerPage)) else Some(0)
    itemsPerPage = Some(getSizeParPage(msg.sizePerPage))
    repository ! SelectCount()
    repository ! SelectPaging(offset.get, itemsPerPage.get)
  }

  /**
    * process result if all params are collected, result is sent back to the sender actor
    */
  private def processResult: Unit = {
    result match {
      case Nil => Unit
      case list:List[E] => size.foreach(sz => msg.foreach(msg =>
        senderAct.foreach(actor => actor ! GetAllPaginationResult[E](msg.page, itemsPerPage.get, sz, list))
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
