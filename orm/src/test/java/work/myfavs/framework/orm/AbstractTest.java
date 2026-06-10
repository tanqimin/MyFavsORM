package work.myfavs.framework.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.test.DatabaseConfigProvider;
import work.myfavs.framework.orm.test.IntegrationTest;
import work.myfavs.framework.orm.util.common.IOUtil;
import work.myfavs.framework.orm.util.common.StringUtil;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Category(IntegrationTest.class)
public class AbstractTest {

  protected static final String DB_TYPE;
  protected static final String DRIVER_CLASS;
  protected static final String JDBC_URL;
  protected static final String JDBC_USERNAME;
  protected static final String JDBC_PASSWORD;

  protected static DataSource dataSource;
  protected static DBTemplate dbTemplate;

  protected static Database database;

  static {
    DatabaseConfigProvider configProvider = DatabaseConfigProvider.create();
    DB_TYPE       = configProvider.getDbType();
    DRIVER_CLASS  = configProvider.getDriverClassName();
    JDBC_URL      = configProvider.getJdbcUrl();
    JDBC_USERNAME = configProvider.getUsername();
    JDBC_PASSWORD = configProvider.getPassword();
  }

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

  private static void createTables() {
    String sqlPath = getSqlPath(DB_TYPE);
    String sqlContent = IOUtil.read(sqlPath);
    List<String> sqlList = new ArrayList<>();
    if (DbType.SQL_SERVER.equals(DB_TYPE) || DbType.SQL_SERVER_2012.equals(DB_TYPE)) {
      for (String s : sqlContent.split("GO")) {
        sqlList.add(StringUtil.trim(s));
      }
    } else {
      for (String s : sqlContent.split(";")) {
        sqlList.add(StringUtil.trim(s));
      }
    }
    database.tx(em -> {
      for (String sql : sqlList) {
        if (StringUtil.isEmpty(sql)) continue;
        em.execute(new Sql(sql));
      }
    });
  }

  private static String getSqlPath(String dbType) {
    switch (dbType) {
      case DbType.MYSQL:
        return "sql/mysql/myfavs_master.sql";
      case DbType.SQL_SERVER:
      case DbType.SQL_SERVER_2012:
        return "sql/mssql/myfavs_master.sql";
      case DbType.POSTGRE_SQL:
        return "sql/postgresql/myfavs_master.sql";
      case DbType.H2:
      default:
        return "sql/h2/myfavs_master.sql";
    }
  }

  @BeforeClass
  public static void beforeClass() {
    initDBTemplate();

    initDatabase();

    createTables();
  }

  private static void initDatabase() {
    if (null == database) {
      database = dbTemplate.createDatabase();
    }
  }

  @AfterClass
  public static void afterClass() {
    if (null != database) {
      database.close();
    }
    dataSource = null;
    dbTemplate = null;
    database = null;
  }

  public static void main(String[] args) {

  }
}
