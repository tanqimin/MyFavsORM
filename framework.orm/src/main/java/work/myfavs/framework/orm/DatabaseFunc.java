package work.myfavs.framework.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.AttributeMeta;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DBConvert;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.SqlLog;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 用于构建支持Java8特性的查询
 */
public class DatabaseFunc {

  //SQL日志
  private static SqlLog sqlLog = null;

  private Database database;
  private Configuration configuration;

  public DatabaseFunc(Database database) {
    this.database = database;
    this.configuration = database.getConfiguration();

    if (sqlLog == null) {
      sqlLog = new SqlLog(configuration.getShowSql(), configuration.getShowResult());
    }
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
  public <TView> Stream<TView> find(Class<TView> viewClass,
      String sql,
      List<Object> params) {
    Metadata.get(viewClass);

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Stream<TView> result;

    try {
      sqlLog.showSql(sql, params);

      conn = this.database.open();
      pstmt = DBUtil.getPsForQuery(conn, sql, params);
      pstmt.setFetchSize(configuration.getFetchSize());
      rs = pstmt.executeQuery();

      result = DBConvert.toStream(viewClass, rs);

      sqlLog.showResult(result);
      return result;
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt, rs);
      this.database.close();
    }
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
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  public <TView> Stream<TView> find(Class<TView> viewClass,
      Sql sql) {

    return this.find(viewClass, sql.getSqlString(), sql.getParams());
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  public Stream<Record> find(String sql,
      List<Object> params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL
   * @return 结果集
   */
  public Stream<Record> find(Sql sql) {

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
      List<Object> params) {

    Sql querySql = this.database.getDialect().selectTop(1, top, sql, params);
    return this.find(viewClass, querySql);
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

    return this.findTop(viewClass, top, sql.getSqlString(), sql.getParams());
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top    行数
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  public Stream<Record> findTop(int top,
      String sql,
      List<Object> params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL
   * @return 结果集
   */
  public Stream<Record> findTop(int top,
      Sql sql) {

    return this.findTop(Record.class, top, sql);
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
      List<Object> params) {
    Iterator<TView> iterator = this.findTop(viewClass, 1, sql, params)
        .iterator();
    if (iterator.hasNext()) {
      return Optional.of(iterator.next());
    }
    return Optional.empty();
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

    return this.get(viewClass, sql.getSqlString(), sql.getParams());
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 记录
   */
  public Optional<Record> get(String sql,
      List<Object> params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL
   * @return 记录
   */
  public Optional<Record> get(Sql sql) {

    return this.get(Record.class, sql);
  }

  /**
   * 根据主键获取记录
   *
   * @param viewClass 结果类型
   * @param id        主键
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> Optional<TView> getById(Class<TView> viewClass,
      Object id) {

    ClassMeta classMeta = Metadata.get(viewClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();

    Sql sql = this.database.getDialect().select(viewClass)
        .where(Cond.eq(primaryKey.getColumnName(), id))
        .and(Cond.logicalDeleteCond(classMeta));

    return this.get(viewClass, sql);
  }


  /**
   * 根据指定字段获取记录
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> Optional<TView> getByField(Class<TView> viewClass,
      String field,
      Object param) {

    Sql sql = this.database.getDialect().select(viewClass)
        .where(Cond.eq(field, param))
        .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(viewClass, sql);
  }

  /**
   * 根据条件获取记录
   *
   * @param viewClass 结果类型
   * @param cond      条件
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> Optional<TView> getByCond(Class<TView> viewClass,
      Cond cond) {

    Sql sql = this.database.getDialect().select(viewClass)
        .where(cond)
        .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(viewClass, sql);
  }

  /**
   * 根据@Condition注解生成的条件查询记录
   *
   * @param viewClass 结果类型
   * @param object    包含@Condition注解Field的对象
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> Optional<TView> getByCondition(Class<TView> viewClass,
      Object object) {

    return this.getByCond(viewClass, Cond.create(object));
  }

  /**
   * 根据@Condition注解生成的条件查询记录
   *
   * @param viewClass      结果类型
   * @param object         包含@Condition注解Field的对象
   * @param conditionGroup 条件组名
   * @param <TView>        实体类型
   * @return 记录
   */
  public <TView> Optional<TView> getByCondition(Class<TView> viewClass,
      Object object,
      String conditionGroup) {

    return this.getByCond(viewClass, Cond.create(object, conditionGroup));
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param viewClass 结果类型
   * @param ids       主键ID集合
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> Stream<TView> findByIds(Class<TView> viewClass,
      List ids) {

    ClassMeta classMeta = Metadata.get(viewClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();
    Sql sql = this.database.getDialect().select(viewClass)
        .where(Cond.in(primaryKey.getColumnName(), ids, false))
        .and(Cond.logicalDeleteCond(classMeta));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> Stream<TView> findByField(Class<TView> viewClass,
      String field,
      Object param) {

    Sql sql = this.database.getDialect().select(viewClass)
        .where(Cond.eq(field, param))
        .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param params    参数集合
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> Stream<TView> findByField(Class<TView> viewClass,
      String field,
      List<Object> params) {

    Sql sql = this.database.getDialect().select(viewClass)
        .where(Cond.in(field, params, false))
        .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param cond      查询条件
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> Stream<TView> findByCond(Class<TView> viewClass,
      Cond cond) {

    Sql sql = this.database.getDialect().select(viewClass)
        .where(cond)
        .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据@Condition注解生成的条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param object    包含@Condition注解Field的对象
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> Stream<TView> findByCondition(Class<TView> viewClass,
      Object object) {

    return findByCond(viewClass, Cond.create(object));
  }

  /**
   * 根据@Condition注解生成的条件查询实体集合
   *
   * @param viewClass      结果类型
   * @param object         包含@Condition注解Field的对象
   * @param conditionGroup 条件组名
   * @param <TView>        实体类型
   * @return 实体集合
   */
  public <TView> Stream<TView> findByCondition(Class<TView> viewClass,
      Object object,
      String conditionGroup) {

    return findByCond(viewClass, Cond.create(object, conditionGroup));
  }
}
