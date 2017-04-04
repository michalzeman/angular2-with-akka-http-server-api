package com.mz.training.common.rest

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern._
import akka.util.Timeout
import com.mz.training.common.jdbc.JDBCConnectionActor.{Commit, Rollback}
import com.mz.training.common.services._
import com.mz.training.domains.EntityId
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by zemi on 10/08/16.
  */
abstract class AbstractRestService[E <: EntityId](implicit system: ActorSystem) extends RestEndpointRoute
  with DefaultJsonProtocol with SprayJsonSupport {

  protected implicit val timeout: Timeout = 5000 milliseconds

  protected implicit val systemAkka: ActorSystem = system

  protected implicit val executorService: ExecutionContextExecutor = system.dispatcher

  protected implicit val format: RootJsonFormat[E] = getFormat

  protected def getFormat: RootJsonFormat[E]

  def stopActors(actors: ActorRef*): Unit = {
    actors.foreach(actor => actor ! PoisonPill)
  }

  override def buildRoute(): Route = {
    getAll ~
      getById ~
      update ~
      deleteById ~
      create
  }

  protected def create: Route = {
    path(getUriPath) {
      post {
        entity(as[E]) { entity =>
          complete(
            getServiceActor.flatMap(actors => completeAndCleanUpAct {
              (actors._1 ? Create(entity)).mapTo[Created].map(result => Some(result.id.toString))
            }(actors)))
        }
      }
    }
  }

  protected def deleteById: Route = {
    path(getUriPath / LongNumber) { id =>
      delete {
        complete {
          getServiceActor.flatMap(actors => completeAndCleanUpAct {
            (actors._1 ? DeleteById(id)).mapTo[Deleted].map(result => Deteled("Ok"))
          }(actors))
        }
      }
    }
  }

  protected def getById: Route = {
    path(getUriPath / IntNumber) { id =>
      get {
        complete {
          getServiceActor.flatMap(actors => completeAndCleanUpAct {
            (actors._1 ? FindById(id)).mapTo[Found[E]].map(result => {
              result.results match {
                case entity :: xs => {
                  Some(entity)
                }
                case _ => {
                  None
                }
              }
            })
          }(actors))
        }
      }
    }
  }

  protected def update: Route = {
    path(getUriPath / IntNumber) { id =>
      put {
        entity(as[E]) { entity =>
          complete {
            getServiceActor.flatMap(actors => completeAndCleanUpAct {
              (actors._1 ? Update(entity)).mapTo[UpdateResult[E]].map {
                case u: Updated[E] => Some(u.entity)
                case _ => None
              }
            }(actors))
          }
        }
      }
    }
  }

  protected def getAll: Route = {
    path(getUriPath) {
      get {
        parameters('page.?, 'items.?) {
          (page, items) => {
            complete {
              if (page.isDefined || items.isDefined) {
                val pageVal = page match {
                  case Some(value) => value
                  case None => "1"
                }
                val itemsVal = items match {
                  case Some(value) => value
                  case None => "10"
                }
                getAllPagination(pageVal, itemsVal)
              }
              else {
                getAllList
              }
            }
          }
        }
      }
    }
  }

  private def getAllPagination(pageVal: String, itemsVal: String):Future[GetAllPaginationResult[E]] = {
    getServiceActor.flatMap(actors => completeAndCleanUpAct {
      (actors._1 ? GetAllPagination[E](Integer.valueOf(pageVal), Integer.valueOf(itemsVal))).mapTo[GetAllPaginationResult[E]]
    }(actors))
  }

  protected def getAllList:Future[Seq[E]] = {
    getServiceActor.flatMap(actors => completeAndCleanUpAct({
      (actors._1 ? GetAll).mapTo[Found[E]].map(result => result.results)
    })(actors))
  }

  /**
    *
    * @return Tuple of actors where
    *         1. is service actor
    *         2. is JdbcActor represents DB transaction
    */
  def getServiceActor: Future[(ActorRef, ActorRef)]

  def getUriPath: String

  def completeAndCleanUpAct[R](execute: => Future[R], test: Int): Future[R] = {
    execute.andThen {
      case Success(s) => {
        s
      }
      case Failure(e) => {
        e
      }
    }
  }

  def completeAndCleanUpAct[R](execute: => Future[R])(actors: (ActorRef, ActorRef)): Future[R] = {
    execute.andThen {
      case Success(s) => {
        (actors._2 ? Commit).andThen({
          case _ =>
            destroyActors(actors._1, actors._2)
        })
        s
      }
      case Failure(e) => {
        (actors._2 ? Rollback).andThen({
          case _ =>
            destroyActors(actors._1, actors._2)
        })
        e
      }
    }
  }

  def destroyActors(actors: ActorRef*): Unit = {
    actors.foreach(actor => actor ! PoisonPill)
  }
}
