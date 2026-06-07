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
 * 查询器基类，提供 Record 查询和分页查询的快捷方法.
 * <p>继承 {@link BaseRepository} 的所有只读查询能力，额外提供：</p>
 * <ul>
 *   <li>{@link Record} 类型的结果查询（{@link #findRecords}, {@link #getRecord} 等）</li>
 *   <li>分页查询（{@link Page} 和 {@link PageLite}）</li>
 *   <li>分页对象创建工具方法</li>
 * </ul>
 *
 * @see BaseRepository
 * @see Repository
 * @author tanqimin
 */
@SuppressWarnings("unused")
public class Query extends BaseRepository {

  /**
   * 构造方法.
   *
   * @param dbTemplate {@link DBTemplate} 实例
   */
  public Query(DBTemplate dbTemplate) {
    super(dbTemplate);
  }

  /**
   * 执行 SQL 查询，返回多行记录（无参数版本）.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       SQL 语句
   * @return 结果集列表
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql) {
    return this.find(viewClass, sql, null);
  }

  /** {@inheritDoc} */
  @Override
  public <TView> List<TView> find(Class<TView> viewClass, Sql sql) {
    return super.find(viewClass, sql);
  }

  /** {@inheritDoc} */
  @Override
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {
    return super.find(viewClass, sql, params);
  }

  /**
   * 执行 SQL 查询，返回 {@link Record} 类型的结果集.
   *
   * @param sql    SQL 语句
   * @param params 查询参数
   * @return {@link Record} 结果集列表
   */
  public List<Record> findRecords(String sql, Collection<?> params) {
    return this.find(Record.class, sql, params);
  }

  /**
   * 执行 SQL 查询，返回 {@link Record} 类型的结果集.
   *
   * @param sql {@link Sql} 构建器对象
   * @return {@link Record} 结果集列表
   */
  public List<Record> findRecords(Sql sql) {
    return this.find(Record.class, sql);
  }

  /** {@inheritDoc} */
  @Override
  public <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params) {
    return super.findTop(viewClass, top, sql, params);
  }

  /** {@inheritDoc} */
  @Override
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {
    return super.findTop(viewClass, top, sql);
  }

  /**
   * 查询指定行数的 {@link Record} 结果集.
   *
   * @param top    返回行数
   * @param sql    SQL 语句
   * @param params 查询参数
   * @return 最多 {@code top} 行的 {@link Record} 结果集列表
   */
  public List<Record> findTopRecords(int top, String sql, Collection<?> params) {
    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 查询指定行数的 {@link Record} 结果集.
   *
   * @param top 返回行数
   * @param sql {@link Sql} 构建器对象
   * @return 最多 {@code top} 行的 {@link Record} 结果集列表
   */
  public List<Record> findTopRecords(int top, Sql sql) {
    return this.findTop(Record.class, top, sql);
  }

  /**
   * 执行 SQL 查询，返回单行记录（无参数版本）.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       SQL 语句
   * @return 结果集对象，无记录时返回 {@code null}
   */
  public <TView> TView get(Class<TView> viewClass, String sql) {
    return this.get(viewClass, sql, null);
  }

  /** {@inheritDoc} */
  @Override
  public <TView> TView get(Class<TView> viewClass, Sql sql) {
    return super.get(viewClass, sql);
  }

  /** {@inheritDoc} */
  @Override
  public <TView> TView get(Class<TView> viewClass, String sql, Collection<?> params) {
    return super.get(viewClass, sql, params);
  }

  /**
   * 查询单行 {@link Record}.
   *
   * @param sql    SQL 语句
   * @param params 查询参数
   * @return {@link Record} 对象，无记录时返回 {@code null}
   */
  public Record getRecord(String sql, Collection<?> params) {
    return this.get(Record.class, sql, params);
  }

  /**
   * 查询单行 {@link Record}.
   *
   * @param sql {@link Sql} 构建器对象
   * @return {@link Record} 对象，无记录时返回 {@code null}
   */
  public Record getRecord(Sql sql) {
    return this.get(Record.class, sql);
  }

  /** {@inheritDoc} */
  @Override
  public long count(String sql, Collection<?> params) {
    return super.count(sql, params);
  }

  /** {@inheritDoc} */
  @Override
  public long count(Sql sql) {
    return super.count(sql);
  }

  /**
   * 执行分页查询，返回 {@link PageLite} 结果集（不含总记录数）.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       SQL 语句
   * @param params    查询参数
   * @param pageable  分页参数
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPageLite(viewClass, sql, params, pageable);
    }
  }

  /**
   * 执行分页查询，返回 {@link PageLite} 结果集（不含总记录数）.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       {@link Sql} 构建器对象
   * @param pageable  分页参数
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPageLite(viewClass, sql, pageable);
    }
  }

  /**
   * 执行分页查询，返回 {@link PageLite} 结果集（不含总记录数）.
   *
   * @param <TView>     结果集类型
   * @param viewClass   结果集类型对应的 {@link Class} 对象
   * @param sql         SQL 语句
   * @param params      查询参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
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
   * 执行分页查询，返回 {@link PageLite} 结果集（不含总记录数）.
   *
   * @param <TView>     结果集类型
   * @param viewClass   结果集类型对应的 {@link Class} 对象
   * @param sql         {@link Sql} 构建器对象
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return this.findPageLite(
        viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行分页查询，返回 {@link PageLite}{@code <Record>} 结果集（不含总记录数）.
   *
   * @param sql         SQL 语句
   * @param params      查询参数
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
   * 执行分页查询，返回 {@link PageLite}{@code <Record>} 结果集（不含总记录数）.
   *
   * @param sql         {@link Sql} 构建器对象
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
   * 执行分页查询，返回 {@link PageLite}{@code <Record>} 结果集（不含总记录数）.
   *
   * @param sql      SQL 语句
   * @param params   查询参数
   * @param pageable 分页参数
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(String sql, Collection<?> params, IPageable pageable) {
    return this.findPageLite(Record.class, sql, params, pageable);
  }

  /**
   * 执行分页查询，返回 {@link PageLite}{@code <Record>} 结果集（不含总记录数）.
   *
   * @param sql      {@link Sql} 构建器对象
   * @param pageable 分页参数
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(Sql sql, IPageable pageable) {
    return this.findPageLite(Record.class, sql, pageable);
  }

  /**
   * 执行分页查询，返回含总记录数的 {@link Page} 结果集.
   *
   * @param <TView>     结果集类型
   * @param viewClass   结果集类型对应的 {@link Class} 对象
   * @param sql         SQL 语句
   * @param params      查询参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
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
   * 执行分页查询，返回含总记录数的 {@link Page} 结果集.
   *
   * @param <TView>     结果集类型
   * @param viewClass   结果集类型对应的 {@link Class} 对象
   * @param sql         {@link Sql} 构建器对象
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return findPage(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行分页查询，返回含总记录数的 {@link Page} 结果集.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       SQL 语句
   * @param params    查询参数
   * @param pageable  分页参数
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPage(viewClass, sql, params, pageable);
    }
  }

  /**
   * 执行分页查询，返回含总记录数的 {@link Page} 结果集.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       {@link Sql} 构建器对象
   * @param pageable  分页参数
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findPage(viewClass, sql, pageable);
    }
  }

  /**
   * 执行分页查询，返回含总记录数的 {@code Page<Record>} 结果集.
   *
   * @param sql         SQL 语句
   * @param params      查询参数
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
   * 执行分页查询，返回含总记录数的 {@code Page<Record>} 结果集.
   *
   * @param sql         {@link Sql} 构建器对象
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行分页查询，返回含总记录数的 {@code Page<Record>} 结果集.
   *
   * @param sql      SQL 语句
   * @param params   查询参数
   * @param pageable 分页参数
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(String sql, Collection<?> params, IPageable pageable) {
    return this.findPage(Record.class, sql, params, pageable);
  }

  /**
   * 执行分页查询，返回含总记录数的 {@code Page<Record>} 结果集.
   *
   * @param sql      {@link Sql} 构建器对象
   * @param pageable 分页参数
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(Sql sql, IPageable pageable) {
    return this.findPage(Record.class, sql, pageable);
  }

  /**
   * 创建含总记录数的分页对象.
   *
   * @param <TView>      分页对象数据类型
   * @param data         当前页数据
   * @param currentPage  当前页码
   * @param pageSize     每页记录数
   * @param totalPages   总页数
   * @param totalRecords 总记录数
   * @return 分页对象
   */
  public <TView> Page<TView> createPage(
      List<TView> data, long currentPage, long pageSize, long totalPages, long totalRecords) {
    return Page.create(data, currentPage, pageSize, totalPages, totalRecords);
  }

  /**
   * 创建不含总记录数的简单分页对象.
   *
   * @param <TView>     分页对象数据类型
   * @param data        当前页数据
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页对象
   */
  public <TView> PageLite<TView> createPageLite(List<TView> data, long currentPage, long pageSize) {
    return PageLite.create(data, currentPage, pageSize);
  }
}
