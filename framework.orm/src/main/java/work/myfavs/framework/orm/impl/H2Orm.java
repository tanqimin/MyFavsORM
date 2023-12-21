package work.myfavs.framework.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;

public class H2Orm extends MySqlOrm {
  public H2Orm(Database database) {
    super(database);
  }

  @Override
  protected String dbType() {
    return DbType.H2;
  }
}
