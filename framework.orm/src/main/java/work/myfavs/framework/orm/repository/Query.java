package work.myfavs.framework.orm.repository;

import cn.hutool.core.util.StrUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 查询器基类
 */
@Slf4j
public class Query
    extends AbstractRepository {


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
   *
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
   *
   * @return 结果集
   */
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
   *
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql, List<Object> params) {

    return super.find(viewClass, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 结果集
   */
  public List<Record> findRecords(String sql, List<Object> params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL
   *
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
   *
   * @return 结果集
   */
  public <TView> List<TView> findTop(Class<TView> viewClass, long top, String sql, List<Object> params) {

    return super.findTop(viewClass, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  public <TView> List<TView> findTop(Class<TView> viewClass, long top, Sql sql) {

    return super.findTop(viewClass, top, sql);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top    行数
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 结果集
   */
  public List<Record> findTopRecords(long top, String sql, List<Object> params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL
   *
   * @return 结果集
   */
  public List<Record> findTopRecords(long top, Sql sql) {

    return this.findTop(Record.class, top, sql);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param <TView>   结果集类型泛型
   *
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
   *
   * @return 记录
   */
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
   *
   * @return 记录
   */
  public <TView> TView get(Class<TView> viewClass, String sql, List<Object> params) {

    return super.get(viewClass, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 记录
   */
  public Record getRecord(String sql, List<Object> params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL
   *
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
   *
   * @return 行数
   */
  public long count(String sql, List<Object> params) {

    return super.count(sql, params);
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   *
   * @return 行数
   */
  public long count(Sql sql) {

    return super.count(sql);
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
   *
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, String sql, List<Object> params, boolean enablePage, long currentPage,
                                              long pageSize) {

    long        pagSize;
    Sql         querySql;
    List<TView> data;

    pagSize = pageSize;
    if (enablePage) {
      long maxPageSize = this.dbTemplate.getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException(StrUtil.format("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize));
      }
    } else {
      pagSize = -1L;
    }

    querySql = this.dialect.selectTop(currentPage, pagSize, sql, params);
    data = this.find(viewClass, querySql);

    return PageLite.createInstance(data, currentPage, pagSize);

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
   *
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, boolean enablePage, long currentPage, long pageSize) {

    return this.findPageLite(viewClass, sql.getSql().toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(String sql, List<Object> params, boolean enablePage, long currentPage, long pageSize) {

    return this.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(Sql sql, boolean enablePage, long currentPage, long pageSize) {

    return this.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
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
   *
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, String sql, List<Object> params, boolean enablePage, long currentPage,
                                      long pageSize) {

    long        pagSize;
    long        totalPages;
    long        totalRecords;
    Sql         querySql;
    List<TView> data;

    pagSize = pageSize;

    if (enablePage) {
      long maxPageSize = this.dbTemplate.getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException(StrUtil.format("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize));
      }
    } else {
      pagSize = -1L;
    }

    querySql = this.dialect.selectTop(currentPage, pagSize, sql, params);
    data = this.find(viewClass, querySql);

    if (!enablePage) {
      totalRecords = data.size();
      totalPages = 1L;
    } else {
      totalRecords = this.count(sql, params);
      totalPages = totalRecords / pagSize;

      if (totalRecords % pagSize != 0) {
        totalPages++;
      }
    }

//    if (enablePage && totalPages > 0 && currentPage > totalPages) {
//      return findPage(viewClass, sql, params, true, totalPages, pagSize);
//    }

    return Page.createInstance(data, currentPage, pagSize, totalPages, totalRecords);
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
   *
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, boolean enablePage, long currentPage, long pageSize) {

    return findPage(viewClass, sql.getSql().toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 分页结果集
   */
  public Page<Record> findRecordsPage(String sql, List<Object> params, boolean enablePage, long currentPage, long pageSize) {

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql, boolean enablePage, long currentPage, long pageSize) {

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

}