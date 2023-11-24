package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Sql;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 仓储基类
 *
 * @author tanqimin
 */
public abstract class BaseRepository {

  protected DBTemplate dbTemplate;

  public BaseRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
  }

  public void setDbTemplate(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
  }

  public void setDbTemplate(String dsName) {
    this.dbTemplate = DBTemplate.get(dsName);
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
  protected <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.find(viewClass, sql, params);
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
  protected <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.find(viewClass, sql);
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
  protected <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, String sql, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.findMap(viewClass, keyField, sql, params);
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
  protected <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, Sql sql) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.findMap(viewClass, keyField, sql);
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
  protected <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.findTop(viewClass, top, sql, params);
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
  protected <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.findTop(viewClass, top, sql);
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
  protected <TView> TView get(Class<TView> viewClass, String sql, Collection<?> params) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.get(viewClass, sql, params);
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
  protected <TView> TView get(Class<TView> viewClass, Sql sql) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.get(viewClass, sql);
    }
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 行数
   */
  protected long count(String sql, Collection<?> params) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.count(sql, params);
    }
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   * @return 行数
   */
  protected long count(Sql sql) {

    try (Database database = this.dbTemplate.createDatabase()) {
      return database.count(sql);
    }
  }
}
