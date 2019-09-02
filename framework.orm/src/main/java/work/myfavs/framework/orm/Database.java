package work.myfavs.framework.orm;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.DialectFactory;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.AttributeMeta;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.*;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 数据库操作对象
 */
@Slf4j
public class Database
    implements AutoCloseable, Closeable {


  private Orm      orm;
  //数据库方言
  private IDialect dialect;
  //SQL日志
  private SqlLog   sqlLog;

  public Database(Orm orm) {

    this.orm = orm;
    this.dialect = DialectFactory.getInstance(getOrm().getDbType());
    this.sqlLog = new SqlLog(getOrm().getShowSql(), getOrm().getShowResult());
  }

  /**
   * 修改当前事务深度的事务隔离级别
   *
   * @param transactionIsolation 事务隔离级别
   */
  public void changeIsolation(int transactionIsolation) {

    getOrm().changeIsolation(transactionIsolation);
  }

  /**
   * 重置当前事务深度的默认事务隔离级别
   */
  public void resetIsolation() {

    getOrm().resetIsolation();
  }

  /**
   * 提交当前事务深度的事务
   */
  public void commit() {

    getOrm().commit();
  }

  /**
   * 回滚当前事务深度的事务
   */
  public void rollback() {

    getOrm().rollback();
  }

  @Override
  public void close() {

    getOrm().release();
  }

  /**
   * 获取当前数据库链接
   *
   * @return Connection 数据库链接
   */
  public Connection getConnection() {

    return getOrm().getCurrentConnection();
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
  public <TView> List<TView> find(Class<TView> viewClass, String sql, List<Object> params) {

    Metadata.get(viewClass);

    Connection        conn   = null;
    PreparedStatement pstmt  = null;
    ResultSet         rs     = null;
    List<TView>       result = new ArrayList<>();

    try {
      getSqlLog().showSql(sql, params);

      conn = getConnection();
      pstmt = params == null || params.size() == 0
          ? DBUtil.getPs(conn, sql)
          : DBUtil.getPs(conn, sql, params);
      pstmt.setFetchSize(getOrm().getFetchSize());
      rs = pstmt.executeQuery();

      result = DBConvert.toList(viewClass, rs);

      getSqlLog().showResult(rs);
      return result;
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt, rs);
    }
  }

  /**
   * 执行SQL，并返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql) {

    return this.find(viewClass, sql, null);
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
  public <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    return this.find(viewClass, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 结果集
   */
  public List<Record> find(String sql, List<Object> params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL
   *
   * @return 结果集
   */
  public List<Record> find(Sql sql) {

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
   *
   * @return 结果集
   */
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, String sql, List<Object> params) {

    Sql querySql = this.dialect.selectTop(1, top, sql, params);
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
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {

    return this.findTop(viewClass, top, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top    行数
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 结果集
   */
  public List<Record> findTop(int top, String sql, List<Object> params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL
   *
   * @return 结果集
   */
  public List<Record> findTop(int top, Sql sql) {

    return this.findTop(Record.class, top, sql);
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
  public <TView> TView get(Class<TView> viewClass, String sql, List<Object> params) {

    Iterator<TView> iterator = this.findTop(viewClass, 1, sql, params).iterator();
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
  public <TView> TView get(Class<TView> viewClass, Sql sql) {

    return this.get(viewClass, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 记录
   */
  public Record get(String sql, List<Object> params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL
   *
   * @return 记录
   */
  public Record get(Sql sql) {

    return this.get(Record.class, sql);
  }

  /**
   * 根据主键获取记录
   *
   * @param viewClass 结果类型
   * @param id        主键
   *
   * @return 记录
   */
  public <TView> TView getById(Class<TView> viewClass, Object id) {

    AttributeMeta primaryKey = Metadata.get(viewClass).checkPrimaryKey();
    Sql           sql        = this.dialect.select(viewClass).where(Cond.eq(primaryKey.getColumnName(), id));
    return this.get(viewClass, sql);
  }

  /**
   * 根据指定字段获取记录
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   *
   * @return 记录
   */
  public <TView> TView getByField(Class<TView> viewClass, String field, Object param) {

    Sql sql = this.dialect.select(viewClass).where(Cond.eq(field, param));
    return this.get(viewClass, sql);
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param viewClass 结果类型
   * @param ids       主键ID集合
   *
   * @return 实体集合
   */
  public <TView> List<TView> findByIds(Class<TView> viewClass, List ids) {

    AttributeMeta primaryKey = Metadata.get(viewClass).checkPrimaryKey();
    Sql           sql        = this.dialect.select(viewClass).where(Cond.in(primaryKey.getColumnName(), ids, false));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   *
   * @return 实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Object param) {

    Sql sql = this.dialect.select(viewClass).where(Cond.eq(field, param));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param params    参数集合
   *
   * @return 实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, List<Object> params) {

    Sql sql = this.dialect.select(viewClass).where(Cond.in(field, params, false));
    return this.find(viewClass, sql);
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
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   *
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, String sql, List<Object> params, boolean enablePage, int currentPage,
                                              int pageSize) {

    int         pagSize;
    Sql         querySql;
    List<TView> data;

    pagSize = pageSize;
    if (enablePage) {
      long maxPageSize = getOrm().getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException(StringUtil.format("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize));
      }
    } else {
      pagSize = -1;
    }

    querySql = this.dialect.selectTop(currentPage, pagSize, sql, params);
    data = this.find(viewClass, querySql);

    return PageLite.createInstance(data, currentPage, pagSize);

  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   *
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(viewClass, sql.getSql().toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(String sql, List<Object> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   *
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, String sql, List<Object> params, boolean enablePage, int currentPage,
                                      int pageSize) {

    int         pagSize;
    long        totalPages;
    long        totalRecords;
    Sql         querySql;
    List<TView> data;

    pagSize = pageSize;

    if (enablePage) {
      long maxPageSize = getOrm().getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException(StringUtil.format("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize));
      }
    } else {
      pagSize = -1;
    }

    querySql = this.dialect.selectTop(currentPage, pagSize, sql, params);
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

    return Page.createInstance(data, currentPage, pagSize, totalPages, totalRecords);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   *
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return findPage(viewClass, sql.getSql().toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 分页结果集
   */
  public Page<Record> findPage(String sql, List<Object> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   *
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 影响行数
   */
  public int execute(String sql, List<Object> params) {

    int               result = 0;
    Connection        conn   = null;
    PreparedStatement pstmt  = null;

    try {
      getSqlLog().showSql(sql, params);

      conn = getConnection();

      pstmt = params != null && params.size() > 0
          ? DBUtil.getPs(conn, false, sql, params)
          : DBUtil.getPs(conn, false, sql);
      result = DBUtil.executeUpdate(pstmt);

      getSqlLog().showAffectedRows(result);

    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      DBUtil.close(pstmt);
    }

    return result;
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   *
   * @return 影响行数
   */
  public int execute(Sql sql) {

    return this.execute(sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   *
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList) {

    int   sqlCnt  = sqlList.size();
    int[] results = new int[sqlCnt];
    for (int i = 0;
         i < sqlCnt;
         i++) {
      results[i] = execute(sqlList.get(i));
    }
    return results;
  }

  /**
   * 创建实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass, TModel entity) {

    int result = 0;
    if (entity == null) {
      return result;
    }

    ClassMeta      classMeta       = Metadata.get(modelClass);
    AttributeMeta  primaryKey      = classMeta.checkPrimaryKey();
    GenerationType strategy;
    String         pkFieldName;
    boolean        autoGeneratedPK = false;

    Sql               sql   = null;
    Connection        conn  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;

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
          throw new DBException("Assigned ID can not be null");
        } else if (strategy == GenerationType.UUID) {
          pkVal = PKGenerator.nextUUID();
        } else if (strategy == GenerationType.SNOW_FLAKE) {
          pkVal = PKGenerator.nextSnowFakeId(1L, 1L);
        }

        ReflectUtil.setFieldValue(entity, pkFieldName, pkVal);
      }
    }

    sql = this.dialect.insert(modelClass, entity);

    try {
      getSqlLog().showSql(sql.getSql().toString(), sql.getParams());

      conn = getConnection();
      pstmt = DBUtil.getPs(conn, autoGeneratedPK, sql);
      result = DBUtil.executeUpdate(pstmt);

      getSqlLog().showAffectedRows(result);

      if (autoGeneratedPK) {
        rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
          ReflectUtil.setFieldValue(entity, pkFieldName, rs.getObject(1));
        }
      }
    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      DBUtil.close(pstmt, rs);
    }

    return result;
  }


  /**
   * 批量创建实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass, Collection<TModel> entities) {

    int                 result          = 0;
    GenerationType      strategy;
    String              pkFieldName;
    Object              pkVal;
    boolean             autoGeneratedPK = false;
    List<AttributeMeta> updateAttributes;
    Sql                 sql;
    List<List>          paramsList;
    List<Object>        params;

    if (entities == null || entities.isEmpty()) {
      return result;
    }
    ClassMeta     classMeta  = Metadata.get(modelClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();

    pkFieldName = primaryKey.getFieldName();
    strategy = classMeta.getStrategy();
    updateAttributes = classMeta.getUpdateAttributes();
    sql = this.dialect.insert(modelClass);
    paramsList = new LinkedList<>();

    if (strategy == GenerationType.IDENTITY) {
      autoGeneratedPK = true;
    }

    for (Iterator<TModel> iterator = entities.iterator();
         iterator.hasNext(); ) {
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
            throw new DBException("Assigned ID can not be null");
          } else if (strategy == GenerationType.UUID) {
            pkVal = PKGenerator.nextUUID();
          } else if (strategy == GenerationType.SNOW_FLAKE) {
            pkVal = PKGenerator.nextSnowFakeId(1L, 1L);
          }

          ReflectUtil.setFieldValue(entity, pkFieldName, pkVal);
        }

        params.add(pkVal);
      }

      for (AttributeMeta attributeMeta : updateAttributes) {
        params.add(ReflectUtil.getFieldValue(entity, attributeMeta.getFieldName()));
      }
      paramsList.add(params);
    }

    Connection        conn  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;

    try {
      getSqlLog().showBatchSql(sql.getSql().toString(), paramsList);

      conn = getConnection();
      pstmt = DBUtil.getPsForUpdate(conn, autoGeneratedPK, sql.getSql().toString(), paramsList, getOrm().getBatchSize());

      result = DBUtil.executeBatch(pstmt);

      getSqlLog().showAffectedRows(result);

      if (autoGeneratedPK) {
        rs = pstmt.getGeneratedKeys();
        for (Iterator<TModel> iterator = entities.iterator();
             iterator.hasNext(); ) {
          TModel tModel = iterator.next();
          if (rs.next()) {
            ReflectUtil.setFieldValue(tModel, pkFieldName, rs.getObject(1));
          }
        }
      }
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt, rs);
    }

    return result;
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, TModel entity) {

    if (entity == null) {
      return 0;
    }
    return execute(this.dialect.update(modelClass, entity));
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param columns    需要更新的列
   * @param <TModel>   实体类型泛型
   *
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
   * @param entities   实体集合
   * @param columns    需要更新的列
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    int                 result = 0;
    List<AttributeMeta> updateAttributes;

    Sql          sql;
    List<List>   paramsList;
    List<Object> params;

    Connection        conn  = null;
    PreparedStatement pstmt = null;

    if (entities == null) {
      return result;
    }

    ClassMeta     classMeta  = Metadata.get(modelClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();

    if (columns == null || columns.length == 0) {
      updateAttributes = classMeta.getUpdateAttributes();
    } else {
      updateAttributes = new LinkedList<>();
      for (String column : columns) {
        AttributeMeta attributeMeta = classMeta.getQueryAttributes().get(column.toUpperCase());
        if (attributeMeta == null) {
          continue;
        }
        if (attributeMeta.isPrimaryKey()) {
          continue;
        }
        updateAttributes.add(attributeMeta);
      }
    }

    if (updateAttributes.isEmpty()) {
      throw new DBException("没有匹配到需要更新的Column");
    }

    sql = Sql.Update(classMeta.getTableName()).append(" SET ");
    for (AttributeMeta updateAttribute : updateAttributes) {
      sql.append(StringUtil.format("{} = ?,", updateAttribute.getColumnName()));
    }
    sql.getSql().deleteCharAt(sql.getSql().lastIndexOf(","));
    sql.append(StringUtil.format(" WHERE {} = ?", primaryKey.getColumnName()));

    paramsList = new LinkedList<>();

    for (Iterator<TModel> iterator = entities.iterator();
         iterator.hasNext(); ) {
      TModel entity = iterator.next();
      params = new LinkedList<>();

      for (AttributeMeta attributeMeta : updateAttributes) {
        params.add(ReflectUtil.getFieldValue(entity, attributeMeta.getFieldName()));
      }

      params.add(ReflectUtil.getFieldValue(entity, primaryKey.getFieldName()));
      paramsList.add(params);
    }

    try {

      getSqlLog().showBatchSql(sql.getSql().toString(), paramsList);

      conn = getConnection();
      pstmt = DBUtil.getPsForUpdate(conn, false, sql.getSql().toString(), paramsList, getOrm().getBatchSize());

      result = DBUtil.executeBatch(pstmt);

      getSqlLog().showAffectedRows(result);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt);
    }

    return result;
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, List<TModel> entities) {

    return this.update(modelClass, entities, null);
  }

  /**
   * 删除记录
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass, TModel entity) {

    String pkFieldName;
    Object pkVal;

    if (entity == null) {
      return 0;
    }
    AttributeMeta primaryKey = Metadata.get(modelClass).checkPrimaryKey();
    pkFieldName = primaryKey.getFieldName();
    pkVal = ReflectUtil.getFieldValue(entity, pkFieldName);

    return deleteById(modelClass, pkVal);
  }

  /**
   * 批量删除记录
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass, List<TModel> entities) {

    String       pkFieldName;
    Object       pkVal;
    List<Object> ids;

    if (entities == null || entities.size() == 0) {
      return 0;
    }
    AttributeMeta primaryKey = Metadata.get(modelClass).checkPrimaryKey();
    pkFieldName = primaryKey.getFieldName();
    ids = new ArrayList<>();
    for (TModel entity : entities) {
      pkVal = ReflectUtil.getFieldValue(entity, pkFieldName);
      if (pkVal == null) {
        continue;
      }

      ids.add(pkVal);
    }

    if (ids.isEmpty()) {
      return 0;
    }

    return deleteByIds(modelClass, ids);
  }

  /**
   * 根据ID集合删除记录
   *
   * @param modelClass 实体类型
   * @param ids        ID集合
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int deleteByIds(Class<TModel> modelClass, Collection ids) {

    if (ids == null || ids.size() == 0) {
      return 0;
    }

    ClassMeta     classMeta    = Metadata.get(modelClass);
    AttributeMeta primaryKey   = classMeta.checkPrimaryKey();
    String        pkColumnName = primaryKey.getColumnName();
    Sql           sql          = Sql.Delete(classMeta.getTableName()).where(Cond.in(pkColumnName, new ArrayList<Object>(ids)));
    return execute(sql);
  }

  /**
   * 根据ID删除记录
   *
   * @param modelClass 实体类型
   * @param id         ID值
   * @param <TModel>   实体类型泛型
   *
   * @return 影响行数
   */
  public <TModel> int deleteById(Class<TModel> modelClass, Object id) {

    if (id == null) {
      return 0;
    }
    ClassMeta     classMeta    = Metadata.get(modelClass);
    AttributeMeta primaryKey   = classMeta.checkPrimaryKey();
    String        pkColumnName = primaryKey.getColumnName();
    Sql           sql          = Sql.Delete(classMeta.getTableName()).where(Cond.eq(pkColumnName, id));
    return execute(sql);
  }

  private SqlLog getSqlLog() {

    return this.sqlLog;
  }

  private Orm getOrm() {

    return this.orm;
  }

}
