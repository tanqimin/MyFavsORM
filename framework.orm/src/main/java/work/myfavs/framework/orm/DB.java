package work.myfavs.framework.orm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.TableAlias;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.Attributes;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.SqlLog;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;
import work.myfavs.framework.orm.util.func.ThrowingRunnable;
import work.myfavs.framework.orm.util.func.ThrowingSupplier;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库操作对象
 *
 * @author tanqimin
 */
@SuppressWarnings("unchecked")
public class DB {

  private static final Logger log = LoggerFactory.getLogger(DB.class);
  private final SqlLog sqlLog;
  private final DBTemplate dbTemplate;
  private final IDatabase database;

  private DB(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
    database = new Database(dbTemplate);
    sqlLog = new SqlLog(database.dbConfig().getShowSql(), database.dbConfig().getShowResult());
  }

  public static DB conn(DBTemplate dbTemplate) {
    return new DB(dbTemplate);
  }

  public static DB conn(String dsName) {
    return new DB(DBTemplateContext.get(dsName));
  }

  public static DB conn() {
    return conn(DBConfig.DEFAULT_DATASOURCE_NAME);
  }

  public <R> R tx(ThrowingFunction<DB, R, SQLException> function) {
    DB db = this;
    return database.tx(() -> function.apply(db));
  }

  public void tx(ThrowingConsumer<DB, SQLException> consumer) {
    DB db = this;
    database.tx(() -> consumer.accept(db));
  }

  public <R> R tx(ThrowingSupplier<R, SQLException> supplier) {
    return database.tx(supplier);
  }

  public void tx(ThrowingRunnable<SQLException> runnable) {
    this.database.tx(runnable);
  }

  /**
   * 是否使用SQL Server数据库
   *
   * @return 如果当前数据库为SQL Server，返回true，否则返回false
   */
  private boolean isSqlServer() {
    return StrUtil.equals(this.database.dbConfig().getDbType(), DbType.SQL_SERVER)
        || StrUtil.equals(this.database.dbConfig().getDbType(), DbType.SQL_SERVER_2012);
  }

  /**
   * 调用存储过程
   *
   * @param sql 调用存储过程语句，如：{ call proc_name(?,?,?)}
   * @param func func
   * @param <TResult> 结果
   * @return TResult
   */
  public <TResult> TResult call(
      String sql, ThrowingFunction<CallableStatement, TResult, SQLException> func) {
    sqlLog.showSql(sql, null);
    TResult result = this.database.call(sql, func);
    sqlLog.showResult(result);
    return result;
  }

  /**
   * 调用存储过程
   *
   * @param sql 调用存储过程语句，如：{ call proc_name(?,?,?)}
   * @param func func
   * @param queryTimeout 超时时间
   * @param <TResult> 结果
   * @return TResult
   */
  public <TResult> TResult call(
      String sql,
      ThrowingFunction<CallableStatement, TResult, SQLException> func,
      int queryTimeout) {
    sqlLog.showSql(sql, null);
    TResult result = this.database.call(sql, func, queryTimeout);
    sqlLog.showResult(result);
    return result;
  }

  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql SQL语句
   * @param params 参数
   * @param <TView> 结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection params) {

    sqlLog.showSql(sql, params);
    List<TView> result = database.find(viewClass, sql, params);
    sqlLog.showResult(result);
    return result;
  }

  /**
   * 执行SQL，并返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql SQL语句
   * @param <TView> 结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql) {

    return this.find(viewClass, sql, null);
  }

  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql SQL
   * @param <TView> 结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    return this.find(viewClass, sql.toString(), sql.getParams());
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 结果集
   */
  public List<Record> find(String sql, Collection params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL
   * @return 结果集
   */
  public List<Record> find(Sql sql) {

    return this.find(Record.class, sql);
  }

  /**
   * 执行SQL，并返回Map
   *
   * @param viewClass 结果集类型
   * @param keyField 返回Map的Key的字段，必须是viewClass中存在的字段
   * @param sql SQL语句
   * @param params SQL参数
   * @param <TView> 结果集类型泛型
   * @return Map
   */
  public <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, String sql, Collection params) {
    final PropDesc prop = BeanUtil.getBeanDesc(viewClass).getProp(keyField);
    if (prop == null) {
      throw new DBException("Class {} not exist Prop named {}", viewClass.getName(), keyField);
    }

    return this.find(viewClass, sql, params).parallelStream()
        .collect(Collectors.toMap(tView -> BeanUtil.getProperty(tView, keyField), tView -> tView));
  }

  /**
   * 执行SQL，并返回Map
   *
   * @param viewClass 结果集类型
   * @param keyField 返回Map的Key的字段，必须是viewClass中存在的字段
   * @param sql SQL
   * @param <TView> 结果集类型泛型
   * @return Map
   */
  public <TKey, TView> Map<TKey, TView> findMap(Class<TView> viewClass, String keyField, Sql sql) {
    return findMap(viewClass, keyField, sql.toString(), sql.getParams());
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top 行数
   * @param sql SQL语句
   * @param params 参数
   * @param <TView> 结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection params) {

    Sql querySql = database.dialect().selectPage(1, top, sql, params);
    return this.find(viewClass, querySql);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top 行数
   * @param sql SQL
   * @param <TView> 结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {

    return this.findTop(viewClass, top, sql.toString(), sql.getParams());
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL语句
   * @param params 参数
   * @return 结果集
   */
  public List<Record> findTop(int top, String sql, Collection params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL
   * @return 结果集
   */
  public List<Record> findTop(int top, Sql sql) {

    return this.findTop(Record.class, top, sql);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql SQL语句
   * @param params 参数
   * @param <TView> 结果集类型泛型
   * @return 记录
   */
  public <TView> TView get(Class<TView> viewClass, String sql, Collection params) {

    Iterator<TView> iterator = this.find(viewClass, sql, params).iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql SQL
   * @param <TView> 结果集类型泛型
   * @return 记录
   */
  public <TView> TView get(Class<TView> viewClass, Sql sql) {

    return this.get(viewClass, sql.toString(), sql.getParams());
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 记录
   */
  public Record get(String sql, Collection params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL
   * @return 记录
   */
  public Record get(Sql sql) {

    return this.get(Record.class, sql);
  }

  /**
   * 根据主键获取记录
   *
   * @param viewClass 结果类型
   * @param id 主键
   * @param <TView> 实体类型
   * @return 记录
   */
  public <TView> TView getById(Class<TView> viewClass, Object id) {

    ClassMeta classMeta = Metadata.get(viewClass);
    Attribute primaryKey = classMeta.checkPrimaryKey();

    Sql sql =
        this.database
            .dialect()
            .select(viewClass)
            .where(Cond.eq(primaryKey.getColumnName(), id))
            .and(Cond.logicalDeleteCond(classMeta));

    return this.get(viewClass, sql);
  }

  /**
   * 根据指定字段获取记录
   *
   * @param viewClass 结果类型
   * @param field 字段名
   * @param param 参数
   * @param <TView> 实体类型
   * @return 记录
   */
  public <TView> TView getByField(Class<TView> viewClass, String field, Object param) {

    Sql sql =
        this.database
            .dialect()
            .select(viewClass)
            .where(Cond.eq(field, param, false))
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(viewClass, sql);
  }

  /**
   * 根据条件获取记录
   *
   * @param viewClass 结果类型
   * @param cond 条件
   * @param <TView> 实体类型
   * @return 记录
   */
  public <TView> TView getByCond(Class<TView> viewClass, Cond cond) {

    Sql sql =
        this.database
            .dialect()
            .select(viewClass)
            .where()
            .and(cond)
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(viewClass, sql);
  }

  /**
   * 根据@Condition注解生成的条件查询记录
   *
   * @param viewClass 结果类型
   * @param object 包含@Condition注解Field的对象
   * @param <TView> 实体类型
   * @return 记录
   */
  public <TView> TView getByCondition(Class<TView> viewClass, Object object) {

    return this.getByCond(viewClass, Cond.create(object));
  }

  /**
   * 根据@Condition注解生成的条件查询记录
   *
   * @param viewClass 结果类型
   * @param object 包含@Condition注解Field的对象
   * @param conditionGroup 条件组名
   * @param <TView> 实体类型
   * @return 记录
   */
  public <TView> TView getByCondition(
      Class<TView> viewClass, Object object, String conditionGroup) {

    return this.getByCond(viewClass, Cond.create(object, conditionGroup));
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param viewClass 结果类型
   * @param ids 主键ID集合
   * @param <TView> 实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByIds(Class<TView> viewClass, Collection ids) {

    ClassMeta classMeta = Metadata.get(viewClass);
    Attribute primaryKey = classMeta.checkPrimaryKey();
    Sql sql =
        this.database
            .dialect()
            .select(viewClass)
            .where()
            .and(Cond.in(primaryKey.getColumnName(), ids, false))
            .and(Cond.logicalDeleteCond(classMeta));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field 字段名
   * @param param 参数
   * @param <TView> 实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Object param) {

    Sql sql =
        this.database
            .dialect()
            .select(viewClass)
            .where(Cond.eq(field, param, false))
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field 字段名
   * @param params 参数集合
   * @param <TView> 实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Collection params) {

    Sql sql =
        this.database
            .dialect()
            .select(viewClass)
            .where()
            .and(Cond.in(field, params, false))
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param cond 查询条件
   * @param <TView> 实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCond(Class<TView> viewClass, Cond cond) {

    Sql sql =
        this.database
            .dialect()
            .select(viewClass)
            .where()
            .and(cond)
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据@Condition注解生成的条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param object 包含@Condition注解Field的对象
   * @param <TView> 实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCondition(Class<TView> viewClass, Object object) {

    return findByCond(viewClass, Cond.create(object));
  }

  /**
   * 根据@Condition注解生成的条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param object 包含@Condition注解Field的对象
   * @param conditionGroup 条件组名
   * @param <TView> 实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCondition(
      Class<TView> viewClass, Object object, String conditionGroup) {

    return findByCond(viewClass, Cond.create(object, conditionGroup));
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 行数
   */
  public long count(String sql, Collection params) {

    Sql countSql = this.database.dialect().count(sql, params);
    return this.get(Number.class, countSql).longValue();
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   * @return 行数
   */
  public long count(Sql sql) {

    return this.count(sql.toString(), sql.getParams());
  }

  /**
   * 根据条件获取查询的行数
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond 条件
   * @param <TView> 查询的数据表、视图对应的Java View类型
   * @return 行数
   */
  public <TView> long countByCond(Class<TView> viewClass, Cond cond) {

    Sql sql =
        this.database
            .dialect()
            .count(viewClass)
            .where()
            .and(cond)
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(Number.class, sql).longValue();
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql SQL
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public boolean exists(Sql sql) {

    return exists(sql.toString(), sql.getParams());
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public boolean exists(String sql, Collection params) {

    return this.count(sql, params) > 0L;
  }

  /**
   * 判断实体（根据ID）是否存在
   *
   * @param modelClass 实体类型
   * @param entity 实体
   * @param <TModel> 实体类型泛型
   * @return 存在返回true，不存在返回false
   */
  public <TModel> boolean exists(Class<TModel> modelClass, TModel entity) {
    if (entity == null) {
      return false;
    }

    Attribute primaryKey = Metadata.get(modelClass).checkPrimaryKey();
    Object pkVal = ReflectUtil.getFieldValue(entity, primaryKey.getFieldName());
    Sql existSql =
        this.database.dialect().count(modelClass).where(Cond.eq(primaryKey.getColumnName(), pkVal));
    return exists(existSql);
  }

  /**
   * 根据条件判断是否存在符合条件的数据
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond 条件
   * @param <TView> 查询的数据表、视图对应的Java View类型
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public <TView> boolean existsByCond(Class<TView> viewClass, Cond cond) {

    return this.countByCond(viewClass, cond) > 0L;
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL语句
   * @param params 参数
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @param <TView> 结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass,
      String sql,
      Collection params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    int pagSize;
    Sql querySql;
    List<TView> data;

    pagSize = pageSize;
    if (enablePage) {
      if (currentPage < 1) {
        throw new DBException("当前页码 (currentPage) 参数必须大于等于 1");
      }

      if (pageSize < 1) {
        throw new DBException("每页记录数 (pageSize) 参数必须大于等于 1");
      }

      long maxPageSize = this.database.dbConfig().getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize);
      }
    } else {
      pagSize = -1;
    }

    querySql = this.database.dialect().selectPage(currentPage, pagSize, sql, params);
    data = this.find(viewClass, querySql);

    return PageLite.createInstance(this.dbTemplate, data, currentPage, pagSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @param <TView> 结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(
        viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL语句
   * @param params 参数
   * @param pageable 分页对象
   * @param <TView> 结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection params, IPageable pageable) {

    return this.findPageLite(
        viewClass,
        sql,
        params,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL
   * @param pageable 分页对象
   * @param <TView> 结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {

    return this.findPageLite(
        viewClass,
        sql.toString(),
        sql.getParams(),
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql SQL语句
   * @param params 参数
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(
      String sql, Collection params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql SQL
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql SQL语句
   * @param params 参数
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(String sql, Collection params, IPageable pageable) {

    return this.findPageLite(Record.class, sql, params, pageable);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql SQL
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(Sql sql, IPageable pageable) {

    return this.findPageLite(Record.class, sql, pageable);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL语句
   * @param params 参数
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @param <TView> 结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass,
      String sql,
      Collection params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    int pagSize;
    long totalPages;
    long totalRecords;
    Sql querySql;
    List<TView> data;

    pagSize = pageSize;

    if (enablePage) {
      if (currentPage < 1) {
        throw new DBException("当前页码 (currentPage) 参数必须大于等于 1");
      }

      if (pageSize < 1) {
        throw new DBException("每页记录数 (pageSize) 参数必须大于等于 1");
      }

      long maxPageSize = this.database.dbConfig().getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize);
      }
    } else {
      pagSize = -1;
    }

    querySql = this.database.dialect().selectPage(currentPage, pagSize, sql, params);
    data = this.find(viewClass, querySql);

    if (!enablePage) {
      totalRecords = data.size();
      totalPages = 1;
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

    return Page.createInstance(
        this.dbTemplate, data, currentPage, pagSize, totalPages, totalRecords);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @param <TView> 结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return findPage(
        viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL语句
   * @param params 参数
   * @param pageable 可分页对象
   * @param <TView> 结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection params, IPageable pageable) {

    return findPage(
        viewClass,
        sql,
        params,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql SQL
   * @param pageable 可分页对象
   * @param <TView> 结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {

    return findPage(
        viewClass,
        sql.toString(),
        sql.getParams(),
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql SQL语句
   * @param params 参数
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @return 分页结果集
   */
  public Page<Record> findPage(
      String sql, Collection params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql SQL
   * @param enablePage 是否启用分页
   * @param currentPage 当前页码
   * @param pageSize 每页记录数
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql SQL语句
   * @param params 参数
   * @param pageable 可分页对象
   * @return 分页结果集
   */
  public Page<Record> findPage(String sql, Collection params, IPageable pageable) {

    return this.findPage(
        Record.class,
        sql,
        params,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql SQL
   * @param pageable 可分页对象
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql, IPageable pageable) {

    return this.findPage(
        Record.class,
        sql,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL语句
   * @param params 参数
   * @param queryTimeOut 超时时间
   * @return 影响行数
   */
  public int execute(String sql, Collection params, int queryTimeOut) {

    sqlLog.showSql(sql, params);
    int result = this.database.execute(sql, params, queryTimeOut);
    this.sqlLog.showAffectedRows(result);
    return result;
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 影响行数
   */
  public int execute(String sql, Collection params) {

    return execute(sql, params, this.database.dbConfig().getQueryTimeout());
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   * @param queryTimeout 超时时间
   * @return 影响行数
   */
  public int execute(Sql sql, int queryTimeout) {

    return this.execute(sql.toString(), sql.getParams(), queryTimeout);
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   * @return 影响行数
   */
  public int execute(Sql sql) {

    return this.execute(sql, this.database.dbConfig().getQueryTimeout());
  }

  /**
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList) {
    int sqlCnt = sqlList.size();
    int[] results = new int[sqlCnt];

    return tx(
        db -> {
          for (int i = 0; i < sqlCnt; i++) {
            results[i] = execute(sqlList.get(i));
          }
          return results;
        });
  }

  /**
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   * @param queryTimeout 超时时间
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList, int queryTimeout) {
    int sqlCnt = sqlList.size();
    int[] results = new int[sqlCnt];

    return tx(
        () -> {
          for (int i = 0; i < sqlCnt; i++) {
            results[i] = execute(sqlList.get(i), queryTimeout);
          }
          return results;
        });
  }

  /**
   * 创建实体
   *
   * @param modelClass 实体类型
   * @param entity 实体
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass, TModel entity) {

    int result = 0;
    if (entity == null) {
      return result;
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    Attribute primaryKey = classMeta.checkPrimaryKey();
    GenerationType strategy;
    String pkFieldName;
    boolean autoGeneratedPK = false;

    Sql sql;

    pkFieldName = primaryKey.getFieldName();
    strategy = classMeta.getStrategy();
    /*
    如果数据库主键策略为非自增，那么需要加入主键值作为参数
    获取实体主键标识字段是否为null：
    1.ASSIGNED 不允许为空；
    2.UUID、SNOW_FLAKE如果主键标识字段为空，则生成值；
    */
    if (strategy == GenerationType.IDENTITY) {
      autoGeneratedPK = true;
    } else {
      Object pkVal = ReflectUtil.getFieldValue(entity, pkFieldName);
      if (pkVal == null) {
        if (strategy == GenerationType.ASSIGNED) {
          throw new DBException("Assigned ID can not be null.");
        } else if (strategy == GenerationType.UUID) {
          pkVal = uuid();
        } else if (strategy == GenerationType.SNOW_FLAKE) {
          pkVal = snowFlakeId();
        }

        ReflectUtil.setFieldValue(entity, pkFieldName, pkVal);
      }
    }

    sql = this.database.dialect().insert(modelClass, entity);

    this.sqlLog.showSql(sql.toString(), sql.getParams());
    result =
        this.database.create(
            sql.toString(),
            sql.getParams(),
            autoGeneratedPK,
            rs -> {
              if (rs.next()) {
                ReflectUtil.setFieldValue(entity, pkFieldName, rs.getObject(1));
              }
            });
    this.sqlLog.showAffectedRows(result);
    return result;
  }

  public long snowFlakeId() {
    return dbTemplate.getPkGenerator().nextSnowFakeId();
  }

  /**
   * 批量创建实体
   *
   * @param modelClass 实体类型
   * @param entities 实体集合
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass, Collection<TModel> entities) {
    if (CollectionUtil.isEmpty(entities)) {
      return 0;
    }

    ClassMeta classMeta = Metadata.get(modelClass);

    final boolean isMySQL = this.database.dbConfig().getDbType().equals(DbType.MYSQL);
    final boolean isIdentity = classMeta.getStrategy().equals(GenerationType.IDENTITY);

    if (!isMySQL) {
      /*
       * 此处处理了一个MSSQL的JDBC驱动问题，当批量保存时，不能返回KEY，所以使用传统的方法遍历
       * 请参考： @see <a href="http://stackoverflow.com/questions/13641832/getgeneratedkeys-after-preparedstatement-executebatch">stackoverflow</a>
       */
      if (isSqlServer() && isIdentity) {
        int result = 0;
        for (TModel entity : entities) {
          result += create(modelClass, entity);
        }
        return result;
      }
      return createInJdbcBatch(classMeta, entities);
    }

    if (isIdentity) {
      return createInJdbcBatch(classMeta, entities);
    }

    return createInSqlBatch(classMeta, entities);
  }

  /**
   * 使用SQL语句的批量创建方法 insert into table (f1, f2, f3) values (?,?,?),(?,?,?)...(?,?,?)
   *
   * @param classMeta 实体类
   * @param entities 实体
   * @param <TModel> 实体类类型
   * @return 记录数
   */
  private <TModel> int createInSqlBatch(ClassMeta classMeta, Collection<TModel> entities) {

    int result = 0;

    final Attributes updateAttributes = classMeta.getUpdateAttributes();
    final GenerationType strategy = classMeta.getStrategy();
    final Attribute primaryKey = classMeta.checkPrimaryKey();
    final String pkFieldName = primaryKey.getFieldName();

    final List<Sql> sqlList = new ArrayList<>();

    final int batchSize = this.database.dbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);

    for (Iterator<List<TModel>> ei = batchList.iterator(); ei.hasNext(); ) {
      List<TModel> entityList = ei.next();

      boolean insertClauseCompleted = false;
      String tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
      Sql insertClause = Sql.New(StrUtil.format("INSERT INTO {} (", tableName));
      Sql valuesClause = Sql.New(") VALUES ");

      for (Iterator<TModel> mi = entityList.iterator(); mi.hasNext(); ) {
        TModel entity = mi.next();

        Object pkVal = ReflectUtil.getFieldValue(entity, pkFieldName);
        if (pkVal == null) {
          if (strategy == GenerationType.ASSIGNED) {
            throw new DBException("Assigned ID can not be null.");
          } else if (strategy == GenerationType.UUID) {
            pkVal = uuid();
          } else if (strategy == GenerationType.SNOW_FLAKE) {
            pkVal = snowFlakeId();
          }
          ReflectUtil.setFieldValue(entity, pkFieldName, pkVal);
        }
        if (insertClauseCompleted == false) {
          insertClause.append(primaryKey.getColumnName() + ",");
        }
        valuesClause.append("(?,", pkVal);

        for (Attribute attr : updateAttributes.values()) {
          if (insertClauseCompleted == false) {
            insertClause.append(attr.getColumnName() + ",");
          }
          valuesClause.append("?,", ReflectUtil.getFieldValue(entity, attr.getFieldName()));
        }

        if (classMeta.needAppendLogicalDeleteField()) {
          if (insertClauseCompleted == false) {
            insertClause.append(classMeta.getLogicalDeleteField() + ",");
          }
          valuesClause.append("?,", 0);
        }

        if (insertClauseCompleted == false) {
          insertClause.deleteLastChar(",");
          insertClauseCompleted = true;
        }
        valuesClause.deleteLastChar(",");
        valuesClause.append("),");
      }

      valuesClause.deleteLastChar(",");
      sqlList.add(insertClause.append(valuesClause));
    }

    for (Sql batchSql : sqlList) {
      result += this.execute(batchSql);
    }
    return result;
  }

  private <TModel> int createInJdbcBatch(ClassMeta classMeta, Collection<TModel> entities) {

    int result = 0;
    GenerationType strategy;
    String pkFieldName;
    Object pkVal;
    boolean autoGeneratedPK = false;
    Attributes updateAttributes;
    Sql sql;
    Collection<Collection> paramsList;
    Collection params;

    Attribute primaryKey = classMeta.checkPrimaryKey();

    pkFieldName = primaryKey.getFieldName();
    strategy = classMeta.getStrategy();
    updateAttributes = classMeta.getUpdateAttributes();
    sql = this.database.dialect().insert(classMeta.getClazz());
    paramsList = new LinkedList<>();

    if (strategy == GenerationType.IDENTITY) {
      autoGeneratedPK = true;
    }

    for (Iterator<TModel> iterator = entities.iterator(); iterator.hasNext(); ) {
      TModel entity = iterator.next();
      params = new LinkedList<>();

      /*
      如果数据库主键策略为非自增，那么需要加入主键值作为参数
      获取实体主键标识字段是否为null：
      1.ASSIGNED 不允许为空；
      2.UUID、SNOW_FLAKE如果主键标识字段为空，则生成值；
      */
      if (strategy != GenerationType.IDENTITY) {
        pkVal = ReflectUtil.getFieldValue(entity, pkFieldName);

        if (pkVal == null) {
          if (strategy == GenerationType.ASSIGNED) {
            throw new DBException("Assigned ID can not be null.");
          } else if (strategy == GenerationType.UUID) {
            pkVal = uuid();
          } else if (strategy == GenerationType.SNOW_FLAKE) {
            pkVal = snowFlakeId();
          }

          ReflectUtil.setFieldValue(entity, pkFieldName, pkVal);
        }

        params.add(pkVal);
      }

      for (Attribute attr : updateAttributes.values()) {
        params.add(ReflectUtil.getFieldValue(entity, attr.getFieldName()));
      }

      paramsList.add(params);
    }
    this.sqlLog.showBatchSql(sql.toString(), paramsList);

    result =
        this.database.createBatch(
            sql.toString(),
            paramsList,
            autoGeneratedPK,
            rs -> {
              for (TModel tModel : entities) {
                if (rs.next()) {
                  ReflectUtil.setFieldValue(tModel, pkFieldName, rs.getObject(1));
                }
              }
            });

    this.sqlLog.showAffectedRows(result);
    return result;
  }

  public String uuid() {
    return this.dbTemplate.getPkGenerator().nextUUID();
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity 实体
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, TModel entity) {

    if (entity == null) {
      return 0;
    }
    Sql sql =
        this.database
            .dialect()
            .update(modelClass, entity, false)
            .and(Cond.logicalDeleteCond(Metadata.get(modelClass)));
    return execute(sql);
  }

  /**
   * 更新实体，忽略Null属性的字段
   *
   * @param modelClass 实体类型
   * @param entity 实体
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int updateIgnoreNull(Class<TModel> modelClass, TModel entity) {

    if (entity == null) {
      return 0;
    }
    Sql sql =
        this.database
            .dialect()
            .update(modelClass, entity, true)
            .and(Cond.logicalDeleteCond(Metadata.get(modelClass)));
    return execute(sql);
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity 实体
   * @param columns 需要更新的列
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, TModel entity, String[] columns) {

    if (entity == null) {
      return 0;
    }
    List<TModel> entities = new ArrayList<>();
    entities.add(entity);
    return update(modelClass, entities, columns);
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities 实体集合
   * @param columns 需要更新的列
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(
      Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    int result = 0;

    if (CollectionUtil.isEmpty(entities)) {
      return result;
    }

    if (isSqlServer()) {
      return updateByLines(modelClass, entities, columns);
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    Attribute pk = classMeta.checkPrimaryKey();
    List<Attribute> updAttrs = classMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new DBException("Could not match update attributes.");
    }

    final int batchSize = this.database.dbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);
    String tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    List<Sql> batchSqls = new ArrayList<>();

    for (Iterator<List<TModel>> ei = batchList.iterator(); ei.hasNext(); ) {
      List<TModel> entityList = ei.next();
      Sql sql = Sql.Update(tableName).append(" SET ");

      List<Object> ids = new ArrayList<>();
      Map<String, Sql> setClauses = new TreeMap<>();
      for (Iterator<TModel> mi = entityList.iterator(); mi.hasNext(); ) {
        TModel entity = mi.next();
        for (Attribute updAttr : updAttrs) {
          Sql setClause = null;
          final String columnName = updAttr.getColumnName();
          if (setClauses.containsKey(columnName)) {
            setClause = setClauses.get(columnName);
          } else {
            setClause = new Sql(StrUtil.format(" {} = CASE {} ", columnName, pk.getColumnName()));
          }
          setClause.append(
              new Sql(
                  " WHEN ? THEN ? ",
                  CollectionUtil.newArrayList(
                      ReflectUtil.getFieldValue(entity, pk.getFieldName()),
                      ReflectUtil.getFieldValue(entity, updAttr.getFieldName()))));

          setClauses.put(columnName, setClause);
        }
        ids.add(ReflectUtil.getFieldValue(entity, pk.getFieldName()));
      }

      for (Sql setClause : setClauses.values()) {
        sql.append(setClause).append(" END,");
      }
      sql.deleteLastChar(",");

      sql.where().and(Cond.in(pk.getColumnName(), ids));
      if (classMeta.isEnableLogicalDelete()) {
        sql.append(StrUtil.format(" AND {} = 0", classMeta.getLogicalDeleteField()));
      }
      batchSqls.add(sql);
    }
    for (Sql batchSql : batchSqls) {
      result += this.execute(batchSql);
    }
    return result;
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities 实体集合
   * @param columns 需要更新的列
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  private <TModel> int updateByLines(
      Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    int result = 0;

    ClassMeta classMeta = Metadata.get(modelClass);
    Attribute pk = classMeta.checkPrimaryKey();
    List<Attribute> updAttrs = classMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new DBException("Could not match update attributes.");
    }

    Sql sql;
    Collection<Collection> paramsList;
    Collection params;

    sql = Sql.Update(classMeta.getTableName()).append(" SET ");
    for (Attribute updateAttribute : updAttrs) {
      sql.append(StrUtil.format("{} = ?,", updateAttribute.getColumnName()));
    }

    sql.getSql().deleteCharAt(sql.getSql().lastIndexOf(","));

    sql.append(StrUtil.format(" WHERE {} = ?", pk.getColumnName()));

    if (classMeta.isEnableLogicalDelete()) {
      sql.append(StrUtil.format(" AND {} = 0", classMeta.getLogicalDeleteField()));
    }

    paramsList = new LinkedList<>();

    for (Iterator<TModel> iterator = entities.iterator(); iterator.hasNext(); ) {
      TModel entity = iterator.next();
      params = new LinkedList<>();

      for (Attribute attributeMeta : updAttrs) {
        params.add(ReflectUtil.getFieldValue(entity, attributeMeta.getFieldName()));
      }

      params.add(ReflectUtil.getFieldValue(entity, pk.getFieldName()));
      paramsList.add(params);
    }

    this.sqlLog.showBatchSql(sql.toString(), paramsList);
    result = database.updateBatch(sql.toString(), paramsList);

    this.sqlLog.showAffectedRows(result);
    return result;
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities 实体集合
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities) {

    return this.update(modelClass, entities, null);
  }

  /**
   * 如果记录存在更新，不存在则创建
   *
   * @param modelClass 实体类型
   * @param entity 实体集合
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int createOrUpdate(Class<TModel> modelClass, TModel entity) {
    if (exists(modelClass, entity)) {
      return update(modelClass, entity);
    } else {
      return create(modelClass, entity);
    }
  }

  /**
   * 删除记录
   *
   * @param modelClass 实体类型
   * @param entity 实体
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass, TModel entity) {

    if (Objects.isNull(entity)) {
      return 0;
    }

    ClassMeta classMeta = ClassMeta.createInstance(modelClass);
    Object pkVal = getPrimaryKeyValue(classMeta, entity);

    return deleteById(classMeta, pkVal);
  }

  /**
   * 批量删除记录
   *
   * @param modelClass 实体类型
   * @param entities 实体集合
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass, Collection<TModel> entities) {

    if (CollUtil.isEmpty(entities)) {
      return 0;
    }

    String pkFieldName = Metadata.get(modelClass).getPrimaryKeyFieldName();
    List ids = new ArrayList<>();

    Object pkVal;
    for (TModel entity : entities) {
      pkVal = ReflectUtil.getFieldValue(entity, pkFieldName);
      if (pkVal == null) {
        continue;
      }

      ids.add(pkVal);
    }

    return deleteByIds(modelClass, ids);
  }

  /**
   * 根据ID集合删除记录
   *
   * @param modelClass 实体类型
   * @param ids ID集合
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteByIds(Class<TModel> modelClass, Collection ids) {

    if (CollUtil.isEmpty(ids)) {
      return 0;
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    Attribute primaryKey = classMeta.checkPrimaryKey();
    String pkColumnName = primaryKey.getColumnName();
    Cond deleteCond = Cond.in(pkColumnName, new ArrayList(ids), false);

    return deleteByCond(classMeta, deleteCond);
  }

  /**
   * 根据ID删除记录
   *
   * @param modelClass 实体类型
   * @param id ID值
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteById(Class<TModel> modelClass, Object id) {

    if (id == null) {
      return 0;
    }
    ClassMeta classMeta = Metadata.get(modelClass);
    return deleteById(classMeta, id);
  }

  private int deleteById(ClassMeta classMeta, Object id) {
    String pkColumnName = classMeta.getPrimaryKeyColumnName();
    Cond deleteCond = Cond.eq(pkColumnName, id);

    return deleteByCond(classMeta, deleteCond);
  }

  /**
   * 根据条件删除记录
   *
   * @param modelClass 实体类型
   * @param cond 条件值
   * @param <TModel> 实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteByCond(Class<TModel> modelClass, Cond cond) {

    if (cond == null) {
      return 0;
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    return deleteByCond(classMeta, cond);
  }

  private int deleteByCond(ClassMeta classMeta, Cond deleteCond) {
    Sql sql;
    String tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    if (classMeta.isEnableLogicalDelete()) {
      sql =
          Sql.Update(tableName)
              .set(
                  StrUtil.format(
                      "{} = {}",
                      classMeta.getLogicalDeleteField(),
                      classMeta.getPrimaryKey().getColumnName()))
              .where(deleteCond)
              .and(Cond.logicalDeleteCond(classMeta));
    } else {
      sql = Sql.Delete(tableName).where(deleteCond);
    }

    return execute(sql);
  }

  private <TModel> Object getPrimaryKeyValue(ClassMeta classMeta, TModel entity) {
    String pkFieldName = classMeta.getPrimaryKeyFieldName();
    return ReflectUtil.getFieldValue(entity, pkFieldName);
  }
}
