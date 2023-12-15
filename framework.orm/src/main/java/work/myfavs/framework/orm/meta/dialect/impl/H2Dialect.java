package work.myfavs.framework.orm.meta.dialect.impl;

import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.meta.DbType;

/**
 * @author tanqimin
 */
public class H2Dialect extends MySqlDialect {

  public H2Dialect(DBConfig dbConfig) {
    super(dbConfig);
  }

  @Override
  public String dbType() {

    return DbType.H2;
  }
}
