package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;

/**
 * ORM PostgreSQL 实现。
 * <p>PostgreSQL 与 MySQL 分页语法兼容（均使用 LIMIT offset, count），故直接继承 {@link MySqlOrm}，无额外逻辑。</p>
 *
 * @see MySqlOrm
 */
public class PostgreSQLOrm extends MySqlOrm {
  /**
   * 构造 PostgreSQLOrm 实例.
   *
   * @param database {@link Database} 实例
   */
  public PostgreSQLOrm(Database database) {
    super(database);
  }
}
