package com.mz.training.domains.address

import java.sql.ResultSet

import com.mz.training.domains.address.Address
import com.mz.training.common.mappers.SqlDomainMapper

/**
 * Created by zemo on 17/10/15.
 */
trait AddressMapper extends SqlDomainMapper[Address] {

  /*
   CREATE TABLE addresses (
  id  bigserial NOT NULL,
  street        VARCHAR(255),
  house_number  INTEGER,
  zip           VARCHAR(5),
  city          VARCHAR(255),
   */

  val TABLE_NAME = "addresses"

  val STREET_COL = "street"

  val HOUSE_NUMBER_COL = "house_number"

  val ZIP_COL = "zip"

  val CITY_COL = "city"

  val SQL_PROJECTION = s"$ID_COL, $STREET_COL, $ZIP_COL, $CITY_COL, $HOUSE_NUMBER_COL"

  val COLUMNS = s"$STREET_COL, $ZIP_COL, $CITY_COL, $HOUSE_NUMBER_COL"

  override def sqlProjection: String = SQL_PROJECTION

  override def values(implicit entity: Address): String = s"'${entity.street}', '${entity.zip}', '${entity.city}', '${entity.houseNumber}'"

  override def columns: String = COLUMNS

  override def tableName: String = TABLE_NAME

  override def setValues(implicit entity: Address): String = s"$STREET_COL = '${entity.street}', $HOUSE_NUMBER_COL = '${entity.houseNumber}', " +
    s"$ZIP_COL = '${entity.zip}', $CITY_COL = '${entity.city}'"

  /**
   * Map ResultSet to User
    *
    * @param resultSet
   * @return Address(id: Long, street: String, zip: String, houseNumber: String, city: String)
   */
  def mapResultSetDomain(resultSet: ResultSet): Address = {
    Address(resultSet.getLong(ID_COL), resultSet.getString(STREET_COL), resultSet.getString(ZIP_COL),
      resultSet.getString(HOUSE_NUMBER_COL), resultSet.getString(CITY_COL))
  }

}
