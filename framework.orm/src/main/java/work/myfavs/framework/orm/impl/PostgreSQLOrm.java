package work.myfavs.framework.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;

public class PostgreSQLOrm extends MySqlOrm {
  public PostgreSQLOrm(Database database) {
    super(database);
  }

  @Override
  protected String dbType() {
    return DbType.POSTGRE_SQL;
  }
}
