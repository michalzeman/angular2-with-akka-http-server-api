package com.mz.training.common.services

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}
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
abstract class AbstractDomainServiceActor[E <: EntityId](repositoryBuilder:(ActorContext) => ActorRef) extends Actor with ActorLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  protected implicit val timeout: Timeout = 2000 milliseconds

  val repository = repositoryBuilder(context)

  override def receive: Receive = {
    case c:Create[E] => create(c.entity) pipeTo sender
    case FindById(id) => findById(id) pipeTo sender
    case d:Delete[E] => delete(d.entity) pipeTo sender
    case DeleteById(id) => delete(id) pipeTo sender
    case u:Update[E]  => update(u.entity) pipeTo sender
    case GetAll => getAll pipeTo sender
    case _ => sender ! UnsupportedOperation
  }

  /**
    * List all entities from DB
    * TODO: add pagination
    * @return
    */
  protected def getAll: Future[Found[E]] = {
    log.info(s"${getClass.getCanonicalName} getAll ->")
    (repository ? repositories.SelectAll).mapTo[List[E]].map(result => {
      log.info("findUserById - success!")
      Found(result)
    })
  }

  /**
    * Create
    *
    * @param entity
    * @return
    */
  protected def create(entity: E): Future[Created] = {
    log.info(s"${getClass.getCanonicalName} create ->")
    (repository ? repositories.Insert(entity)).mapTo[repositories.Inserted].map(result => {
      log.info("createUser - success!")
      Created(result.id)
    })
  }

  /**
    * Find entity by id
    *
    * @param id
    * @return
    */
  protected def findById(id: Long): Future[Found[E]] = {
    log.info(s"${getClass.getCanonicalName} findById ->")
    (repository ? repositories.SelectById(id)).mapTo[Option[E]].map(result => {
      log.info("findUserById - success!")
      result match {
        case s:Some[E] => Found[E](List(s.get))
        case None => Found(Nil)
      }
    })
  }

  /**
    * Delete entity
    *
    * @param entity - Entity to delete
    * @return Future[UserDeleted]
    */
  protected def delete(entity: E): Future[Deleted] = {
    log.info(s"${getClass.getCanonicalName} delete ->")
    (repository ? repositories.Delete(entity.id)).mapTo[Boolean].map(result => {
      log.info("User delete success!")
      Deleted()
    })
  }

  /**
    * Delete by id
    * @param id - id of entity
    * @return
    */
  protected def delete(id: Long): Future[Deleted] = {
    log.info(s"${getClass.getCanonicalName} delete ->")
    (repository ? repositories.Delete(id)).mapTo[Boolean].map(result => {
      log.info("User delete success!")
      Deleted()
    })
  }

  /**
    * Update entity
    *
    * @param entity
    * @return
    */
  protected def update(entity: E): Future[UpdateResult[E]] = {
    log.info(s"${getClass.getCanonicalName} update ->")
    (repository ? repositories.Update(entity)).mapTo[Boolean].map(result => {
      if (result) Updated(entity)
      else NotUpdated(entity)
    })
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug("Actor stop")
    super.postStop()
  }

}
