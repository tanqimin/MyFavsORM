package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.strategy.MySqlPageStrategy;

/**
 * Orm MySql实现
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
