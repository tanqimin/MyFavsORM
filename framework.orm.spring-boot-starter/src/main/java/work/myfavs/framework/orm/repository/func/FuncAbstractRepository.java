package work.myfavs.framework.orm.repository.func;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Sql;

public class FuncAbstractRepository {

  protected DBTemplate dbTemplate;

  public FuncAbstractRepository(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
  }

  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  protected <TView> Stream<TView> find(Class<TView> viewClass,
      String sql,
      List<Object> params) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.func().find(viewClass, sql, params);
    }
  }

  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  protected <TView> Stream<TView> find(Class<TView> viewClass,
      Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.func().find(viewClass, sql);
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
   * @return 结果集
   */
  protected <TView> Stream<TView> findTop(Class<TView> viewClass,
      int top,
      String sql,
      List<Object> params) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.func().findTop(viewClass, top, sql, params);
    }
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
  protected <TView> Stream<TView> findTop(Class<TView> viewClass,
      int top,
      Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.func().findTop(viewClass, top, sql);
    }
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
  protected <TView> Optional<TView> get(Class<TView> viewClass,
      String sql,
      List<Object> params) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.func().get(viewClass, sql, params);
    }
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  protected <TView> Optional<TView> get(Class<TView> viewClass,
      Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.func().get(viewClass, sql);
    }
  }
}
