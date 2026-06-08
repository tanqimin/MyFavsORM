package work.myfavs.framework.orm.orm.strategy;

import work.myfavs.framework.orm.meta.clause.Sql;

import java.util.Collection;

/**
 * 分页策略接口：根据数据库方言生成分页查询 SQL
 */
@FunctionalInterface
public interface PageStrategy {

  /**
   * 将原始 SQL 转换为分页查询 SQL
   *
   * @param sql         原始 SQL
   * @param params      SQL 参数集合
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页查询 {@link Sql}
   */
  Sql apply(String sql, Collection<?> params, int currentPage, int pageSize);
}
