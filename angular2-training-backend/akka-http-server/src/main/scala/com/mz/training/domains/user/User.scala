package com.mz.training.domains.user

import com.mz.training.domains.address.Address
import com.mz.training.domains.EntityId
import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 10/08/16.
  */
case class User(id: Long, firstName: String, lastName: String, addressId: Option[Long], address: Option[Address]) extends EntityId

object User {
  implicit val format = jsonFormat5(User.apply)
}
