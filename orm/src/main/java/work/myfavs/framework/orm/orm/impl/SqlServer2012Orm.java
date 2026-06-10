package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.SqlServer2012PageStrategy;

/**
 * ORM SQL Server 2012+ 实现。
 * <p>使用 {@link SqlServer2012PageStrategy}（OFFSET...FETCH NEXT 语法）作为分页策略。
 * 若使用 SQL Server 2005~2008，请改用 {@link SqlServerOrm}。</p>
 *
 * @see SqlServer2012PageStrategy
 * @see SqlServerOrm
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
