package com.mz.training.domains.item

import java.sql.ResultSet

import com.mz.training.common.mappers.SqlDomainMapper
import com.mz.training.domains.address.Address

/**
  * Created by zemi on 10/08/16.
  */
trait ItemMapper extends SqlDomainMapper[Item] {

  /*
   CREATE TABLE addresses (
  id  bigserial NOT NULL,
  street        VARCHAR(255),
  house_number  INTEGER,
  zip           VARCHAR(5),
  city          VARCHAR(255),
   */

  val TABLE_NAME = "item"

  val NAME_COL = "name"

  val DESCRIPTION_COL = "description"

  val SQL_PROJECTION = s"$ID_COL, $NAME_COL, $DESCRIPTION_COL"

  val COLUMNS = s"$NAME_COL, $DESCRIPTION_COL"

  override def sqlProjection: String = SQL_PROJECTION

  override def values(implicit entity: Item): String = s"'${entity.name}', '${entity.description}'"

  override def columns: String = COLUMNS

  override def tableName: String = TABLE_NAME

  override def setValues(implicit entity: Item): String = s"$NAME_COL = '${entity.name}', $DESCRIPTION_COL = '${entity.description}'"

  /**
    * Map ResultSet to User
    *
    * @param resultSet
    * @return Address(id: Long, street: String, zip: String, houseNumber: String, city: String)
    */
  def mapResultSetDomain(resultSet: ResultSet): Item = {
    Item(resultSet.getLong(ID_COL), resultSet.getString(NAME_COL), resultSet.getString(DESCRIPTION_COL))
  }

}
