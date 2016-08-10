package com.mz.training.domains.address

import com.mz.training.domains.EntityId
import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 10/08/16.
  */
case class Address(id: Long, street: String, zip: String, houseNumber: String, city: String) extends EntityId

object Address {
  implicit val format = jsonFormat5(Address.apply)
}
