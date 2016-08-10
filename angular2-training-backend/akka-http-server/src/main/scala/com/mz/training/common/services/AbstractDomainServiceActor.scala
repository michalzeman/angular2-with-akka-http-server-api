package com.mz.training.common.services

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern._
import akka.util.Timeout
import com.mz.training.common.messages.UnsupportedOperation
import com.mz.training.common._
import com.mz.training.domains.EntityId

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * Created by zemi on 12/06/16.
  */
abstract class AbstractDomainServiceActor[E <: EntityId](repositoryProps: Props) extends Actor with ActorLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  protected implicit val timeout: Timeout = 2000 milliseconds

  val repository = context.actorOf(repositoryProps)

  override def receive: Receive = {
    case c:Create[E] => create(c.entity) pipeTo sender
    case FindById(id) => findById(id) pipeTo sender
    case d:Delete[E] => delete(d.entity) pipeTo sender
    case u:Update[E]  => update(u.entity) pipeTo sender
    case GetAll => getAll pipeTo sender
    case _ => sender ! UnsupportedOperation
  }

  protected def getAll: Future[Found[E]] = {
    log.info(s"${getClass.getCanonicalName} getAll ->")
    val p = Promise[Found[E]]
    (repository ? repositories.SelectAll).mapTo[List[E]] onComplete {
      case Success(s) => {
        log.info("findUserById - success!")
        p.success(Found(s))
//        s match {
//          case s:Some[E] => p.success(Found[E](List(s.get)))
//          case None => p.success(Found(Nil))
//          case _ => {
//            log.warning("Unsupported message type!")
//            p.failure(new RuntimeException("Unsupported message type"))
//          }
//        }
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
  }

  /**
    * Create
 *
    * @param entity
    * @return
    */
  protected def create(entity: E): Future[Created] = {
    log.info(s"${getClass.getCanonicalName} create ->")
    val p = Promise[Created]
    (repository ? repositories.Insert(entity)).mapTo[repositories.Inserted] onComplete {
      case Success(s) => {
        log.info("createUser - success!")
        p.success(Created(s.id))
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
  }

  /**
    * Find entity by id
    *
    * @param id
    * @return
    */
  protected def findById(id: Long): Future[Found[E]] = {
    log.info(s"${getClass.getCanonicalName} findById ->")
    val p = Promise[Found[E]]
    (repository ? repositories.SelectById(id)).mapTo[Option[E]] onComplete {
      case Success(s) => {
        log.info("findUserById - success!")
        s match {
          case s:Some[E] => p.success(Found[E](List(s.get)))
          case None => p.success(Found(Nil))
          case _ => {
            log.warning("Unsupported message type!")
            p.failure(new RuntimeException("Unsupported message type"))
          }
        }
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
  }

  /**
    * Delete entity
    *
    * @param entity - Entity to delete
    * @return Future[UserDeleted]
    */
  protected def delete(entity: E): Future[Deleted] = {

    val p = Promise[Deleted]
    (repository ? repositories.Delete(entity.id)).mapTo[Boolean] onComplete {
      case Success(success) => {
        log.info("User delete success!")
        p.success(Deleted())
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
  }

  /**
    * Update entity
    *
    * @param entity
    * @return
    */
  protected def update(entity: E): Future[UpdateResult[E]] = {

    val p = Promise[UpdateResult[E]]
    (repository ? repositories.Update(entity)).mapTo[Boolean] onComplete {
      case Success(true) => {
        p.success(Updated(entity))
      }
      case Success(false) => {
        p.success(NotUpdated(entity))
      }
      case Failure(f) => {
        log.error(f, f.getMessage)
        p.failure(f)
      }
    }
    p.future
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug("Actor stop")
    super.postStop()
  }

}
