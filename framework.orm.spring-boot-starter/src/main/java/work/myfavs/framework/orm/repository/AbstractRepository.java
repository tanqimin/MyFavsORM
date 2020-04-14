package work.myfavs.framework.orm.repository;

import java.util.List;
import java.util.Map;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.repository.func.FuncAbstractRepository;

/**
 * 仓储基类
 */
abstract public class AbstractRepository {

  protected DBTemplate dbTemplate;

  private FuncAbstractRepository funcAbstractRepository;

  private AbstractRepository() {
  }

  public AbstractRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
  }

  public FuncAbstractRepository func() {
    if (funcAbstractRepository == null) {
      funcAbstractRepository = new FuncAbstractRepository(this.dbTemplate);
    }
    return funcAbstractRepository;
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
  protected <TView> List<TView> find(Class<TView> viewClass,
      String sql,
      List<Object> params) {

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
   * @return 结果集
   */
  protected <TView> List<TView> find(Class<TView> viewClass,
      Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.find(viewClass, sql);
    }
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param viewClass 结果集类型
   * @param keyField  返回Map的Key的字段，必须是viewClass中存在的字段
   * @param sql       SQL语句
   * @param params    SQL参数
   * @param <TView>   结果集类型泛型
   * @return Map
   */
  protected <TView> Map<Object, TView> findMap(Class<TView> viewClass,
      String keyField,
      String sql,
      List<Object> params) {
    try (Database conn = this.dbTemplate.open()) {
      return conn.findMap(viewClass, keyField, sql, params);
    }
  }

  /**
   * 执行SQL，并返回Map
   *
   * @param viewClass 结果集类型
   * @param keyField  返回Map的Key的字段，必须是viewClass中存在的字段
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   * @return Map
   */
  protected <TView> Map<Object, TView> findMap(Class<TView> viewClass,
      String keyField,
      Sql sql) {
    try (Database conn = this.dbTemplate.open()) {
      return conn.findMap(viewClass, keyField, sql);
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
  protected <TView> List<TView> findTop(Class<TView> viewClass,
      int top,
      String sql,
      List<Object> params) {

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
   * @return 结果集
   */
  protected <TView> List<TView> findTop(Class<TView> viewClass,
      int top,
      Sql sql) {

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
   * @return 记录
   */
  protected <TView> TView get(Class<TView> viewClass,
      String sql,
      List<Object> params) {

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
   * @return 记录
   */
  protected <TView> TView get(Class<TView> viewClass,
      Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.get(viewClass, sql);
    }
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 行数
   */
  protected long count(String sql,
      List<Object> params) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.count(sql, params);
    }
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   * @return 行数
   */
  protected long count(Sql sql) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.count(sql);
    }
  }

}
