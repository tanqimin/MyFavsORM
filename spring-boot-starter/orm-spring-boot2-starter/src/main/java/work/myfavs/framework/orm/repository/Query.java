package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;

import java.util.Collection;
import java.util.List;

/**
 * 查询器基类
 */
@SuppressWarnings("unused")
public class Query extends BaseRepository {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  public Query(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  /**
   * 执行SQL，并返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql) {

    return this.find(viewClass, sql, null);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  @Override
  public <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    return super.find(viewClass, sql);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  @Override
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {

    return super.find(viewClass, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  public List<Record> findRecords(String sql, Collection<?> params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL
   * @return 结果集
   */
  public List<Record> findRecords(Sql sql) {

    return this.find(Record.class, sql);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  @Override
  public <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params) {

    return super.findTop(viewClass, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  @Override
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {

    return super.findTop(viewClass, top, sql);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top    行数
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  public List<Record> findTopRecords(int top, String sql, Collection<?> params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL
   * @return 结果集
   */
  public List<Record> findTopRecords(int top, Sql sql) {

    return this.findTop(Record.class, top, sql);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  public <TView> TView get(Class<TView> viewClass, String sql) {

    return this.get(viewClass, sql, null);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  @Override
  public <TView> TView get(Class<TView> viewClass, Sql sql) {

    return super.get(viewClass, sql);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  @Override
  public <TView> TView get(Class<TView> viewClass, String sql, Collection<?> params) {

    return super.get(viewClass, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 记录
   */
  public Record getRecord(String sql, Collection<?> params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL
   * @return 记录
   */
  public Record getRecord(Sql sql) {

    return this.get(Record.class, sql);
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 行数
   */
  @Override
  public long count(String sql, Collection<?> params) {

    return super.count(sql, params);
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   * @return 行数
   */
  @Override
  public long count(Sql sql) {

    return super.count(sql);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  分页对象
   * @param <TView>   结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPageLite(viewClass, sql, params, pageable);
    }
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL
   * @param pageable  分页对象
   * @param <TView>   结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPageLite(viewClass, sql, pageable);
    }
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPageLite(viewClass, sql, params, enablePage, currentPage, pageSize);
    }
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(
        viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(
      Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(String sql, Collection<?> params, IPageable pageable) {

    return this.findPageLite(Record.class, sql, params, pageable);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql      SQL
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(Sql sql, IPageable pageable) {

    return this.findPageLite(Record.class, sql, pageable);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPage(viewClass, sql, params, enablePage, currentPage, pageSize);
    }
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return findPage(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  是否启用分页
   * @param <TView>   结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPage(viewClass, sql, params, pageable);
    }
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL
   * @param pageable  是否启用分页
   * @param <TView>   结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPage(viewClass, sql, pageable);
    }
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable 分页对象
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(String sql, Collection<?> params, IPageable pageable) {

    return this.findPage(Record.class, sql, params, pageable);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql      SQL
   * @param pageable 分页对象
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(Sql sql, IPageable pageable) {

    return this.findPage(Record.class, sql, pageable);
  }

  /**
   * 创建分页对象
   *
   * @param data         分页数据
   * @param currentPage  当前页码
   * @param pageSize     每页记录数
   * @param totalPages   总页数
   * @param totalRecords 总记录数
   * @param <TView>      分页对象数据类型泛型
   * @return 分页对象
   */
  public <TView> Page<TView> createPage(
      List<TView> data, long currentPage, long pageSize, long totalPages, long totalRecords) {

    return Page.create(data, currentPage, pageSize, totalPages, totalRecords);
  }

  /**
   * 创建简单分页对象实例
   *
   * @param data        分页数据
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     简单分页对象泛型
   * @return 简单分页对象
   */
  public <TView> PageLite<TView> createPageLite(List<TView> data, long currentPage, long pageSize) {

    return PageLite.create(data, currentPage, pageSize);
  }
}
