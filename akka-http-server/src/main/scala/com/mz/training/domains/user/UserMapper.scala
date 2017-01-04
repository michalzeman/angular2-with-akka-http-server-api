package com.mz.training.domains.user

import java.sql.ResultSet

import com.mz.training.common.mappers.SqlDomainMapper
import com.mz.training.domains.user.User

/**
 * Created by zemo on 11/10/15.
 */
trait UserMapper extends SqlDomainMapper[User] {

  /*
  id  bigserial NOT NULL,
  last_name     VARCHAR(255),
  first_name    VARCHAR(255),
  address_id    bigint,
  */

  //case class User(id: Long, firstName: String, lastName: String, addressId: Option[Long], address: Option[Address])

  val TABLE_NAME = "users"

  val FIRST_NAME_COL = "first_name"

  val LAST_NAME_COL = "last_name"

  val ADDRESS_ID_COL = "address_id"

  val SQL_PROJECTION = s"$ID_COL, $FIRST_NAME_COL, $LAST_NAME_COL, $ADDRESS_ID_COL"

  val COLUMNS = s"$FIRST_NAME_COL, $LAST_NAME_COL, $ADDRESS_ID_COL"

  override def sqlProjection: String = SQL_PROJECTION

  override def values(implicit entity: User): String = {
    val address = entity.addressId match {
      case Some(id) => s", ${id}"
      case None => ", NULL"
    }
    s"'${entity.firstName}', '${entity.lastName}'".concat(address)
  }

  override def columns: String = COLUMNS

  override def tableName: String = TABLE_NAME

  override def setValues(implicit entity: User): String = {
    val addressId = entity.addressId match {
      case Some(id) => s", $ADDRESS_ID_COL = ${id}"
      case None => s", $ADDRESS_ID_COL = NULL"
    }
    s"$FIRST_NAME_COL = '${entity.firstName}', $LAST_NAME_COL = '${entity.lastName}'".concat(addressId)
  }

  /**
   * Map ResultSet to User
    *
    * @param resultSet
   * @return User
   */
  def mapResultSetDomain(resultSet: ResultSet): User = {
    try {
      User(resultSet.getLong(ID_COL), resultSet.getString(FIRST_NAME_COL), resultSet.getString(LAST_NAME_COL),
        Option(resultSet.getLong(ADDRESS_ID_COL)), None)
    } catch {
      case e:Exception => {
        e.getMessage
        throw e
      }
    }
  }

}
