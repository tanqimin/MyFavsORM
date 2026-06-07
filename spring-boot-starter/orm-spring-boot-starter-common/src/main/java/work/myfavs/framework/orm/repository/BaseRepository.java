package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Sql;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 仓储基类，提供只读查询快捷方法.
 * <p>封装 {@code Orm.find/findTop/get/count/findMap} 等只读操作，
 * 自动管理 {@link Database} 的生命周期.</p>
 *
 * @see SimpleRepository
 * @see Query
 * @author tanqimin
 */
public abstract class BaseRepository extends SimpleRepository {

  /**
   * 构造方法.
   *
   * @param dbTemplate {@link DBTemplate} 实例
   */
  public BaseRepository(DBTemplate dbTemplate) {
    super(dbTemplate);
  }

  /**
   * 设置 {@link DBTemplate}.
   *
   * @param dbTemplate {@link DBTemplate} 实例
   */
  public void setDbTemplate(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
  }

  /**
   * 根据数据源名称设置 {@link DBTemplate}.
   *
   * @param dsName 数据源名称
   */
  public void setDbTemplate(String dsName) {
    this.dbTemplate = DBTemplate.get(dsName);
  }

  /**
   * 执行 SQL 查询，返回多行记录.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       SQL 语句
   * @param params    查询参数
   * @return 结果集列表
   */
  protected <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().find(viewClass, sql, params);
    }
  }

  /**
   * 执行 SQL 查询，返回多行记录.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       {@link Sql} 构建器对象
   * @return 结果集列表
   */
  protected <TView> List<TView> find(Class<TView> viewClass, Sql sql) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().find(viewClass, sql);
    }
  }

  /**
   * 执行 SQL 查询，返回以指定字段为键的 Map.
   *
   * @param <TKey>    Map 键类型
   * @param <TView>   Map 值类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param keyField  作为 Map 键的字段名，必须是 {@code viewClass} 中存在的字段
   * @param sql       SQL 语句
   * @param params    查询参数
   * @return 以 {@code keyField} 为键的结果集映射
   */
  protected <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, String sql, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findMap(viewClass, keyField, sql, params);
    }
  }

  /**
   * 执行 SQL 查询，返回以指定字段为键的 Map.
   *
   * @param <TKey>    Map 键类型
   * @param <TView>   Map 值类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param keyField  作为 Map 键的字段名，必须是 {@code viewClass} 中存在的字段
   * @param sql       {@link Sql} 构建器对象
   * @return 以 {@code keyField} 为键的结果集映射
   */
  protected <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, Sql sql) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findMap(viewClass, keyField, sql);
    }
  }

  /**
   * 执行 SQL 查询，返回指定行数的结果集.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param top       返回行数
   * @param sql       SQL 语句
   * @param params    查询参数
   * @return 最多 {@code top} 行的结果集列表
   */
  protected <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findTop(viewClass, top, sql, params);
    }
  }

  /**
   * 执行 SQL 查询，返回指定行数的结果集.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param top       返回行数
   * @param sql       {@link Sql} 构建器对象
   * @return 最多 {@code top} 行的结果集列表
   */
  protected <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findTop(viewClass, top, sql);
    }
  }

  /**
   * 执行 SQL 查询，返回单行记录.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       SQL 语句
   * @param params    查询参数
   * @return 结果集对象，无记录时返回 {@code null}
   */
  protected <TView> TView get(Class<TView> viewClass, String sql, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().get(viewClass, sql, params);
    }
  }

  /**
   * 执行 SQL 查询，返回单行记录.
   *
   * @param <TView>   结果集类型
   * @param viewClass 结果集类型对应的 {@link Class} 对象
   * @param sql       {@link Sql} 构建器对象
   * @return 结果集对象，无记录时返回 {@code null}
   */
  protected <TView> TView get(Class<TView> viewClass, Sql sql) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().get(viewClass, sql);
    }
  }

  /**
   * 获取 SQL 查询的行数.
   *
   * @param sql    SQL 语句
   * @param params 查询参数
   * @return 总行数
   */
  protected long count(String sql, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().count(sql, params);
    }
  }

  /**
   * 获取 SQL 查询的行数.
   *
   * @param sql {@link Sql} 构建器对象
   * @return 总行数
   */
  protected long count(Sql sql) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().count(sql);
    }
  }
}
