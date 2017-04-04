/** set shema name */

DROP SCHEMA IF EXISTS akkademo CASCADE;

CREATE SCHEMA akkademo AUTHORIZATION postgres;

SET SCHEMA 'akkademo';

CREATE TABLE users (
  id  bigserial NOT NULL,
  last_name     VARCHAR(255),
  first_name    VARCHAR(255),
  address_id    bigint,
  CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE addresses (
  id  bigserial NOT NULL,
  street        VARCHAR(255),
  house_number  VARCHAR(255),
  zip           VARCHAR(5),
  city          VARCHAR(255),
  CONSTRAINT addresses_pk PRIMARY KEY (id)
);

ALTER TABLE users ADD CONSTRAINT fk_users_addresses FOREIGN KEY (address_id) REFERENCES addresses(id) ON UPDATE CASCADE;

CREATE TABLE item (
  id  bigserial NOT NULL,
  name        VARCHAR(255),
  description  VARCHAR(255),
  CONSTRAINT item_pk PRIMARY KEY (id)
);