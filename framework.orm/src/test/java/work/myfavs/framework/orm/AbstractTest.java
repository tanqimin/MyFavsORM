package work.myfavs.framework.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;

public class AbstractTest {
  protected static final String DB_TYPE = DbType.SQL_SERVER;
  protected static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  protected static final String JDBC_URL =
      "jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_master;sendStringParametersAsUnicode=false;encrypt=false";
  protected static final String JDBC_USERNAME = "sa";
  protected static final String JDBC_PASSWORD = "sa";

  protected static DataSource dataSource;
  protected static DBTemplate dbTemplate;

  private static void initDBTemplate() {
    if (dbTemplate == null) {
      HikariConfig configuration = new HikariConfig();
      configuration.setDriverClassName(DRIVER_CLASS);
      configuration.setJdbcUrl(JDBC_URL);
      configuration.setUsername(JDBC_USERNAME);
      configuration.setPassword(JDBC_PASSWORD);
      configuration.setAutoCommit(false);
      dataSource = new HikariDataSource(configuration);
      dbTemplate =
          new DBTemplate.Builder()
              .dataSource(dataSource)
              .config(config -> config.setDbType(DB_TYPE).setShowSql(true).setShowResult(true))
              .build();
    }
  }

  private static void initTables() {

    List<Sql> initSql = new ArrayList<>();
    initSql.add(
        new Sql(
                "IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_identity]') AND type IN (N'U'))")
            .appendLine("DROP TABLE [dbo].[tb_identity]"));
    initSql.add(
        new Sql("CREATE TABLE [dbo].[tb_identity](")
            .appendLine("[id] [BIGINT] IDENTITY(1,1) NOT NULL,")
            .appendLine("[created] [DATETIME] NULL,")
            .appendLine("[name] [NVARCHAR](50) NULL,")
            .appendLine("[disable] [BIT] NULL,")
            .appendLine("[price] [NUMERIC](18, 5) NULL,")
            .appendLine("[type] [NVARCHAR](10) NULL,")
            .appendLine("[config] [TEXT] NULL,")
            .appendLine(" CONSTRAINT [PK_tb_identity] PRIMARY KEY CLUSTERED ")
            .appendLine(
                "( [id] ASC ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]")
            .appendLine(") ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]"));
    initSql.add(
        new Sql(
                "IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_snowflake]') AND type IN (N'U'))")
            .appendLine("DROP TABLE [dbo].[tb_snowflake]"));
    initSql.add(
        new Sql("CREATE TABLE [dbo].[tb_snowflake](")
            .appendLine("[id] [BIGINT] NOT NULL,")
            .appendLine("[created] [DATETIME] NULL,")
            .appendLine("[name] [NVARCHAR](50) NULL,")
            .appendLine("[disable] [BIT] NULL,")
            .appendLine("[price] [NUMERIC](18, 5) NULL,")
            .appendLine("[type] [NVARCHAR](10) NULL,")
            .appendLine("[config] [TEXT] NULL,")
            .appendLine("CONSTRAINT [PK_tb_snowflake] PRIMARY KEY CLUSTERED ")
            .appendLine(
                "( [id] ASC ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]")
            .appendLine(") ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]"));
    initSql.add(
        new Sql(
                "IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tb_uuid]') AND type IN (N'U'))")
            .appendLine("DROP TABLE [dbo].[tb_uuid]"));
    initSql.add(
        new Sql("CREATE TABLE [dbo].[tb_uuid](")
            .appendLine("[id] [UNIQUEIDENTIFIER] NOT NULL,")
            .appendLine("[created] [DATETIME] NULL,")
            .appendLine("[name] [NVARCHAR](50) NULL,")
            .appendLine("[disable] [BIT] NULL,")
            .appendLine("[price] [NUMERIC](18, 5) NULL,")
            .appendLine("[type] [NVARCHAR](10) NULL,")
            .appendLine("[config] [TEXT] NULL,")
            .appendLine("CONSTRAINT [PK_tb_uuid] PRIMARY KEY CLUSTERED")
            .appendLine(
                "( [id] ASC )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]")
            .appendLine(") ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]"));

    DB db = DB.conn(dbTemplate);
    db.tx(() -> db.execute(initSql));
  }

  @BeforeClass
  public static void beforeClass() {
    initDBTemplate();
    initTables();
  }

  @AfterClass
  public static void afterClass() {
    dataSource = null;
    dbTemplate = null;
  }
}
