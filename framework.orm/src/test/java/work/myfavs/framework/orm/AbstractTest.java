package work.myfavs.framework.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.IOUtil;
import work.myfavs.framework.orm.util.common.StringUtil;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

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
    if (null == dbTemplate) {
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
    String       sqlContent = IOUtil.read("sql/sql_server.sql");
    List<String> sqlList    = new ArrayList<>();
    for (String s : sqlContent.split("GO")) {
      sqlList.add(StringUtil.trim(s));
    }
    database.tx(em -> {
      for (String sql : sqlList) {
        em.execute(new Sql(sql));
      }
    });
  }

  @BeforeClass
  public static void beforeClass() {
    initDBTemplate();

    initDatabase();

    createTablesForSqlServer();
  }

  private static void initDatabase() {
    if (null == database) {
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

  public static void main(String[] args) {

  }
}
