package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;

/**
 * Orm PostgreSQL实现
 */
public class PostgreSQLOrm extends MySqlOrm {
  public PostgreSQLOrm(Database database) {
    super(database);
  }

  @Override
  protected String dbType() {
    return DbType.POSTGRE_SQL;
  }
}
