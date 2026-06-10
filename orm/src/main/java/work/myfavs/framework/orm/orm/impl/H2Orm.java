package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;

/**
 * ORM H2 数据库实现。
 * <p>H2 与 MySQL 分页语法兼容（均使用 LIMIT offset, count），故直接继承 {@link MySqlOrm}，无额外逻辑。</p>
 *
 * @see MySqlOrm
 */
public class H2Orm extends MySqlOrm {
  /**
   * 构造 H2Orm 实例.
   *
   * @param database {@link Database} 实例
   */
  public H2Orm(Database database) {
    super(database);
  }
}
