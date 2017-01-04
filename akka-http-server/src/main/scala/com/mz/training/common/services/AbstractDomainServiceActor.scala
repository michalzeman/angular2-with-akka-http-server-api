package com.mz.training.common.services

import java.util.UUID

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, PoisonPill, Props}
import akka.pattern._
import akka.util.Timeout
import com.mz.training.common.messages.UnsupportedOperation
import com.mz.training.common._
import com.mz.training.common.services.pagination.GetAllPaginationActor
import com.mz.training.domains.EntityId

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * Created by zemi on 12/06/16.
  */
abstract class AbstractDomainServiceActor[E <: EntityId](repositoryBuilder:(ActorContext) => ActorRef)
  extends Actor with ActorLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  protected implicit val timeout: Timeout = 2000 milliseconds

  val repository = repositoryBuilder(context)

  override def receive: Receive = {
    case c:Create[E] => create(c.entity) pipeTo sender
    case FindById(id) => findById(id) pipeTo sender
    case d:Delete[E] => delete(d.entity) pipeTo sender
    case DeleteById(id) => delete(id) pipeTo sender
    case u:Update[E]  => update(u.entity) pipeTo sender
    case msg:GetAllPagination[E]  => getAllPagination(msg) pipeTo sender
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
    * List all entities from DB pagination
    * TODO: add pagination
    * @return
    */
  protected def getAllPagination(msg:GetAllPagination[E]): Future[GetAllPaginationResult[E]] = {
    log.info(s"${getClass.getCanonicalName} getAllPagination ->")
    val actRef = Future {
      context.actorOf(GetAllPaginationActor.props[E](repository),
        s"GetAllPaginationActor-${UUID.randomUUID.toString}")
    }
    actRef.flatMap(getAllPagActRef => executeAndCleanUpAct(getAllPagActRef ? msg)(getAllPagActRef)
      .mapTo[GetAllPaginationResult[E]])
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

  /**
    * execute action and clean up actor
    * @param execute - execution function
    * @param actor - actor to terminate after execution
    * @tparam R - Type of result
    * @return Future of execution
    */
  protected def executeAndCleanUpAct[R](execute: => Future[R])(actor: ActorRef): Future[R] = {
    execute.andThen {
      case Success(s) => {
        destroyActors(Some(actor))
        s
      }
      case Failure(e) => {
        destroyActors(Some(actor))
        e
      }
    }
  }

  /**
    * send message PoinsonPill to the actor
    * @param actor - ActorRef to be terminated
    */
  def destroyActors(actor: Option[ActorRef]): Unit = {
    actor.foreach(actor => actor ! PoisonPill)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug("Actor stop")
    super.postStop()
  }

}
