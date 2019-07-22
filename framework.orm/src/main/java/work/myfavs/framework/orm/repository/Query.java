package work.myfavs.framework.orm.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DBConvert;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.exception.DBException;

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
   * @param modelClass 结果集类型
   * @param sql        SQL语句
   * @param <TModel>   结果集类型泛型
   *
   * @return 结果集
   */
  public <TModel> List<TModel> find(Class<TModel> modelClass, String sql) {

    return this.find(modelClass, sql, null);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param modelClass 结果集类型
   * @param sql        SQL
   * @param <TModel>   结果集类型泛型
   *
   * @return 结果集
   */
  public <TModel> List<TModel> find(Class<TModel> modelClass, Sql sql) {

    return this.find(modelClass, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param modelClass 结果集类型
   * @param sql        SQL语句
   * @param params     参数
   * @param <TModel>   结果集类型泛型
   *
   * @return 结果集
   */
  public <TModel> List<TModel> find(Class<TModel> modelClass, String sql, List<Object> params) {

    Metadata.get(modelClass);

    Connection        conn  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;

    try {
      conn = this.dbTemplate.createConnection();
      pstmt = DBUtil.getPs(conn, sql, params.toArray());
      rs = pstmt.executeQuery();
      return DBConvert.toList(modelClass, rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.dbTemplate.release(conn, pstmt, rs);
    }
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param modelClass 结果集类型
   * @param sql        SQL语句
   * @param <TModel>   结果集类型泛型
   *
   * @return 记录
   */
  public <TModel> TModel get(Class<TModel> modelClass, String sql) {

    return this.get(modelClass, sql, null);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param modelClass 结果集类型
   * @param sql        SQL
   * @param <TModel>   结果集类型泛型
   *
   * @return 记录
   */
  public <TModel> TModel get(Class<TModel> modelClass, Sql sql) {

    return this.get(modelClass, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param modelClass 结果集类型
   * @param sql        SQL语句
   * @param params     参数
   * @param <TModel>   结果集类型泛型
   *
   * @return 记录
   */
  public <TModel> TModel get(Class<TModel> modelClass, String sql, List<Object> params) {

    List<TModel> list = this.find(modelClass, this.dialect.selectTop(1, 1, sql, params));

    Iterator<TModel> iterator = list.iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
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

    return this.get(Number.class, this.dialect.count(sql, params)).longValue();
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   *
   * @return 行数
   */
  public long count(Sql sql) {

    return this.count(sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param modelClass  返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    结果类型泛型
   *
   * @return 简单分页结果集
   */
  public <TModel> PageLite<TModel> findPageLite(Class<TModel> modelClass, String sql, List<Object> params, boolean enablePage,
                                                long currentPage, long pageSize) {

    long         pagSize;
    Sql          querySql;
    List<TModel> data;

    pagSize = pageSize;
    if (!enablePage) {
      pagSize = -1L;
    }

    querySql = this.dialect.selectTop(currentPage, pagSize, sql, params);
    data = this.find(modelClass, querySql);

    return PageLite.createInstance(data, currentPage, pagSize);

  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param modelClass  返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    结果类型泛型
   *
   * @return 简单分页结果集
   */
  public <TModel> PageLite<TModel> findPageLite(Class<TModel> modelClass, Sql sql, boolean enablePage, long currentPage, long pageSize) {

    return this.findPageLite(modelClass, sql.getSql().toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param modelClass  返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    结果类型泛型
   *
   * @return 分页结果集
   */
  public <TModel> Page<TModel> findPage(Class<TModel> modelClass, String sql, List<Object> params, boolean enablePage, long currentPage,
                                        long pageSize) {

    long         pagSize;
    long         totalPages;
    long         totalRecords;
    Sql          querySql;
    List<TModel> data;

    pagSize = pageSize;

    if (!enablePage) {
      pagSize = -1L;
    }

    querySql = this.dialect.selectTop(currentPage, pagSize, sql, params);
    data = this.find(modelClass, querySql);

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

    if (enablePage && totalPages > 0 && currentPage > totalPages) {
      return findPage(modelClass, sql, params, true, totalPages, pagSize);
    }

    return Page.createInstance(data, currentPage, pagSize, totalPages, totalRecords);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param modelClass  返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    结果类型泛型
   *
   * @return 分页结果集
   */
  public <TModel> Page<TModel> findPage(Class<TModel> modelClass, Sql sql, boolean enablePage, long currentPage, long pageSize) {

    return findPage(modelClass, sql.getSql().toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

}
