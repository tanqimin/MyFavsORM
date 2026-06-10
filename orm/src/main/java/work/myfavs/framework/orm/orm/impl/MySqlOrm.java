package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.orm.dialect.MySqlDialect;

/**
 * ORM MySQL 实现。
 * <p>使用 {@link MySqlDialect}（LIMIT offset, count 语法 + INSERT ... ON DUPLICATE KEY UPDATE）。</p>
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
    super(database, MySqlDialect.INSTANCE);
  }
}
