package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;

/**
 * Orm H2实现
 */
public class H2Orm extends MySqlOrm {
  public H2Orm(Database database) {
    super(database);
  }

  @Override
  protected String dbType() {
    return DbType.H2;
  }
}
