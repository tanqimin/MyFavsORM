package work.myfavs.framework.orm;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.dialect.DialectFactory;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.exception.DBException;


@Slf4j
public class Orm
    implements Cloneable, AutoCloseable {

  private final ThreadLocal<Database>            connectionHolder      = new ThreadLocal<>();
  private final ThreadLocal<java.sql.Connection> jdbcConnectionHolder  = new ThreadLocal<>();
  private final ThreadLocal<Integer>             transactionDeepHolder = new ThreadLocal<>();

  //数据源
  protected DataSource dataSource;
  //数据库方言
  protected IDialect   dialect;
  //数据库类型
  protected String     dbType       = DbType.MYSQL;
  //一次批量插入数据的数量
  protected int        batchSize    = 200;
  //查询每次抓取数据的数量
  protected int        fetchSize    = 1000;
  //查询超时时间，单位：秒
  protected int        queryTimeout = 60;
  //是否显示SQL
  protected boolean    showSql      = false;
  //是否显示查询结果
  protected boolean    showResult   = false;
  //每页最大记录数
  protected int        maxPageSize  = -1;

  @Override
  public void close() {

  }

  protected Orm() {

  }

  /**
   * 构造方法
   *
   * @param dataSource DataSource
   */
  protected Orm(DataSource dataSource) {

    this.dataSource = dataSource;
  }

  /**
   * 创建一个 DBTemplate 实例
   *
   * @param dataSource 数据源
   *
   * @return DBTemplate 实例
   */
  public static Orm build(DataSource dataSource) {

    return new Orm(dataSource);
  }

  /**
   * 注册 PropertyHandler
   *
   * @param clazz           属性类型
   * @param propertyHandler 属性类型转换器
   *
   * @return DBTemplate
   */
  public Orm registerPropertyHandler(Class<?> clazz, PropertyHandler propertyHandler) {

    PropertyHandlerFactory.register(clazz, propertyHandler);
    return this;
  }

  /**
   * 注册默认的 PropertyHandler
   *
   * @return DBTemplate
   */
  public Orm registerDefaultPropertyHandler() {

    PropertyHandlerFactory.registerDefault();
    return this;
  }

  public Database open() {

    Database database = connectionHolder.get();
    if (database == null) {
      database = new Database(this);
      connectionHolder.set(database);
      transactionDeepHolder.set(1);
    } else {
      transactionDeepHolder.set(transactionDeepHolder.get() + 1);
    }

    return database;
  }

  /**
   * 获取数据库链接
   *
   * @return Connection
   */
  public java.sql.Connection createConnection() {

    java.sql.Connection connection = jdbcConnectionHolder.get();
    if (connection == null) {
      try {
        connection = DBUtil.createConnection(dataSource);
      } catch (SQLException e) {
        throw new DBException("Could not get datasource, error message: ", e);
      }
      jdbcConnectionHolder.set(connection);
    }

    return connection;
  }

  /**
   * 关闭 ResultSet、Statement 并且释放数据库连接
   *
   * @param connection Connection
   * @param statement  Statement
   * @param resultSet  ResultSet
   */
  public void release(java.sql.Connection connection, Statement statement, ResultSet resultSet) {

    DBUtil.close(resultSet);
    DBUtil.close(statement);
    this.release();
  }

  /**
   * 关闭 Statement 并且释放数据库连接
   *
   * @param connection Connection
   * @param statement  Statement
   */
  public void release(java.sql.Connection connection, Statement statement) {

    DBUtil.close(statement);
    this.release();
  }

  /**
   * 释放数据库连接
   */
  public void release() {

    if (transactionDeepHolder.get() == 1) {
      java.sql.Connection connection = jdbcConnectionHolder.get();
      DBUtil.close(connection);
      jdbcConnectionHolder.remove();
      connectionHolder.remove();
      transactionDeepHolder.remove();
    } else {
      transactionDeepHolder.set(transactionDeepHolder.get() - 1);
    }
  }

  /**
   * 获取数据库方言
   *
   * @return 数据库方言
   */
  public IDialect getDialect() {

    if (this.dialect == null) {
      this.dialect = DialectFactory.getInstance(this.dbType);
    }
    return this.dialect;
  }

  /**
   * 获取数据源
   *
   * @return 数据源
   */
  public DataSource getDataSource() {

    return dataSource;
  }

  /**
   * 设置数据源
   *
   * @param dataSource 数据源
   *
   * @return DBTemplate
   */
  public Orm setDataSource(DataSource dataSource) {

    this.dataSource = dataSource;
    return this;
  }

  /**
   * 获取数据库类型
   *
   * @return 数据库类型
   */
  public String getDbType() {

    return dbType;
  }

  /**
   * 设置数据库类型
   *
   * @param dbType 数据库类型
   *
   * @return DBTemplate
   */
  public Orm setDbType(String dbType) {

    this.dbType = dbType;
    return this;
  }

  /**
   * 获取批处理大小
   *
   * @return 批处理大小
   */
  public int getBatchSize() {

    return batchSize;
  }

  /**
   * 设置批处理大小
   *
   * @param batchSize 批处理大小
   *
   * @return DBTemplate
   */
  public Orm setBatchSize(int batchSize) {

    this.batchSize = batchSize;
    return this;
  }

  /**
   * 获取抓取数据大小
   *
   * @return 抓取数据大小
   */
  public int getFetchSize() {

    return fetchSize;
  }

  /**
   * 设置抓取数据大小
   *
   * @param fetchSize 抓取数据大小
   *
   * @return DBTemplate
   */
  public Orm setFetchSize(int fetchSize) {

    this.fetchSize = fetchSize;
    return this;
  }

  /**
   * 获取查询超时时间
   *
   * @return 查询超时时间
   */
  public int getQueryTimeout() {

    return queryTimeout;
  }

  /**
   * 设置查询超时时间
   *
   * @param queryTimeout 查询超时时间
   *
   * @return DBTemplate
   */
  public Orm setQueryTimeout(int queryTimeout) {

    this.queryTimeout = queryTimeout;
    return this;
  }

  /**
   * 获取是否显示SQL
   *
   * @return 是否显示SQL
   */
  public boolean getShowSql() {

    return showSql;
  }

  /**
   * 设置是否显示SQL（日志级别INFO）
   *
   * @param showSql 是否显示SQL
   *
   * @return DBTemplate
   */
  public Orm setShowSql(boolean showSql) {

    this.showSql = showSql;
    return this;
  }

  /**
   * 获取是否显示查询结果
   *
   * @return 是否显示查询结果
   */
  public boolean getShowResult() {

    return showResult;
  }

  /**
   * 设置是否显示查询结果（日志级别INFO）
   *
   * @param showResult 是否显示查询结果
   *
   * @return DBTemplate
   */
  public Orm setShowResult(boolean showResult) {

    this.showResult = showResult;
    return this;
  }

  /**
   * 获取分页时每页最大记录数
   *
   * @return 分页时每页最大记录数
   */
  public long getMaxPageSize() {

    return maxPageSize;
  }

  /**
   * 设置分页时每页最大记录数(小于 0 为不限制)
   *
   * @param maxPageSize 分页时每页最大记录数
   */
  public Orm setMaxPageSize(int maxPageSize) {

    this.maxPageSize = maxPageSize;
    return this;
  }

  public void commit() {

    final java.sql.Connection connection = createConnection();
    try {
      if (transactionDeepHolder.get() == 1) {
        if (!connection.isReadOnly() && !connection.getAutoCommit()) {
          connection.commit();
        }
      }
    } catch (SQLException e) {
      throw new DBException(e);
    }

  }

  public void rollback() {

    final java.sql.Connection connection = createConnection();
    try {
      if (transactionDeepHolder.get() == 1) {
        if (!connection.isReadOnly() && !connection.getAutoCommit()) {
          connection.rollback();
        }
      }
    } catch (SQLException e) {
      throw new DBException(e);
    }

  }

}
