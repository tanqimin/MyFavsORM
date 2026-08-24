package work.myfavs.framework.orm.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.DbType;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 集中化数据库连接配置提供者。
 *
 * <p>按优先级从以下来源读取连接信息：</p>
 * <ol>
 *   <li>系统属性（System Property）：{@code db.type}、{@code db.url}、{@code db.user}、{@code db.password}</li>
 *   <li>环境变量（Environment Variable）：{@code DB_TYPE}、{@code DB_URL}、{@code DB_USER}、{@code DB_PASSWORD}</li>
 *   <li>默认值：H2 内存数据库</li>
 * </ol>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * // 默认 H2 内存数据库
 * DatabaseConfigProvider provider = DatabaseConfigProvider.create();
 *
 * // 指定 MySQL 数据库
 * DatabaseConfigProvider provider = DatabaseConfigProvider.create(
 *     "mysql", "jdbc:mysql://localhost:3306/test", "root", "root"
 * );
 *
 * // 通过系统属性或环境变量切换（maven 命令行）
 * // mvn test -Ddb.type=mysql -Ddb.url=jdbc:mysql://... -Ddb.user=root -Ddb.password=root
 * }</pre>
 */
public class DatabaseConfigProvider {

  static final String PROP_TYPE     = "db.type";
  static final String PROP_URL      = "db.url";
  static final String PROP_USER     = "db.user";
  static final String PROP_PASSWORD = "db.password";

  static final String ENV_TYPE     = "DB_TYPE";
  static final String ENV_URL      = "DB_URL";
  static final String ENV_USER     = "DB_USER";
  static final String ENV_PASSWORD = "DB_PASSWORD";

  private static final String H2_JDBC_URL = "jdbc:h2:mem:myfavs_test;DB_CLOSE_DELAY=-1;MODE=MYSQL";
  private static final String H2_USER     = "sa";
  private static final String H2_PASSWORD = "sa";

  private final String dbType;
  private final String jdbcUrl;
  private final String username;
  private final String password;
  private final String driverClassName;

  /**
   * 使用默认策略创建配置（优先读取系统属性/环境变量，兜底 H2）。
   */
  public static DatabaseConfigProvider create() {
    String type     = resolveType();
    String url      = resolveUrl(type);
    String user     = resolveUser();
    String password = resolvePassword();
    return new DatabaseConfigProvider(type, url, user, password);
  }

  /**
   * 显式指定全部连接参数创建配置。
   */
  public static DatabaseConfigProvider create(String dbType, String jdbcUrl, String username, String password) {
    return new DatabaseConfigProvider(
        Objects.requireNonNull(dbType),
        Objects.requireNonNull(jdbcUrl),
        Objects.requireNonNull(username),
        Objects.requireNonNull(password));
  }

  private DatabaseConfigProvider(String dbType, String jdbcUrl, String username, String password) {
    this.dbType         = dbType;
    this.jdbcUrl        = jdbcUrl;
    this.username       = username;
    this.password       = password;
    this.driverClassName = resolveDriver(dbType);
  }

  // ================ Public Getters ================

  public String getDbType()             { return dbType; }

  public String getJdbcUrl()            { return jdbcUrl; }

  public String getUsername()           { return username; }

  public String getPassword()           { return password; }

  public String getDriverClassName()    { return driverClassName; }

  // ================ Factory Methods ================

  /**
   * 创建 HikariCP DataSource。
   */
  public DataSource createDataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(driverClassName);
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setAutoCommit(false);
    return new HikariDataSource(config);
  }

  /**
   * 创建 DBTemplate 实例。
   */
  public DBTemplate createDBTemplate() {
    return new DBTemplate.Builder()
        .dataSource(createDataSource())
        .config(cfg -> cfg.setDbType(dbType).setShowSql(true).setShowResult(true))
        .build();
  }

  // ================ Private Helpers ================

  private static String resolveType() {
    String type = System.getProperty(PROP_TYPE);
    if (type != null && !type.isEmpty()) return type;

    type = System.getenv(ENV_TYPE);
    if (type != null && !type.isEmpty()) return type;

    return DbType.H2;
  }

  private static String resolveUrl(String type) {
    String url = System.getProperty(PROP_URL);
    if (url != null && !url.isEmpty()) return url;

    url = System.getenv(ENV_URL);
    if (url != null && !url.isEmpty()) return url;

    switch (type) {
      case DbType.MYSQL:
        return "jdbc:mysql://localhost:3306/myfavs_master?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
      case DbType.SQL_SERVER:
      case DbType.SQL_SERVER_2012:
        return "jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_master;sendStringParametersAsUnicode=false;encrypt=false";
      case DbType.POSTGRE_SQL:
        return "jdbc:postgresql://localhost:5432/myfavs_master";
      case DbType.ORACLE:
        return "jdbc:oracle:thin:@localhost:1521:xe";
      case DbType.H2:
      default:
        return H2_JDBC_URL;
    }
  }

  private static String resolveUser() {
    String user = System.getProperty(PROP_USER);
    if (user != null && !user.isEmpty()) return user;

    user = System.getenv(ENV_USER);
    if (user != null && !user.isEmpty()) return user;

    return H2_USER;
  }

  private static String resolvePassword() {
    String pass = System.getProperty(PROP_PASSWORD);
    if (pass != null && !pass.isEmpty()) return pass;

    pass = System.getenv(ENV_PASSWORD);
    if (pass != null && !pass.isEmpty()) return pass;

    return H2_PASSWORD;
  }

  private static String resolveDriver(String dbType) {
    switch (dbType) {
      case DbType.MYSQL:
        return "com.mysql.cj.jdbc.Driver";
      case DbType.SQL_SERVER:
      case DbType.SQL_SERVER_2012:
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
      case DbType.POSTGRE_SQL:
        return "org.postgresql.Driver";
      case DbType.ORACLE:
        return "oracle.jdbc.OracleDriver";
      case DbType.H2:
      default:
        return "org.h2.Driver";
    }
  }
}
