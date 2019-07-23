package work.myfavs.framework.orm.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DBConvert;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.exception.DBException;

abstract public class AbstractRepository {

  protected IDialect   dialect;
  protected DBTemplate dbTemplate;

  private AbstractRepository() {}

  public AbstractRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
    dialect = dbTemplate.getDialect();
  }


  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  protected <TView> List<TView> find(Class<TView> viewClass, String sql, List<Object> params) {

    Metadata.get(viewClass);

    Connection        conn  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;

    try {
      conn = this.dbTemplate.createConnection();
      pstmt = params == null || params.size() == 0
          ? DBUtil.getPs(conn, sql)
          : DBUtil.getPs(conn, sql, params);
      rs = pstmt.executeQuery();
      return DBConvert.toList(viewClass, rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.dbTemplate.release(conn, pstmt, rs);
    }
  }

  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  protected <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    return this.find(viewClass, sql.getSql().toString(), sql.getParams());
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
  protected <TView> List<TView> findTop(Class<TView> viewClass, long top, String sql, List<Object> params) {

    Sql querySql = this.dialect.selectTop(1L, top, sql, params);
    return this.find(viewClass, querySql);
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
  protected <TView> List<TView> findTop(Class<TView> viewClass, long top, Sql sql) {

    return this.findTop(viewClass, top, sql.getSql().toString(), sql.getParams());
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
  protected <TView> TView get(Class<TView> viewClass, String sql, List<Object> params) {

    Iterator<TView> iterator = this.findTop(viewClass, 1L, sql, params).iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
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
  protected <TView> TView get(Class<TView> viewClass, Sql sql) {

    return this.get(viewClass, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 行数
   */
  protected long count(String sql, List<Object> params) {

    return this.get(Number.class, this.dialect.count(sql, params)).longValue();
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   *
   * @return 行数
   */
  protected long count(Sql sql) {

    return this.count(sql.getSql().toString(), sql.getParams());
  }

}
