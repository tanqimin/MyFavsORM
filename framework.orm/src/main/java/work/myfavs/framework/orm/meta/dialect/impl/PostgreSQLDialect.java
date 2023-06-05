package work.myfavs.framework.orm.meta.dialect.impl;

import work.myfavs.framework.orm.meta.DbType;

/** @author tanqimin */
public class PostgreSQLDialect extends MySqlDialect {

  @Override
  public String dbType() {

    return DbType.POSTGRE_SQL;
  }
}
