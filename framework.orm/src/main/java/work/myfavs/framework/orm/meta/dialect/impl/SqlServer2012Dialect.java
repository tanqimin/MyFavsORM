package work.myfavs.framework.orm.meta.dialect.impl;

import work.myfavs.framework.orm.meta.DbType;

/**
 * @author tanqimin
 */
public class SqlServer2012Dialect extends SqlServerDialect {

  @Override
  public String dbType() {

    return DbType.SQL_SERVER_2012;
  }
}
