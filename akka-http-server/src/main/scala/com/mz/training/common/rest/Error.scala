package com.mz.training.common.rest

import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 19/08/16.
  */
case class Error(message: String)

object Error {
  implicit val format = jsonFormat1(Error.apply)
}
