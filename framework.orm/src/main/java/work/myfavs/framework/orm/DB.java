package work.myfavs.framework.orm;

import cn.hutool.core.bean.BeanDesc.PropDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.DialectFactory;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.AttributeMeta;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.PKGenerator;
import work.myfavs.framework.orm.util.SqlLog;
import work.myfavs.framework.orm.util.convert.DBConvert;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 数据库操作对象
 */
@SuppressWarnings("unchecked")
public class DB
    implements AutoCloseable, Closeable {

  private final static Logger log = LoggerFactory.getLogger(DB.class);
  //数据库连接工厂构造器
  private static Constructor<? extends ConnFactory> constructor = null;
  //数据库方言
  private static IDialect dialect = null;
  //SQL日志
  private static SqlLog sqlLog = null;

  private DBTemplate dbTemplate;
  private DBConfig DBConfig;
  private ConnFactory connFactory;

  public DB(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
    this.DBConfig = dbTemplate.getDBConfig();
    this.connFactory = createConnectionFactoryInstance(this.dbTemplate.getDataSource());

    if (dialect == null) {
      dialect = DialectFactory.getInstance(this.DBConfig.getDbType());
    }
    if (sqlLog == null) {
      sqlLog = new SqlLog(this.DBConfig.getShowSql(), this.DBConfig.getShowResult());
    }
  }

  /**
   * 获取数据库方言
   *
   * @return 数据库方言
   */
  public IDialect getDialect() {

    return dialect;
  }

  /**
   * 获取ORM配置
   *
   * @return ORM配置
   */
  public DBConfig getDBConfig() {
    return DBConfig;
  }

  /**
   * 获取数据库连接工厂
   *
   * @param dataSource 数据源
   * @return 数据库连接工厂
   */
  private ConnFactory createConnectionFactoryInstance(DataSource dataSource) {

    try {
      if (constructor == null) {
        final Class<? extends ConnFactory> connectionFactoryClass = this.dbTemplate
            .getConnectionFactory();
        constructor = connectionFactoryClass.getConstructor(DataSource.class);
      }
      return constructor.newInstance(dataSource);
    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new DBException(e, "Fail to create ConnectionFactory instance, error message:");
    }
  }

  /**
   * 打开数据库连接
   *
   * @return 数据库连接
   */
  public Connection open() {

    return this.connFactory.openConnection();
  }

  /**
   * 关闭数据库连接
   */
  @Override
  public void close() {

    Connection connection = this.connFactory.getCurrentConnection();
    this.connFactory.closeConnection(connection);
  }

  /**
   * 提交事务
   */
  public void commit() {

    log.debug("Try to commit transaction.");
    try {
      this.connFactory.getCurrentConnection()
          .commit();
    } catch (SQLException e) {
      throw new DBException(e, "Fail to commit transaction, error message:");
    }

    log.debug("Transaction committed successfully.");
  }

  /**
   * 回滚事务
   */
  public void rollback() {

    log.debug("Try to rollback transaction.");
    try {
      this.connFactory.getCurrentConnection()
          .rollback();
    } catch (SQLException e) {
      throw new DBException(e, "Fail to rollback transaction, error message:");
    }

    log.debug("The transaction rollback was successful.");
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
  public <TView> List<TView> find(Class<TView> viewClass,
      String sql,
      Collection params) {

    Metadata.get(viewClass);

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<TView> result;

    try {
      sqlLog.showSql(sql, params);

      conn = this.open();
      pstmt = DBUtil.getPstForQuery(conn, sql, params);
      pstmt.setFetchSize(getDBConfig().getFetchSize());
      rs = pstmt.executeQuery();

      result = DBConvert.toList(viewClass, rs);

      sqlLog.showResult(result);
      return result;
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt, rs);
      this.close();
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
  public <TView> List<TView> find(Class<TView> viewClass,
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
  public <TView> List<TView> find(Class<TView> viewClass,
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
  public List<Record> find(String sql,
      Collection params) {

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
   * @param keyField  返回Map的Key的字段，必须是viewClass中存在的字段
   * @param sql       SQL语句
   * @param params    SQL参数
   * @param <TView>   结果集类型泛型
   * @return Map
   */
  public <TView> Map<Object, TView> findMap(Class<TView> viewClass,
      String keyField, String sql,
      Collection params) {
    final PropDesc prop = BeanUtil.getBeanDesc(viewClass).getProp(keyField);
    if (prop == null) {
      throw new DBException(
          StrUtil.format("Class {} not exist Prop named {}", viewClass.getName(), keyField));
    }

    return this.find(viewClass, sql, params).stream()
        .collect(Collectors.toMap(tView -> BeanUtil.getProperty(tView, keyField), tView -> tView));
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
  public <TView> Map<Object, TView> findMap(Class<TView> viewClass,
      String keyField, Sql sql) {
    return findMap(viewClass, keyField, sql.getSqlString(), sql.getParams());
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
  public <TView> List<TView> findTop(Class<TView> viewClass,
      int top,
      String sql,
      Collection params) {

    Sql querySql = this.getDialect().selectTop(1, top, sql, params);
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
  public <TView> List<TView> findTop(Class<TView> viewClass,
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
  public List<Record> findTop(int top,
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
  public List<Record> findTop(int top,
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
  public <TView> TView get(Class<TView> viewClass,
      String sql,
      Collection params) {

    Iterator<TView> iterator = this.findTop(viewClass, 1, sql, params)
        .iterator();
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
   * @return 记录
   */
  public <TView> TView get(Class<TView> viewClass,
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
  public Record get(String sql,
      Collection params) {

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
   * @param id        主键
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> TView getById(Class<TView> viewClass,
      Object id) {

    ClassMeta classMeta = Metadata.get(viewClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();

    Sql sql = this.getDialect().select(viewClass)
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
  public <TView> TView getByField(Class<TView> viewClass,
      String field,
      Object param) {

    Sql sql = this.getDialect().select(viewClass)
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
  public <TView> TView getByCond(Class<TView> viewClass,
      Cond cond) {

    Sql sql = this.getDialect().select(viewClass)
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
  public <TView> TView getByCondition(Class<TView> viewClass,
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
  public <TView> TView getByCondition(Class<TView> viewClass,
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
  public <TView> List<TView> findByIds(Class<TView> viewClass,
      Collection ids) {

    ClassMeta classMeta = Metadata.get(viewClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();
    Sql sql = this.getDialect().select(viewClass)
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
  public <TView> List<TView> findByField(Class<TView> viewClass,
      String field,
      Object param) {

    Sql sql = this.getDialect().select(viewClass)
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
  public <TView> List<TView> findByField(Class<TView> viewClass,
      String field,
      Collection params) {

    Sql sql = this.getDialect().select(viewClass)
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
  public <TView> List<TView> findByCond(Class<TView> viewClass,
      Cond cond) {

    Sql sql = this.getDialect().select(viewClass)
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
  public <TView> List<TView> findByCondition(Class<TView> viewClass,
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
  public <TView> List<TView> findByCondition(Class<TView> viewClass,
      Object object,
      String conditionGroup) {

    return findByCond(viewClass, Cond.create(object, conditionGroup));
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 行数
   */
  public long count(String sql,
      Collection params) {

    return this.get(Number.class, dialect.count(sql, params))
        .longValue();
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   * @return 行数
   */
  public long count(Sql sql) {

    return this.count(sql.getSqlString(), sql.getParams());
  }

  /**
   * 根据条件获取查询的行数
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 行数
   */
  public <TView> long countByCond(Class<TView> viewClass,
      Cond cond) {

    Sql sql = dialect.count(viewClass)
        .where(cond)
        .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(Number.class, sql)
        .longValue();
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql SQL
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public boolean exists(Sql sql) {

    return exists(sql.getSqlString(), sql.getParams());
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public boolean exists(String sql,
      Collection params) {

    return this.count(sql, params) > 0L;
  }

  /**
   * 根据条件判断是否存在符合条件的数据
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public <TView> boolean existsByCond(Class<TView> viewClass,
      Cond cond) {

    return this.countByCond(viewClass, cond) > 0L;
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
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass,
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

      long maxPageSize = this.DBConfig.getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize);
      }
    } else {
      pagSize = -1;
    }

    querySql = dialect.selectTop(currentPage, pagSize, sql, params);
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
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass,
      Sql sql,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    return this
        .findPageLite(viewClass, sql.getSqlString(), sql.getParams(), enablePage, currentPage,
            pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  分页对象
   * @param <TView>   结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass,
      String sql,
      Collection params,
      IPageable pageable) {

    return this
        .findPageLite(viewClass, sql, params, pageable.getEnablePage(), pageable.getCurrentPage(),
            pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL
   * @param pageable  分页对象
   * @param <TView>   结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass,
      Sql sql,
      IPageable pageable) {

    return this
        .findPageLite(viewClass, sql.getSqlString(), sql.getParams(), pageable.getEnablePage(),
            pageable.getCurrentPage(), pageable.getPageSize());
  }


  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(String sql,
      Collection params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    return this.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(Sql sql,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    return this.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(String sql,
      Collection params,
      IPageable pageable) {

    return this.findPageLite(Record.class, sql, params, pageable);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql      SQL
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(Sql sql,
      IPageable pageable) {

    return this.findPageLite(Record.class, sql, pageable);
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
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass,
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

      long maxPageSize = this.DBConfig.getMaxPageSize();
      if (maxPageSize > 0L && pagSize > maxPageSize) {
        throw new DBException("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize);
      }
    } else {
      pagSize = -1;
    }

    querySql = dialect.selectTop(currentPage, pagSize, sql, params);
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
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass,
      Sql sql,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    return findPage(viewClass, sql.getSqlString(), sql.getParams(), enablePage, currentPage,
        pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  可分页对象
   * @param <TView>   结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass,
      String sql,
      Collection params,
      IPageable pageable) {

    return findPage(viewClass, sql, params, pageable.getEnablePage(), pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL
   * @param pageable  可分页对象
   * @param <TView>   结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass,
      Sql sql,
      IPageable pageable) {

    return findPage(viewClass, sql.getSqlString(), sql.getParams(), pageable.getEnablePage(),
        pageable.getCurrentPage(), pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public Page<Record> findPage(String sql,
      Collection params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable 可分页对象
   * @return 分页结果集
   */
  public Page<Record> findPage(String sql,
      Collection params,
      IPageable pageable) {

    return this
        .findPage(Record.class, sql, params, pageable.getEnablePage(), pageable.getCurrentPage(),
            pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql      SQL
   * @param pageable 可分页对象
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql,
      IPageable pageable) {

    return this.findPage(Record.class, sql, pageable.getEnablePage(), pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 影响行数
   */
  public int execute(String sql,
      Collection params) {

    int result = 0;
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      getSqlLog().showSql(sql, params);

      conn = this.open();

      pstmt = DBUtil.getPstForUpdate(conn, false, sql, params);
      result = DBUtil.executeUpdate(pstmt);

      getSqlLog().showAffectedRows(result);

    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      DBUtil.close(pstmt);
      this.close();
    }

    return result;
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   * @return 影响行数
   */
  public int execute(Sql sql) {

    return this.execute(sql.getSqlString(), sql.getParams());
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
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass,
      TModel entity) {

    int result = 0;
    if (entity == null) {
      return result;
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();
    GenerationType strategy;
    String pkFieldName;
    boolean autoGeneratedPK = false;

    Sql sql = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

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

    sql = dialect.insert(modelClass, entity);

    try {
      getSqlLog().showSql(sql.getSqlString(), sql.getParams());

      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, autoGeneratedPK, sql.getSqlString(), sql.getParams());
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
      this.close();
    }

    return result;
  }

  public long snowFlakeId() {
    return PKGenerator.nextSnowFakeId(this.DBConfig.getWorkerId(),
        this.DBConfig.getDataCenterId());
  }


  /**
   * 批量创建实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass,
      Collection<TModel> entities) {

    int result = 0;
    GenerationType strategy;
    String pkFieldName;
    Object pkVal;
    boolean autoGeneratedPK = false;
    List<AttributeMeta> updateAttributes;
    Sql sql;
    Collection<Collection> paramsList;
    Collection params;

    if (entities == null || entities.isEmpty()) {
      return result;
    }
    ClassMeta classMeta = Metadata.get(modelClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();

    pkFieldName = primaryKey.getFieldName();
    strategy = classMeta.getStrategy();
    updateAttributes = classMeta.getUpdateAttributes();
    sql = dialect.insert(modelClass);
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

      for (AttributeMeta attributeMeta : updateAttributes) {
        params.add(ReflectUtil.getFieldValue(entity, attributeMeta.getFieldName()));
      }
      paramsList.add(params);
    }

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      getSqlLog().showBatchSql(sql.getSqlString(), paramsList);

      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, autoGeneratedPK, sql.getSqlString());
      result = DBUtil.executeBatch(pstmt, paramsList, this.DBConfig.getBatchSize());

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
      this.close();
    }

    return result;
  }

  public String uuid() {
    return PKGenerator.nextUUID();
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass,
      TModel entity) {

    if (entity == null) {
      return 0;
    }
    Sql sql = dialect.update(modelClass, entity, false)
        .and(Cond.logicalDeleteCond(Metadata.get(modelClass)));
    return execute(sql);
  }

  /**
   * 更新实体，忽略Null属性的字段
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int updateIgnoreNull(Class<TModel> modelClass,
      TModel entity) {

    if (entity == null) {
      return 0;
    }
    Sql sql = dialect.update(modelClass, entity, true)
        .and(Cond.logicalDeleteCond(Metadata.get(modelClass)));
    return execute(sql);
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param columns    需要更新的列
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass,
      TModel entity,
      String[] columns) {

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
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass,
      Collection<TModel> entities,
      String[] columns) {

    int result = 0;
    List<AttributeMeta> updateAttributes;

    Sql sql;
    Collection<Collection> paramsList;
    Collection params;

    Connection conn = null;
    PreparedStatement pstmt = null;

    if (entities == null) {
      return result;
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();

    if (columns == null || columns.length == 0) {
      updateAttributes = classMeta.getUpdateAttributes();
    } else {
      updateAttributes = new LinkedList<>();
      for (String column : columns) {
        AttributeMeta attributeMeta = classMeta.getQueryAttributes()
            .get(column.toUpperCase());
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
      throw new DBException("Could not match update attributes.");
    }

    sql = Sql.Update(classMeta.getTableName())
        .append(" SET ");
    for (AttributeMeta updateAttribute : updateAttributes) {
      sql.append(StrUtil.format("{} = ?,", updateAttribute.getColumnName()));
    }

    sql.getSql()
        .deleteCharAt(sql.getSql()
            .lastIndexOf(","));

    sql.append(StrUtil.format(" WHERE {} = ?", primaryKey.getColumnName()));

    if (classMeta.isEnableLogicalDelete()) {
      sql.append(StrUtil.format(" AND {} = 0", classMeta.getLogicalDeleteField()));
    }

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

      getSqlLog().showBatchSql(sql.getSqlString(), paramsList);

      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, false, sql.getSqlString());
      result = DBUtil.executeBatch(pstmt, paramsList, this.DBConfig.getBatchSize());

      getSqlLog().showAffectedRows(result);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt);
      this.close();
    }

    return result;
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass,
      List<TModel> entities) {

    return this.update(modelClass, entities, null);
  }

  /**
   * 删除记录
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass,
      TModel entity) {

    String pkFieldName;
    Object pkVal;

    if (entity == null) {
      return 0;
    }
    AttributeMeta primaryKey = Metadata.get(modelClass)
        .checkPrimaryKey();
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
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass,
      List<TModel> entities) {

    String pkFieldName;
    Object pkVal;
    List ids;

    if (entities == null || entities.size() == 0) {
      return 0;
    }
    AttributeMeta primaryKey = Metadata.get(modelClass)
        .checkPrimaryKey();
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
   * @return 影响行数
   */
  public <TModel> int deleteByIds(Class<TModel> modelClass,
      Collection ids) {

    if (ids == null || ids.size() == 0) {
      return 0;
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();
    String pkColumnName = primaryKey.getColumnName();
    Sql sql = Sql.Delete(classMeta.getTableName())
        .where(Cond.in(pkColumnName, new ArrayList(ids), false));
    return execute(sql);
  }

  /**
   * 根据ID删除记录
   *
   * @param modelClass 实体类型
   * @param id         ID值
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteById(Class<TModel> modelClass,
      Object id) {

    if (id == null) {
      return 0;
    }
    ClassMeta classMeta = Metadata.get(modelClass);
    AttributeMeta primaryKey = classMeta.checkPrimaryKey();
    String pkColumnName = primaryKey.getColumnName();

    Sql sql;

    if (classMeta.isEnableLogicalDelete()) {
      sql = Sql.Update(classMeta.getTableName())
          .set(StrUtil.format("{} = {}", classMeta.getLogicalDeleteField(), pkColumnName))
          .where(Cond.eq(pkColumnName, id))
          .and(Cond.logicalDeleteCond(classMeta));
    } else {
      sql = Sql.Delete(classMeta.getTableName())
          .where(Cond.eq(pkColumnName, id));
    }

    return execute(sql);
  }

  /**
   * 根据条件删除记录
   *
   * @param modelClass 实体类型
   * @param cond       条件值
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteByCond(Class<TModel> modelClass,
      Cond cond) {

    if (cond == null) {
      return 0;
    }

    ClassMeta classMeta = Metadata.get(modelClass);
    Sql sql;

    if (classMeta.isEnableLogicalDelete()) {
      sql = Sql.Update(classMeta.getTableName())
          .set(
              StrUtil.format("{} = {}", classMeta.getLogicalDeleteField(), classMeta.getPrimaryKey()
                  .getColumnName()))
          .where(cond)
          .and(Cond.eq(classMeta.getLogicalDeleteField(), 0));
    } else {
      sql = Sql.Delete(classMeta.getTableName())
          .where(cond);
    }

    return execute(sql);
  }

  private static SqlLog getSqlLog() {

    return sqlLog;
  }

}
