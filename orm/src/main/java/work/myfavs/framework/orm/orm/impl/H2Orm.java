package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;

/**
 * Orm H2实现
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

  /**
   * 获取数据库类型.
   *
   * @return 数据库类型 {@link DbType#H2}
   */
  @Override
  protected String dbType() {
    return DbType.H2;
  }
}
