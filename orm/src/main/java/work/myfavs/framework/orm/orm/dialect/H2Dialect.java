package work.myfavs.framework.orm.orm.dialect;

import com.alibaba.druid.util.JdbcConstants;

/**
 * H2 方言实现。
 * <p>H2 兼容 MySQL 模式（MODE=MYSQL），分页和 UPSERT 语法均复用 MySQL 实现。</p>
 */
public class H2Dialect extends MySqlDialect {

  public static final H2Dialect INSTANCE = new H2Dialect();

  @Override
  public com.alibaba.druid.DbType getDruidDbType() {
    return JdbcConstants.H2;
  }
}
