package work.myfavs.framework.orm.meta.dialect;

import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.dialect.impl.*;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库方言工厂类
 *
 * @author tanqimin
 */
public class DialectFactory {

  private static final Map<String, IDialect> map = new HashMap<>();

  private DialectFactory() {}

  /**
   * 创建方言实例
   *
   * @param dbType      数据库类型 {@link DbType}
   * @param maxPageSize 每页最大记录数 {@link DBConfig#getMaxPageSize()}
   * @return {@link IDialect}
   */
  private static IDialect createDialect(String dbType, int maxPageSize) {
    IDialect dialect;
    switch (dbType) {
      case DbType.SQL_SERVER:
        dialect = new SqlServerDialect(maxPageSize);
        break;
      case DbType.SQL_SERVER_2012:
        dialect = new SqlServer2012Dialect(maxPageSize);
        break;
      case DbType.MYSQL:
        dialect = new MySqlDialect(maxPageSize);
        break;
      case DbType.POSTGRE_SQL:
        dialect = new PostgreSQLDialect(maxPageSize);
        break;
      case DbType.ORACLE:
        dialect = new OracleDialect(maxPageSize);
        break;
      case DbType.H2:
        dialect = new H2Dialect(maxPageSize);
        break;
      default:
        throw new DBException("{} database is not supported.", dbType);
    }
    return dialect;
  }

  /**
   * 获取方言实例
   *
   * @param dbType      数据库类型 {@link DbType}
   * @param maxPageSize 每页最大记录数 {@link DBConfig#getMaxPageSize()}
   * @return {@link IDialect}
   */
  public static IDialect getInstance(String dbType, int maxPageSize) {
    IDialect dialect = map.get(dbType);
    if (dialect == null) {
      dialect = createDialect(dbType, maxPageSize);
      map.put(dbType, dialect);
    }
    return dialect;
  }
}
