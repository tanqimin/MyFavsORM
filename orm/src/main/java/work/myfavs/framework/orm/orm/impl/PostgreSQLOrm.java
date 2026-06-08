package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;

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
}
