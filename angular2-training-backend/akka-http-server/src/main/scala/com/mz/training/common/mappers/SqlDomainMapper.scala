package com.mz.training.common.mappers

import java.sql.ResultSet

import com.mz.training.domains.EntityId

/**
 * Created by zemo on 11/10/15.
 */
trait SqlDomainMapper[E <: EntityId] {

  val ID_COL = "id"

  def sqlProjection: String

  def tableName: String

  def columns: String

  def setValues(implicit entity: E): String

  def values(implicit entity: E): String

  /**
   * Map ResultSet to Domain object
   * @param resultSet
   * @return Some[E] or None
   */
  def mapResultSet(resultSet: ResultSet): Option[E] = {
    if (resultSet.next()) {
      Some(mapResultSetDomain(resultSet))
    } else None
  }

  /**
   * Map ResultSet to list
   * @param resultSet
   * @return List[E]
   */
  def mapResultSetList(resultSet: ResultSet): Seq[E] = {
    val list = scala.collection.mutable.MutableList.empty[E]
    while(resultSet.next()) {
      list += mapResultSetDomain(resultSet)
    }
    list
  }

  /**
   * Map ResultSet to Domain object
   * @param resultSet
   * @return
   */
  def mapResultSetDomain(resultSet: ResultSet): E

}
