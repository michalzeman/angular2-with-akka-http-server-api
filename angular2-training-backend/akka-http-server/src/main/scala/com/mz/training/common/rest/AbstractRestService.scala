package com.mz.training.common.rest

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Max-Age`}
import spray.json.DefaultJsonProtocol
import akka.util.Timeout
import com.mz.training.common.services._
import com.mz.training.domains.EntityId

import scala.concurrent.{Future}
import scala.concurrent.duration._
import akka.pattern._
import com.mz.training.common.jdbc.JDBCConnectionActor.{Commit}

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
    getServiceActor.flatMap(actors => (actors._1 ? GetAll).mapTo[Found[E]].map(result => {
      actors._2 ! Commit
      result.results
    }))
  }

  def getById(id: Int): Future[Option[E]] = {
    getServiceActor.flatMap(actors => (actors._1 ? FindById(id)).mapTo[Found[E]].map(result => {
      actors._2 ! Commit
      result.results match {
        case result :: xs => {
          Some(result)
        }
        case _ => {
          None
        }
      }
    }))
  }

  def update(entity: E): Future[Option[E]] = {
    getServiceActor.flatMap(actors => (actors._1 ? Update(entity)).mapTo[UpdateResult[E]].map(result => {
      actors._2 ! Commit
      result match {
        case u: Updated[E] => {
          Some(u.entity)
        }
        case _ => {
          None
        }
      }
    }))
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
