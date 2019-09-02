package work.myfavs.framework.orm;


import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.dialect.DialectFactory;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.transaction.TransactionDeep;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.exception.DBException;


@Slf4j
public class Orm
    implements Cloneable, AutoCloseable {

  private final ThreadLocal<Database>        databaseHolder        = new ThreadLocal<>();
  private final ThreadLocal<Connection>      connectionHolder      = new ThreadLocal<>();     //保证当前线程获取同一个链接
  private final ThreadLocal<TransactionDeep> transactionDeepHolder = new ThreadLocal<>();     //记录当前事务深度

  //数据源
  protected DataSource dataSource;
  //数据库方言
  protected IDialect   dialect;
  //数据库类型
  protected String     dbType           = DbType.MYSQL;
  //一次批量插入数据的数量
  protected int        batchSize        = 200;
  //查询每次抓取数据的数量
  protected int        fetchSize        = 1000;
  //查询超时时间，单位：秒
  protected int        queryTimeout     = 60;
  //是否显示SQL
  protected boolean    showSql          = false;
  //是否显示查询结果
  protected boolean    showResult       = false;
  //每页最大记录数
  protected int        maxPageSize      = -1;
  //默认事务级别
  protected int        defaultIsolation = Connection.TRANSACTION_READ_COMMITTED;

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

  /**
   * 创建 Database 对象
   *
   * @return Database
   */
  public Database open() {

    Database        database               = databaseHolder.get();
    TransactionDeep transactionDeep;
    int             currentTransactionDeep = 1;

    if (database == null) {
      database = new Database(this);
      databaseHolder.set(database);
      transactionDeep = new TransactionDeep();
    } else {
      transactionDeep = transactionDeepHolder.get();
      currentTransactionDeep = transactionDeep.getCurrentTransactionDeep() + 1;
    }
    transactionDeep.log(currentTransactionDeep, this.defaultIsolation);
    transactionDeepHolder.set(transactionDeep);

    return database;
  }

  /**
   * 创建 Database 对象，并开启事务
   *
   * @return Database
   */
  public Database beginTransaction() {

    Database database = open();
    try {
      database.getConnection().setAutoCommit(false);
    } catch (SQLException e) {
      throw new DBException("Could not start the transaction, error message: ", e);
    }
    return database;
  }

  /**
   * 创建 Database 对象，并开启事务
   *
   * @param transactionIsolation 事务隔离级别
   *
   * @return Database
   */
  public Database beginTransaction(int transactionIsolation) {

    Database database = open();
    try {
      final TransactionDeep transactionDeep        = transactionDeepHolder.get();
      final int             currentTransactionDeep = transactionDeep.getCurrentTransactionDeep();
      transactionDeep.log(currentTransactionDeep, transactionIsolation);
      database.getConnection().setTransactionIsolation(transactionIsolation);
      database.getConnection().setAutoCommit(false);
    } catch (SQLException e) {
      throw new DBException("Could not start the transaction, error message: ", e);
    }
    return database;
  }

  /**
   * 获取数据库链接
   *
   * @return Connection
   */
  public Connection getCurrentConnection() {

    Connection connection = connectionHolder.get();
    if (connection == null) {
      try {
        connection = DBUtil.createConnection(dataSource);
      } catch (SQLException e) {
        throw new DBException("Could not get datasource, error message: ", e);
      }
      connectionHolder.set(connection);
    }

    return connection;
  }

  /**
   * 释放数据库连接
   */
  public void release() {

    Connection            connection      = connectionHolder.get();
    final TransactionDeep transactionDeep = transactionDeepHolder.get();

    if (transactionDeep.getCurrentTransactionDeep() == 1) {
      DBUtil.close(connection);
      connectionHolder.remove();
      databaseHolder.remove();
      transactionDeepHolder.remove();
    } else {
      final int currentTransactionDeep = transactionDeep.getCurrentTransactionDeep() - 1;
      transactionDeep.setCurrentTransactionDeep(currentTransactionDeep);
      this.setTransactionIsolation(connection, transactionDeep.getIsolation(currentTransactionDeep));
    }
  }

  private void setTransactionIsolation(Connection conn, int isolation) {

    try {
      conn.setTransactionIsolation(isolation);
    } catch (SQLException e) {
      throw new DBException("Could not set the transaction isolation, error message: ", e);
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

  /**
   * 获取默认事务隔离级别
   *
   * @return int
   */
  public int getDefaultIsolation() {

    return this.defaultIsolation;
  }

  /**
   * 设置默认事务隔离级别
   *
   * @param defaultIsolation 事务隔离级别
   *
   * @return Orm
   */
  public Orm setDefaultIsolation(int defaultIsolation) {

    this.defaultIsolation = defaultIsolation;
    return this;
  }

  /**
   * 改变当前事务隔离级别
   *
   * @param transactionIsolation 事务隔离级别
   */
  public void changeIsolation(int transactionIsolation) {

    setTransactionIsolation(getCurrentConnection(), transactionIsolation);
  }

  /**
   * 重置当前事务深度的默认事务隔离级别
   */
  public void resetIsolation() {

    final TransactionDeep transactionDeep = transactionDeepHolder.get();
    if (transactionDeep != null) {
      final int currentTransactionDeep = transactionDeep.getCurrentTransactionDeep();
      setTransactionIsolation(getCurrentConnection(), transactionDeep.getIsolation(currentTransactionDeep));
    }
  }

  public void commit() {

    final Connection connection = getCurrentConnection();
    try {
      final TransactionDeep transactionDeep = transactionDeepHolder.get();
      if (transactionDeep.getCurrentTransactionDeep() == 1) {
        if (!connection.isReadOnly() && !connection.getAutoCommit()) {
          connection.commit();
        }
      }
    } catch (SQLException e) {
      throw new DBException(e);
    }

  }

  public void rollback() {

    final Connection connection = getCurrentConnection();
    try {
      final TransactionDeep transactionDeep = transactionDeepHolder.get();
      if (transactionDeep.getCurrentTransactionDeep() == 1) {
        if (!connection.isReadOnly() && !connection.getAutoCommit()) {
          connection.rollback();
        }
      }
    } catch (SQLException e) {
      throw new DBException(e);
    }

  }

}
