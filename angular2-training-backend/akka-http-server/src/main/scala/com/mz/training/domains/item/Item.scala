package com.mz.training.domains.item

import com.mz.training.domains.EntityId
import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 10/08/16.
  */
case class Item(id:Long, name:String, description: String) extends EntityId

object Item {
  implicit val format = jsonFormat3(Item.apply)
}
