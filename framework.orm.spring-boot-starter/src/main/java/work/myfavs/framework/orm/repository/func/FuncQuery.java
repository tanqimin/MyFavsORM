package work.myfavs.framework.orm.repository.func;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Sql;

@SuppressWarnings("unchecked")
public class FuncQuery extends FuncBaseRepository {

  public FuncQuery(DBTemplate dbTemplate) {
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
  public <TView> Stream<TView> find(Class<TView> viewClass,
      String sql) {

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
  public <TView> Stream<TView> find(Class<TView> viewClass,
      Sql sql) {

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
  public <TView> Stream<TView> find(Class<TView> viewClass,
      String sql,
      Collection params) {

    return super.find(viewClass, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  public Stream<Record> findRecord(String sql,
      Collection params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL
   * @return 结果集
   */
  public Stream<Record> findRecord(Sql sql) {

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
  public <TView> Stream<TView> findTop(Class<TView> viewClass,
      int top,
      String sql,
      Collection params) {

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
  public <TView> Stream<TView> findTop(Class<TView> viewClass,
      int top,
      Sql sql) {

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
  public Stream<Record> findRecordTop(int top,
      String sql,
      Collection params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL
   * @return 结果集
   */
  public Stream<Record> findRecordTop(int top,
      Sql sql) {

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
  public <TView> Optional<TView> get(Class<TView> viewClass,
      String sql) {

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
  public <TView> Optional<TView> get(Class<TView> viewClass,
      Sql sql) {

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
  public <TView> Optional<TView> get(Class<TView> viewClass,
      String sql,
      Collection params) {

    return super.get(viewClass, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 记录
   */
  public Optional<Record> getRecord(String sql,
      Collection params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL
   * @return 记录
   */
  public Optional<Record> getRecord(Sql sql) {

    return this.get(Record.class, sql);
  }
}
