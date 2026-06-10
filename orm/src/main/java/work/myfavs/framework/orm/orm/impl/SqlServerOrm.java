package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.SqlServerPageStrategy;

/**
 * ORM SQL Server 2005~2008 实现。
 * <p>使用 {@link SqlServerPageStrategy}（ROW_NUMBER() OVER 语法）作为分页策略。
 * 若使用 SQL Server 2012+，请改用 {@link SqlServer2012Orm}。</p>
 *
 * @see SqlServerPageStrategy
 * @see SqlServer2012Orm
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
