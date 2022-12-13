/****** Object:  Table [dbo].[tb_tenant]    Script Date: 2022/12/13 11:32:30 ******/
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_tenant]') AND type IN (N'U'))
DROP TABLE [dbo].[tb_tenant];
GO

/****** Object:  Table [dbo].[tb_tenant]    Script Date: 2022/12/13 11:32:30 ******/
SET ANSI_NULLS ON;
GO

SET QUOTED_IDENTIFIER ON;
GO

CREATE TABLE [dbo].[tb_tenant] (
    [id]            [BIGINT]       NOT NULL,
    [created]       [DATETIME]     NULL,
    [modified]      [DATETIME]     NULL,
    [tenant]        [VARCHAR](50)  NULL,
    [jdbc_url]      [VARCHAR](300) NULL,
    [jdbc_user]     [VARCHAR](50)  NULL,
    [jdbc_password] [VARCHAR](50)  NULL,
    [jdbc_class]    [VARCHAR](100) NULL,
    CONSTRAINT [PK_tenant] PRIMARY KEY CLUSTERED ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON ) ON [PRIMARY]
    ) ON [PRIMARY];
GO

-- ----------------------------
-- Records of tb_tenant
-- ----------------------------
INSERT INTO dbo.[tb_tenant]
  (id, created, modified, tenant, jdbc_url, jdbc_user, jdbc_password, jdbc_class)
VALUES
  (1492150022916149249, '2022-02-12 21:29:20', '2022-02-12 21:29:22', 'myfavs_master',
   'jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_master;sendStringParametersAsUnicode=false;encrypt=false',
   'sa', 'sa', 'com.microsoft.sqlserver.jdbc.SQLServerDriver');
INSERT INTO dbo.[tb_tenant]
(id, created, modified, tenant, jdbc_url, jdbc_user, jdbc_password, jdbc_class)
VALUES
    (1492150022916149250, '2022-02-12 21:29:20', '2022-02-12 21:29:22', 'myfavs_tenant1',
    'jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_tenant1;sendStringParametersAsUnicode=false;encrypt=false',
    'sa', 'sa', 'com.microsoft.sqlserver.jdbc.SQLServerDriver');
INSERT INTO dbo.[tb_tenant]
(id, created, modified, tenant, jdbc_url, jdbc_user, jdbc_password, jdbc_class)
VALUES
    (1492150022916149251, '2022-02-12 21:29:20', '2022-02-12 21:29:22', 'myfavs_tenant2',
    'jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_tenant2;sendStringParametersAsUnicode=false;encrypt=false',
    'sa', 'sa', 'com.microsoft.sqlserver.jdbc.SQLServerDriver');

/****** Object:  Table [dbo].[tb_user]    Script Date: 2022/12/13 11:33:15 ******/
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_user]') AND type IN (N'U'))
DROP TABLE [dbo].[tb_user];
GO

/****** Object:  Table [dbo].[tb_user]    Script Date: 2022/12/13 11:33:15 ******/
SET ANSI_NULLS ON;
GO

SET QUOTED_IDENTIFIER ON;
GO

CREATE TABLE [dbo].[tb_user] (
    [id]        [BIGINT]       NOT NULL,
    [created]   [DATETIME]     NULL,
    [modified]  [DATETIME]     NULL,
    [username]  [VARCHAR](20)  NULL,
    [email]     [VARCHAR](50)  NULL,
    [password]  [VARCHAR](100) NULL,
    [user_type] [VARCHAR](20)  NULL,
    CONSTRAINT [PK_user] PRIMARY KEY CLUSTERED ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON ) ON [PRIMARY]
    ) ON [PRIMARY];
GO

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO dbo.[tb_user]
  (id, created, modified, username, email, password, user_type)
VALUES
  (1492150022916149248, '2022-02-11 12:00:00', '2022-02-11 12:00:01', 'master_user1', 'master_user1@myfavs_master.com', '123456', 'ADMIN');
GO
