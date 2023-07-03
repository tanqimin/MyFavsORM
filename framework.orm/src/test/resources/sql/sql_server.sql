/*
 Navicat Premium Data Transfer

 Source Server         : mssql-192.168.8.246
 Source Server Type    : SQL Server
 Source Server Version : 12002569 (12.00.2569)
 Source Host           : 192.168.8.246:1433
 Source Catalog        : myfavs_master
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 12002569 (12.00.2569)
 File Encoding         : 65001

 Date: 12/06/2023 10:57:46
*/


-- ----------------------------
-- Table structure for tb_identity
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_identity]') AND type IN ('U'))
DROP TABLE [dbo].[tb_identity]
    GO

CREATE TABLE [dbo].[tb_identity] (
    [id] bigint  IDENTITY(1,1) NOT NULL,
    [created] datetime  NULL,
    [name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
    [disable] bit  NULL,
    [price] numeric(18,5)  NULL,
    [type] nvarchar(10) COLLATE Chinese_PRC_CI_AS  NULL,
    [config] text COLLATE Chinese_PRC_CI_AS  NULL
    )
    GO

ALTER TABLE [dbo].[tb_identity] SET (LOCK_ESCALATION = TABLE)
    GO


    -- ----------------------------
-- Table structure for tb_logic_delete
-- ----------------------------
    IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_logic_delete]') AND type IN ('U'))
DROP TABLE [dbo].[tb_logic_delete]
    GO

CREATE TABLE [dbo].[tb_logic_delete] (
    [id] bigint  NOT NULL,
    [created] datetime  NULL,
    [name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
    [disable] bit  NULL,
    [price] numeric(18,5)  NULL,
    [type] nvarchar(10) COLLATE Chinese_PRC_CI_AS  NULL,
    [config] text COLLATE Chinese_PRC_CI_AS  NULL,
    [deleted] bit  NULL
    )
    GO

ALTER TABLE [dbo].[tb_logic_delete] SET (LOCK_ESCALATION = TABLE)
    GO


    -- ----------------------------
-- Table structure for tb_snowflake
-- ----------------------------
    IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_snowflake]') AND type IN ('U'))
DROP TABLE [dbo].[tb_snowflake]
    GO

CREATE TABLE [dbo].[tb_snowflake] (
    [id] bigint  NOT NULL,
    [created] datetime  NULL,
    [name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
    [disable] bit  NULL,
    [price] numeric(18,5)  NULL,
    [type] nvarchar(10) COLLATE Chinese_PRC_CI_AS  NULL,
    [config] text COLLATE Chinese_PRC_CI_AS  NULL
    )
    GO

ALTER TABLE [dbo].[tb_snowflake] SET (LOCK_ESCALATION = TABLE)
    GO

    -- ----------------------------
-- Table structure for tb_uuid
-- ----------------------------
    IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_uuid]') AND type IN ('U'))
DROP TABLE [dbo].[tb_uuid]
    GO

CREATE TABLE [dbo].[tb_uuid] (
    [id] uniqueidentifier  NOT NULL,
    [created] datetime  NULL,
    [name] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
    [disable] bit  NULL,
    [price] numeric(18,5)  NULL,
    [type] nvarchar(10) COLLATE Chinese_PRC_CI_AS  NULL,
    [config] text COLLATE Chinese_PRC_CI_AS  NULL
    )
    GO

ALTER TABLE [dbo].[tb_uuid] SET (LOCK_ESCALATION = TABLE)
    GO


    -- ----------------------------
-- Auto increment value for tb_identity
-- ----------------------------
    DBCC CHECKIDENT ('[dbo].[tb_identity]', RESEED, 1)
    GO


-- ----------------------------
-- Primary Key structure for table tb_identity
-- ----------------------------
ALTER TABLE [dbo].[tb_identity] ADD CONSTRAINT [PK_tb_identity] PRIMARY KEY CLUSTERED ([id])
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
    GO


-- ----------------------------
-- Primary Key structure for table tb_logic_delete
-- ----------------------------
ALTER TABLE [dbo].[tb_logic_delete] ADD CONSTRAINT [PK__tb_snowf__3213E83F272798EB] PRIMARY KEY CLUSTERED ([id])
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
    GO


-- ----------------------------
-- Primary Key structure for table tb_snowflake
-- ----------------------------
ALTER TABLE [dbo].[tb_snowflake] ADD CONSTRAINT [PK_tb_snowflake] PRIMARY KEY CLUSTERED ([id])
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
    GO

-- ----------------------------
-- Primary Key structure for table tb_uuid
-- ----------------------------
ALTER TABLE [dbo].[tb_uuid] ADD CONSTRAINT [PK_tb_uuid] PRIMARY KEY CLUSTERED ([id])
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
    GO

