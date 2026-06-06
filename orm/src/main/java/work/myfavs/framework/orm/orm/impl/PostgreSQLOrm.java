package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;

/**
 * Orm PostgreSQL实现
 */
public class PostgreSQLOrm extends MySqlOrm {
  /**
   * 构造 PostgreSQLOrm 实例.
   *
   * @param database {@link Database} 实例
   */
  public PostgreSQLOrm(Database database) {
    super(database);
  }

  /**
   * 获取数据库类型.
   *
   * @return 数据库类型 {@link DbType#POSTGRE_SQL}
   */
  @Override
  protected String dbType() {
    return DbType.POSTGRE_SQL;
  }
}
