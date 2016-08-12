package com.mz.training.common.rest

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpHeader, HttpMethods}
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Max-Age`}
import spray.json.DefaultJsonProtocol
import akka.util.Timeout
import com.mz.training.common.services._
import com.mz.training.domains.EntityId

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern._
import com.mz.training.common.jdbc.JDBCConnectionActor.{Commit, Rollback}

import scala.util.{Failure, Success}

/**
  * Created by zemi on 10/08/16.
  */
abstract class AbstractRestService[E <: EntityId](system: ActorSystem) extends DefaultJsonProtocol with SprayJsonSupport with CorsSupport {

  protected implicit val timeout: Timeout = 5000 milliseconds

  protected implicit val systemAkka = system

  protected implicit val executorService = system.dispatcher

  def stopActors(actors: ActorRef*): Unit = {
    actors.foreach(actor => actor ! PoisonPill)
  }

  /**
    *
    * @return Tuple of actors where
    *         1. is service actor
    *         2. is JdbcActor represents DB transaction
    */
  def getServiceActor: Future[(ActorRef, ActorRef)]

  def getUriPath: String

  def getAll: Future[Seq[E]] = {
    getServiceActor.flatMap(actors => completeAndCleanUpAct {
      (actors._1 ? GetAll).mapTo[Found[E]].map(result => result.results)
    }(actors))
  }

  def getById(id: Int): Future[Option[E]] = {
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

  def update(entity: E): Future[Option[E]] = {
    getServiceActor.flatMap(actors => completeAndCleanUpAct {
      (actors._1 ? Update(entity)).mapTo[UpdateResult[E]].map {
        case u: Updated[E] => {
          Some(u.entity)
        }
        case _ => {
          None
        }
      }
    }(actors))
  }

  def create(entity: E): Future[Option[String]] = {
    getServiceActor.flatMap(actors => completeAndCleanUpAct {
      (actors._1 ? Create(entity)).mapTo[Created].map(result => Some(result.id.toString))
    }(actors))
  }

  def deleteById(id: Long): Future[Deteled] = {
    getServiceActor.flatMap(actors => completeAndCleanUpAct {
      (actors._1 ? DeleteById(id)).mapTo[Deleted].map(result => Deteled("Ok"))
    } (actors))
  }

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

  override val corsAllowOrigins: List[String] = List("*")

  override val corsAllowedHeaders: List[String] = List("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

  override val corsAllowCredentials: Boolean = true

  override val optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
    `Access-Control-Allow-Methods`(HttpMethods.GET, HttpMethods.PUT, HttpMethods.POST, HttpMethods.DELETE),
    `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20), // cache pre-flight response for 20 days
    `Access-Control-Allow-Credentials`(corsAllowCredentials)
  )
}
