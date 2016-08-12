package com.mz.training.common

import com.mz.training.domains.EntityId


/**
  * Created by zemi on 12/06/16.
  */
package object services {

  case class Create[E <: EntityId](entity: E)

  case class Created(id: Long)

  case class FindById(id: Long)

  case class Found[E <: EntityId](results: Seq[E])

  case class Delete[E <: EntityId](entity: E)

  case class DeleteById(id: Long)

  case class Deleted()

  case class GetAll[E <: EntityId]()

  case class Update[E <: EntityId](entity: E)

  trait UpdateResult[E <: EntityId]

  case class Updated[E <: EntityId](entity:E) extends UpdateResult[E]

  case class NotUpdated[E <: EntityId](entity:E) extends UpdateResult[E]

}
