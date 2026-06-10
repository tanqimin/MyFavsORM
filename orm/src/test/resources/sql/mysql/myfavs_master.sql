-- MySQL DDL for myfavs_master test database

DROP TABLE IF EXISTS tb_assigned;
CREATE TABLE tb_assigned (
    epc varchar(50) NOT NULL PRIMARY KEY
);

DROP TABLE IF EXISTS tb_identity;
CREATE TABLE tb_identity (
    id bigint AUTO_INCREMENT NOT NULL PRIMARY KEY,
    created datetime NULL,
    name varchar(50) NULL,
    disable tinyint(1) NULL,
    price decimal(18,5) NULL,
    type varchar(10) NULL,
    config text NULL
);

DROP TABLE IF EXISTS tb_logic_delete;
CREATE TABLE tb_logic_delete (
    id bigint NOT NULL PRIMARY KEY,
    created datetime NULL,
    name varchar(50) NULL,
    disable tinyint(1) NULL,
    price decimal(18,5) NULL,
    type varchar(10) NULL,
    config text NULL,
    deleted bigint NULL
);

DROP TABLE IF EXISTS tb_snowflake;
CREATE TABLE tb_snowflake (
    id bigint NOT NULL PRIMARY KEY,
    created datetime NULL,
    name varchar(50) NULL,
    disable tinyint(1) NULL,
    price decimal(18,5) NULL,
    type varchar(10) NULL,
    config text NULL
);

DROP TABLE IF EXISTS tb_uuid;
CREATE TABLE tb_uuid (
    id varchar(36) NOT NULL PRIMARY KEY,
    created datetime NULL,
    name varchar(50) NULL,
    disable tinyint(1) NULL,
    price decimal(18,5) NULL,
    type varchar(10) NULL,
    config text NULL
);
