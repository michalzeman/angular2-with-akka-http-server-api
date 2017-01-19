package com.mz.training.common.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpResponse}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Max-Age`}
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshalling.Marshal

/**
  * Created by zemi on 18/08/16.
  */
trait RestEndpointRoute extends CorsSupport with DefaultJsonProtocol with SprayJsonSupport{

  def buildRoute(): Route

  val myExceptionHandler = ExceptionHandler {
    case exc: Exception =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(InternalServerError, entity = exc.getMessage))
      }
  }

  def getRoute(): Route = {
//    cors {
//      handleExceptions(myExceptionHandler) {
//        buildRoute()
//      }
//    }
    buildRoute()
  }

}
