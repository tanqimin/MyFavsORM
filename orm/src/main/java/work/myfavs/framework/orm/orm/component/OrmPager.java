package work.myfavs.framework.orm.orm.component;

import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.orm.strategy.PageStrategy;
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
  private final PageStrategy pageStrategy;

  public OrmPager(OrmSelector selector, OrmSqlBuilder sqlBuilder, DBConfig dbConfig, PageStrategy pageStrategy) {
    this.selector = selector;
    this.sqlBuilder = sqlBuilder;
    this.dbConfig = dbConfig;
    this.pageStrategy = pageStrategy;
  }

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

  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return this.findPageLite(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return this.findPageLite(viewClass, sql, params, pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return this.findPageLite(viewClass, sql.toString(), sql.getParams(), pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

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

  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return findPage(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return findPage(viewClass, sql, params, pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {
    Objects.requireNonNull(pageable);
    return findPage(viewClass, sql.toString(), sql.getParams(), pageable.getEnablePage(), pageable.getCurrentPage(), pageable.getPageSize());
  }

  public <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params) {
    final Sql querySql = this.selectPage(true, sql, params, 1, top);
    return this.selector.find(viewClass, querySql);
  }

  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {
    return this.findTop(viewClass, top, sql.toString(), sql.getParams());
  }

  private Sql selectPage(boolean enablePage, String sql, Collection<?> params, int currentPage, int pageSize) {
    if (!enablePage) {
      int maxPageSize = this.dbConfig.getMaxPageSize();
      if (maxPageSize <= 0L) {
        return new Sql(sql, params);
      } else {
        return this.pageStrategy.apply(sql, params, 1, maxPageSize);
      }
    }
    if (currentPage < 1) {
      throw new PaginationException("当前页码 (currentPage) 参数必须大于等于 1");
    }

    if (pageSize < 1) {
      throw new PaginationException("每页记录数 (pageSize) 参数必须大于等于 1");
    }

    int maxPageSize = this.dbConfig.getMaxPageSize();
    if (maxPageSize > 0L && pageSize > maxPageSize) {
      throw new PaginationException("每页记录数不能超出系统设置的最大记录数 %d", maxPageSize);
    }

    return this.pageStrategy.apply(sql, params, currentPage, pageSize);
  }
}
