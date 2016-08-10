package com.mz.training.common.rest

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Max-Age`}
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol
import akka.util.Timeout
import com.mz.training.common.services._
import com.mz.training.domains.EntityId

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import akka.pattern._
import com.mz.training.common.jdbc.JDBCConnectionActor.{Commit, Rollback}
import com.mz.training.domains.user.User

/**
  * Created by zemi on 10/08/16.
  */
abstract class AbstractRestService[E <: EntityId](system: ActorSystem) extends DefaultJsonProtocol with SprayJsonSupport with CorsSupport {

  protected implicit val timeout: Timeout = 5000 milliseconds

  protected implicit val systemAkka = system

  protected implicit val executorService = system.dispatcher

  def getServiceActor: Future[(ActorRef, ActorRef)]

  def getUriPath: String

  def getAll: Future[Seq[E]] = {
    val p = Promise[Seq[E]]()
    getServiceActor onSuccess { case (service, jdbc) => (service ? GetAll).mapTo[Found[E]] onComplete {
      case Success(s) => {
        jdbc ! Commit
        p.success(s.results)
      }
      case Failure(e) => {
        jdbc ! Rollback
        p.failure(e)
      }
    }
    }
    p.future
  }

  def getById(id: Int): Future[Option[E]] = {
    val p = Promise[Option[E]]()
    getServiceActor onSuccess { case (service, jdbc) => (service ? FindById(id)).mapTo[Found[E]] onComplete {
      case Success(s) => s.results.filter(item => item.id == id) match {
        case result :: xs => {
          jdbc ! Commit
          p.success(Some(result))
        }
        case _ => {
          jdbc ! Commit
          p.success(None)
        }
      }
      case Failure(e) => {
        jdbc ! Rollback
        p.failure(e)
      }
    }
    }
    p.future
  }

  def update(entity: E): Future[Option[E]] = {
    val p = Promise[Option[E]]()
    getServiceActor onSuccess { case (service, jdbc) => (service ? Update(entity)).mapTo[UpdateResult[E]] onComplete {
      case Success(s) => s match {
        case u: Updated[E] => {
          jdbc ! Commit
          p.success(Some(u.entity))
        }
        case _ => p.success(None)
      }
      case Failure(e) => p.failure(e)
    }
    }
    p.future
  }

  override val corsAllowOrigins: List[String] = List("*")

  override val corsAllowedHeaders: List[String] = List("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

  override val corsAllowCredentials: Boolean = true

  override val optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
    `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20), // cache pre-flight response for 20 days
    `Access-Control-Allow-Credentials`(corsAllowCredentials)
  )
}
