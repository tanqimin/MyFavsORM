package work.myfavs.framework.orm.repository;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * 仓储基类
 */
@Slf4j
abstract public class AbstractRepository {

  protected DBTemplate dbTemplate;

  private AbstractRepository() {}

  public AbstractRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
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

    try (Database conn = this.dbTemplate.open()) {
      return conn.find(viewClass, sql, params);
    }
  }

  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  protected <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.find(viewClass, sql);
    }
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
  protected <TView> List<TView> findTop(Class<TView> viewClass, int top, String sql, List<Object> params) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.findTop(viewClass, top, sql, params);
    }
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
  protected <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.findTop(viewClass, top, sql);
    }
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

    try (Database conn = this.dbTemplate.open()) {
      return conn.get(viewClass, sql, params);
    }
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

    try (Database conn = this.dbTemplate.open()) {
      return conn.get(viewClass, sql);
    }
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

    try (Database conn = this.dbTemplate.open()) {
      return conn.count(sql, params);
    }
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   *
   * @return 行数
   */
  protected long count(Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.count(sql);
    }
  }

}
