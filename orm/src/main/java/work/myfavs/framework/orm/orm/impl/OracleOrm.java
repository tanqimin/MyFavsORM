package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.OraclePageStrategy;

/**
 * ORM Oracle 实现。
 * <p>使用 {@link OraclePageStrategy}（ROWNUM 双层子查询）作为分页策略。</p>
 *
 * @see OraclePageStrategy
 * @see MySqlOrm
 */
public class OracleOrm extends AbstractOrm {

  /**
   * 构造 OracleOrm 实例.
   *
   * @param database {@link Database} 实例
   */
  public OracleOrm(Database database) {
    super(database, OraclePageStrategy.INSTANCE);
  }
}
