package work.myfavs.framework.orm.meta.dialect;

import work.myfavs.framework.orm.meta.DbType;

/** @author tanqimin */
public class H2Dialect extends MySqlDialect {

  @Override
  public String getDialectName() {

    return DbType.H2;
  }
}
