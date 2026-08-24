package work.myfavs.framework.orm.orm.component;

import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.orm.dialect.SqlDialect;
import work.myfavs.framework.orm.util.exception.PaginationException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 分页查询器，处理分页查询逻辑（Page 和 PageLite）
 */
public class OrmPager {

  private final OrmSelector selector;
  private final OrmSqlBuilder sqlBuilder;
  private final DBConfig dbConfig;
  private final SqlDialect dialect;

  /**
   * 构造 OrmPager 实例.
   *
   * @param selector   {@link OrmSelector} 实例
   * @param sqlBuilder {@link OrmSqlBuilder} 实例
   * @param dbConfig   {@link DBConfig} 实例
   * @param dialect    {@link SqlDialect} 方言实例
   */
  public OrmPager(OrmSelector selector, OrmSqlBuilder sqlBuilder, DBConfig dbConfig, SqlDialect dialect) {
    this.selector = selector;
    this.sqlBuilder = sqlBuilder;
    this.dbConfig = dbConfig;
    this.dialect = dialect;
  }

  /**
   * 执行 SQL 语句，返回 {@link PageLite} 简单分页结果集.
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL 语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    final Sql querySql = this.selectPage(enablePage, sql, params, currentPage, pageSize);
    final List<TView> data = this.selector.find(viewClass, querySql);
    return PageLite.create(data, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集.
   *
   * @param viewClass   返回的数据类型
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return this.findPageLite(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link PageLite} 简单分页结果集.
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL 语句
   * @param params    参数
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return this.findPageLite(viewClass, sql, params, pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集.
   *
   * @param viewClass 返回的数据类型
   * @param sql       {@link Sql}
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return this.findPageLite(viewClass, sql.toString(), sql.getParams(), pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回 {@link Page} 分页结果集（含总记录数）.
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL 语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    final Sql querySql = this.selectPage(enablePage, sql, params, currentPage, pageSize);
    final List<TView> data = this.selector.find(viewClass, querySql);

    long totalPages = 1;
    long totalRecords;

    if (enablePage) {
      totalRecords = this.selector.count(sql, params);
      totalPages = totalRecords / pageSize;

      if (totalRecords % pageSize != 0) {
        totalPages++;
      }
    } else {
      totalRecords = data.size();
    }

    return Page.create(data, currentPage, pageSize, totalPages, totalRecords);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 分页结果集（含总记录数）.
   *
   * @param viewClass   返回的数据类型
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return findPage(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link Page} 分页结果集（含总记录数）.
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL 语句
   * @param params    参数
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return findPage(viewClass, sql, params, pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 分页结果集（含总记录数）.
   *
   * @param viewClass 返回的数据类型
   * @param sql       {@link Sql}
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return findPage(viewClass, sql.toString(), sql.getParams(), pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回指定行数的结果集.
   *
   * @param viewClass 返回的数据类型
   * @param top       行数
   * @param sql       SQL 语句
   * @param params    参数
   * @param <TView>   结果类型泛型
   * @return 结果集
   */
  public <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params) {
    final Sql querySql = this.selectPage(true, sql, params, 1, top);
    return this.selector.find(viewClass, querySql);
  }

  /**
   * 执行 {@link Sql} 语句，返回指定行数的结果集.
   *
   * @param viewClass 返回的数据类型
   * @param top       行数
   * @param sql       {@link Sql}
   * @param <TView>   结果类型泛型
   * @return 结果集
   */
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {
    return this.findTop(viewClass, top, sql.toString(), sql.getParams());
  }

  private Sql selectPage(boolean enablePage, String sql, Collection<?> params, int currentPage, int pageSize) {
    if (!enablePage) {
      int maxPageSize = this.dbConfig.getMaxPageSize();
      if (maxPageSize <= 0) {
        return new Sql(sql, params);
      } else {
        return this.dialect.applyPageSql(sql, params, 1, maxPageSize);
      }
    }
    if (currentPage < 1) {
      throw new PaginationException("当前页码 (currentPage) 参数必须大于等于 1");
    }

    if (pageSize < 1) {
      throw new PaginationException("每页记录数 (pageSize) 参数必须大于等于 1");
    }

    int maxPageSize = this.dbConfig.getMaxPageSize();
    if (maxPageSize > 0 && pageSize > maxPageSize) {
      throw new PaginationException("每页记录数不能超出系统设置的最大记录数 %d", maxPageSize);
    }

    return this.dialect.applyPageSql(sql, params, currentPage, pageSize);
  }
}
