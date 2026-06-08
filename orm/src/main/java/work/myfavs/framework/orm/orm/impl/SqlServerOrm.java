package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.SqlServerPageStrategy;

/**
 * Orm SqlServer实现：2005以上，2012或以上版本请使用 {@link SqlServer2012Orm}
 */
public class SqlServerOrm extends AbstractOrm {

  /**
   * 构造 SqlServerOrm 实例.
   *
   * @param database {@link Database} 实例
   */
  public SqlServerOrm(Database database) {
    super(database, SqlServerPageStrategy.INSTANCE);
  }
}
