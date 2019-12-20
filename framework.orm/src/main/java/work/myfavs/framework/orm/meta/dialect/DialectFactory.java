package work.myfavs.framework.orm.meta.dialect;

import java.util.HashMap;
import java.util.Map;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.util.exception.DBException;


/**
 * 数据库方言工厂类
 *
 * @author tanqimin
 */
public class DialectFactory {

  private final static Map<String, IDialect> map;

  static {
    map = new HashMap<>();
    map.put(DbType.SQL_SERVER, new SqlServerDialect());
    map.put(DbType.SQL_SERVER_2012, new SqlServer2012Dialect());
    map.put(DbType.MYSQL, new MySqlDialect());
    map.put(DbType.H2, new H2Dialect());
    map.put(DbType.POSTGRE_SQL, new PostgreSQLDialect());
  }

  private DialectFactory() {

  }

  /**
   * 获取数据库方言实现
   *
   * @param dbType 数据库类型，sqlserver、mysql
   *
   * @return 数据库方言实现类
   */
  public static IDialect getInstance(String dbType) {

    IDialect iDialect = map.get(dbType);
    if (iDialect == null) {
      throw new DBException("{} database is not supported.", dbType);
    }
    return iDialect;
  }

}
