package work.myfavs.framework.orm;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import javax.sql.DataSource;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.dialect.DialectFactory;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.exception.DBException;


public class DBTemplate
    implements Cloneable, AutoCloseable {

  //region Attributes
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
  //终端ID
  protected long       workerId         = 1L;
  //数据中心ID
  protected long       dataCenterId     = 1L;

  //数据库连接工厂
  protected Class<? extends ConnectionFactory> connectionFactoryClass = null;
  //endregion

  //region Constructor
  /**
   * 构造方法
   *
   * @param dataSource DataSource
   */
  protected DBTemplate(DataSource dataSource) {

    this.setDataSource(dataSource);
    this.setConnectionFactoryClass(JdbcConnectionFactory.class);
  }
  //endregion

  /**
   * 创建一个 DBTemplate 实例
   *
   * @param dataSource 数据源
   *
   * @return DBTemplate 实例
   */
  public static DBTemplate build(DataSource dataSource) {

    return new DBTemplate(dataSource);
  }

  /**
   * 注册 PropertyHandler
   *
   * @param clazz           属性类型
   * @param propertyHandler 属性类型转换器
   *
   * @return DBTemplate
   */
  public DBTemplate registerPropertyHandler(Class<?> clazz,
                                            PropertyHandler propertyHandler) {

    PropertyHandlerFactory.register(clazz, propertyHandler);
    return this;
  }

  /**
   * 注册默认的 PropertyHandler
   *
   * @return DBTemplate
   */
  public DBTemplate registerDefaultPropertyHandler() {

    PropertyHandlerFactory.registerDefault();
    return this;
  }

  /**
   * 创建 Database 对象
   *
   * @return Database
   */
  public Database open() {

    return this.open(null);
  }

  private Database open(Consumer<Connection> consumer) {

    Database   database   = new Database(this);
    Connection connection = database.open();
    if (consumer != null) {
      consumer.accept(connection);
    }
    return database;
  }

  @Override
  public void close() {

  }


  /**
   * 创建 Database 对象，并开启事务
   *
   * @return Database
   */
  public Database beginTransaction() {

    return this.beginTransaction(this.defaultIsolation);
  }

  /**
   * 创建 Database 对象，并开启事务
   *
   * @param transactionIsolation 事务隔离级别
   *
   * @return Database
   */
  public Database beginTransaction(int transactionIsolation) {

    return this.open(connection -> {
      try {
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(transactionIsolation);
      } catch (SQLException e) {
        throw new DBException(e, "Could not start the transaction, error message: ");
      }
    });
  }

  private void setTransactionIsolation(Connection conn,
                                       int isolation) {

    try {
      conn.setTransactionIsolation(isolation);
    } catch (SQLException e) {
      throw new DBException(e, "Could not set the transaction isolation, error message: ");
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
  public DBTemplate setDataSource(DataSource dataSource) {

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
  public DBTemplate setDbType(String dbType) {

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
  public DBTemplate setBatchSize(int batchSize) {

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
  public DBTemplate setFetchSize(int fetchSize) {

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
  public DBTemplate setQueryTimeout(int queryTimeout) {

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
  public DBTemplate setShowSql(boolean showSql) {

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
  public DBTemplate setShowResult(boolean showResult) {

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
   *
   * @return DBTemplate
   */
  public DBTemplate setMaxPageSize(int maxPageSize) {

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
   * @return DBTemplate
   */
  public DBTemplate setDefaultIsolation(int defaultIsolation) {

    this.defaultIsolation = defaultIsolation;
    return this;
  }

  /**
   * 获取终端ID
   *
   * @return 终端ID
   */
  public long getWorkerId() {

    return workerId;
  }

  /**
   * 设置终端ID
   *
   * @param workerId 终端ID
   *
   * @return DBTemplate
   */
  public DBTemplate setWorkerId(long workerId) {

    this.workerId = workerId;
    return this;
  }

  /**
   * 获取数据中心ID
   *
   * @return 数据中心ID
   */
  public long getDataCenterId() {

    return dataCenterId;
  }

  /**
   * 设置数据中心ID
   *
   * @param dataCenterId 数据中心ID
   *
   * @return DBTemplate
   */
  public DBTemplate setDataCenterId(long dataCenterId) {

    this.dataCenterId = dataCenterId;
    return this;
  }

  public Class<? extends ConnectionFactory> getConnectionFactoryClass() {

    return connectionFactoryClass;
  }

  public DBTemplate setConnectionFactoryClass(Class<? extends ConnectionFactory> connectionFactoryClass) {

    this.connectionFactoryClass = connectionFactoryClass;
    return this;
  }

}
