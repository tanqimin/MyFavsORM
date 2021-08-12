package work.myfavs.framework.orm.meta.dialect;

import work.myfavs.framework.orm.meta.DbType;

/** @author tanqimin */
public class PostgreSQLDialect extends MySqlDialect {

  @Override
  public String getDialectName() {

    return DbType.POSTGRE_SQL;
  }
}
