package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.dialect.PostgreSqlDialect;

/**
 * ORM PostgreSQL 实现。
 * <p>使用 {@link PostgreSqlDialect}（LIMIT ... OFFSET 分页 + INSERT ... ON CONFLICT UPSERT）。</p>
 *
 * @see MySqlOrm
 */
public class PostgreSQLOrm extends AbstractOrm {

  /**
   * 构造 PostgreSQLOrm 实例.
   *
   * @param database {@link Database} 实例
   */
  public PostgreSQLOrm(Database database) {
    super(database, PostgreSqlDialect.INSTANCE);
  }
}
