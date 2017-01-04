package com.mz.training.common

import com.mz.training.domains.EntityId


/**
  * Created by zemo on 12/10/15.
  */

package object repositories {

  sealed trait Message {
  }

  case class Inserted(id: Long) extends Message

  case class SelectById(id: Long) extends Message

  case object SelectAll

  case class Update[E <: EntityId](entity: E) extends Message

  case class Delete(id: Long) extends Message

  case class Insert[E <: EntityId](entity: E) extends Message

}
