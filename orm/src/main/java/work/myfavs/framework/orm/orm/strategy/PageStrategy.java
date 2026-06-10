package work.myfavs.framework.orm.orm.strategy;

import work.myfavs.framework.orm.meta.clause.Sql;

import java.util.Collection;

/**
 * 分页策略接口，根据数据库方言生成分页查询 SQL。
 * <p>框架内置了以下实现：</p>
 * <ul>
 *   <li>{@link MySqlPageStrategy} — 适用于 MySQL、H2（LIMIT offset, count）</li>
 *   <li>{@link OraclePageStrategy} — 适用于 Oracle（ROWNUM 嵌套查询）</li>
 *   <li>{@link SqlServerPageStrategy} — 适用于 SQL Server 2005~2008（ROW_NUMBER OVER）</li>
 *   <li>{@link SqlServer2012PageStrategy} — 适用于 SQL Server 2012+（OFFSET FETCH NEXT）</li>
 * </ul>
 *
 * @see MySqlPageStrategy
 * @see OraclePageStrategy
 * @see SqlServerPageStrategy
 * @see SqlServer2012PageStrategy
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
