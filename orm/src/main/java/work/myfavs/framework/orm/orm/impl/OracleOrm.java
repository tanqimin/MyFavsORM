package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.OraclePageStrategy;

/**
 * Orm Oracle实现
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
