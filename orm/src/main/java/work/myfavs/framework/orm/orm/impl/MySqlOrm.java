package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.MySqlPageStrategy;

/**
 * ORM MySQL 实现。
 * <p>使用 {@link MySqlPageStrategy}（LIMIT offset, count 语法）作为分页策略。
 * 若需使用 {@code H2} 数据库，可复用此实现（{@link H2Orm} 继承此类）。</p>
 *
 * @see H2Orm
 * @see OracleOrm
 * @see PostgreSQLOrm
 * @see SqlServerOrm
 * @see SqlServer2012Orm
 */
public class MySqlOrm extends AbstractOrm {

  /**
   * 构造 MySqlOrm 实例.
   *
   * @param database {@link Database} 实例
   */
  public MySqlOrm(Database database) {
    super(database, MySqlPageStrategy.INSTANCE);
  }
}
