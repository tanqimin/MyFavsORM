package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.SqlServer2012PageStrategy;

/**
 * Orm SqlServer实现 2012或以上版本
 */
public class SqlServer2012Orm extends AbstractOrm {

  /**
   * 构造 SqlServer2012Orm 实例.
   *
   * @param database {@link Database} 实例
   */
  public SqlServer2012Orm(Database database) {
    super(database, SqlServer2012PageStrategy.INSTANCE);
  }
}
