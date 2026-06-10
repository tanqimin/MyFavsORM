package work.myfavs.framework.orm.meta;

/**
 * 数据库类型常量定义。
 * <p>以字符串常量形式定义框架支持的数据库类型，用于 {@link work.myfavs.framework.orm.DBConfig#setDbType(String)} 配置。</p>
 * <p>当前支持的数据库：SQL Server（两个版本）、MySQL、PostgreSQL、Oracle、H2。</p>
 *
 * @see work.myfavs.framework.orm.DBConfig
 * @see work.myfavs.framework.orm.orm.OrmFactory
 */
public class DbType {

  public static final String SQL_SERVER = "sqlserver";
  public static final String SQL_SERVER_2012 = "sqlserver2012";
  public static final String MYSQL = "mysql";
  public static final String POSTGRE_SQL = "postgresql";
  public static final String ORACLE = "oracle";
  public static final String H2 = "h2";
}
