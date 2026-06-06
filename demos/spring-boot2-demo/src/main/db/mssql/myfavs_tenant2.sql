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
  (1492150022916149248, '2022-02-11 12:00:00', '2022-02-11 12:00:01', 'tenant2_user1', 'tenant2_user1@myfavs_tenant2.com', '123456', 'ADMIN');
GO
