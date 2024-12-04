/*
 Navicat Premium Dump SQL

 Source Server         : mssql@192.168.8.246
 Source Server Type    : SQL Server
 Source Server Version : 12002569 (12.00.2569)
 Source Host           : 192.168.8.246:1433
 Source Catalog        : myfavs_tenant2
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 12002569 (12.00.2569)
 File Encoding         : 65001

 Date: 04/12/2024 09:38:37
*/


-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_user]') AND type IN ('U'))
DROP TABLE [dbo].[tb_user]
    GO

CREATE TABLE [dbo].[tb_user] (
    [id] bigint  NOT NULL,
    [created] datetime  NULL,
    [modified] datetime  NULL,
    [username] varchar(20) COLLATE Chinese_PRC_CI_AS  NULL,
    [email] varchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
    [password] varchar(100) COLLATE Chinese_PRC_CI_AS  NULL,
    [user_type] varchar(20) COLLATE Chinese_PRC_CI_AS  NULL
    )
    GO

ALTER TABLE [dbo].[tb_user] SET (LOCK_ESCALATION = TABLE)
    GO


-- ----------------------------
-- Primary Key structure for table tb_user
-- ----------------------------
ALTER TABLE [dbo].[tb_user] ADD CONSTRAINT [PK_user] PRIMARY KEY CLUSTERED ([id])
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
    GO

