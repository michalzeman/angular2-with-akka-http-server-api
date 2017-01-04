package com.mz.training.common

import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 12/08/16.
  */
package object rest {

  case class HttpSuccess(message: String)

  object HttpSuccess {
    implicit val format = jsonFormat1(Deteled.apply)
  }
  case class Deteled(nessage: String)

  object Deteled {
    implicit val format = jsonFormat1(Deteled.apply)
  }

}
