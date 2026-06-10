-- PostgreSQL DDL for myfavs_master test database

DROP TABLE IF EXISTS tb_assigned CASCADE;
CREATE TABLE tb_assigned (
    epc varchar(50) NOT NULL PRIMARY KEY
);

DROP TABLE IF EXISTS tb_identity CASCADE;
CREATE TABLE tb_identity (
    id bigserial NOT NULL PRIMARY KEY,
    created timestamp NULL,
    name varchar(50) NULL,
    disable boolean NULL,
    price numeric(18,5) NULL,
    type varchar(10) NULL,
    config text NULL
);

DROP TABLE IF EXISTS tb_logic_delete CASCADE;
CREATE TABLE tb_logic_delete (
    id bigint NOT NULL PRIMARY KEY,
    created timestamp NULL,
    name varchar(50) NULL,
    disable boolean NULL,
    price numeric(18,5) NULL,
    type varchar(10) NULL,
    config text NULL,
    deleted bigint NULL
);

DROP TABLE IF EXISTS tb_snowflake CASCADE;
CREATE TABLE tb_snowflake (
    id bigint NOT NULL PRIMARY KEY,
    created timestamp NULL,
    name varchar(50) NULL,
    disable boolean NULL,
    price numeric(18,5) NULL,
    type varchar(10) NULL,
    config text NULL
);

DROP TABLE IF EXISTS tb_uuid CASCADE;
CREATE TABLE tb_uuid (
    id varchar(36) NOT NULL PRIMARY KEY,
    created timestamp NULL,
    name varchar(50) NULL,
    disable boolean NULL,
    price numeric(18,5) NULL,
    type varchar(10) NULL,
    config text NULL
);
