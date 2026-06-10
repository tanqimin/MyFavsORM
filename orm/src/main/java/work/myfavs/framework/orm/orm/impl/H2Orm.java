package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.dialect.H2Dialect;

/**
 * ORM H2 数据库实现。
 * <p>使用 {@link H2Dialect}（兼容 MySQL 方言）。</p>
 *
 * @see MySqlOrm
 */
public class H2Orm extends AbstractOrm {

  /**
   * 构造 H2Orm 实例.
   *
   * @param database {@link Database} 实例
   */
  public H2Orm(Database database) {
    super(database, H2Dialect.INSTANCE);
  }
}
