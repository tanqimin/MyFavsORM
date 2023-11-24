package work.myfavs.framework.orm;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
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
  protected static final String DB_TYPE       = DbType.SQL_SERVER;
  protected static final String DRIVER_CLASS  = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  protected static final String JDBC_URL      =
      "jdbc:sqlserver://192.168.8.246:1433;"
          + "DatabaseName=myfavs_master;"
          + "sendStringParametersAsUnicode=false;"
          + "encrypt=false";
  protected static final String JDBC_USERNAME = "sa";
  protected static final String JDBC_PASSWORD = "sa";

  protected static DataSource dataSource;
  protected static DBTemplate dbTemplate;

  protected static Database database;

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

  private static void createTablesForSqlServer() {
    String    sqlContent = ResourceUtil.readUtf8Str("sql/sql_server.sql");
    List<Sql> sqlList    = new ArrayList<>();
    for (String s : sqlContent.split("GO")) {
      sqlList.add(new Sql(StrUtil.trim(s)));
    }
    database.execute(sqlList);
  }

  @BeforeClass
  public static void beforeClass() {
    initDBTemplate();

    initDatabase();

    createTablesForSqlServer();
  }

  private static void initDatabase() {
    if (database == null) {
      database = dbTemplate.createDatabase();
    }
  }

  @AfterClass
  public static void afterClass() {
    database.close();
    dataSource = null;
    dbTemplate = null;
    database = null;
  }
}
