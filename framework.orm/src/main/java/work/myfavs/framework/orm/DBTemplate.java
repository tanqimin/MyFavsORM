package work.myfavs.framework.orm;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import work.myfavs.framework.orm.meta.dialect.DialectFactory;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;
import work.myfavs.framework.orm.repository.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.DBUtil;


@Slf4j
public class DBTemplate
    implements Cloneable, AutoCloseable {

  private static Snowflake  snowflake    = null;
  //数据源
  private        DataSource dataSource;
  //数据库方言
  private        IDialect   dialect;
  //数据库类型
  private        String     dbType       = "sqlserver";
  //一次批量插入数据的数量
  private        int        batchSize    = 1000;
  //查询每次抓取数据的数量
  private        int        fetchSize    = 500;
  //查询超时时间，单位：秒
  private        int        queryTimeout = 60;
  //终端ID(雪花算法生成主键用)
  private        long       workerId     = 1L;
  //数据中心ID(雪花算法生成主键用)
  private        long       dataCenterId = 1L;

  @Override
  public void close()
      throws Exception {

  }

  /**
   * 构造方法
   */
  private DBTemplate() {

  }

  /**
   * 构造方法
   *
   * @param dataSource DataSource
   */
  private DBTemplate(DataSource dataSource) {

    this.dataSource = dataSource;
  }

  public static DBTemplate build() {

    return new DBTemplate();
  }

  public static DBTemplate build(DataSource dataSource) {

    return new DBTemplate(dataSource);
  }

  /**
   * 获取雪花主键值
   *
   * @return 雪花主键值
   */
  public long nextSnowFakeId() {

    if (snowflake == null) {
      snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
    }
    return snowflake.nextId();
  }

  /**
   * 获取UUID主键值
   *
   * @return UUID主键值
   */
  public String nextUUID() {

    return IdUtil.randomUUID();
  }

  /**
   * 注册PropertyHandler
   *
   * @param clazz           属性类型
   * @param propertyHandler 属性类型转换器
   *
   * @return DBTemplate
   */
  public DBTemplate registerPropertyHandler(Class<?> clazz, PropertyHandler propertyHandler) {

    PropertyHandlerFactory.register(clazz, propertyHandler);
    return this;
  }

  /**
   * 注册默认的PropertyHandler
   *
   * @return DBTemplate
   */
  public DBTemplate registerDefaultPropertyHandler() {

    PropertyHandlerFactory.registerDefault();
    return this;
  }

  /**
   * 获取数据库链接
   *
   * @return Connection
   */
  public Connection createConnection() {

    Connection connection = DBUtil.createConnection(this.getDataSource());
    try {
      log.info("AUTO COMMIT:{}", connection.getAutoCommit());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

  /**
   * 关闭Statment并且释放数据库连接
   *
   * @param connection Connection
   * @param statement  Statement
   * @param resultSet  ResultSet
   */
  public void release(Connection connection, Statement statement, ResultSet resultSet) {

    DBUtil.close(resultSet);
    DBUtil.close(statement);
    this.release(connection);
  }

  /**
   * 关闭Statment并且释放数据库连接
   *
   * @param connection Connection
   * @param statement  Statement
   */
  public void release(Connection connection, Statement statement) {

    DBUtil.close(statement);
    this.release(connection);
  }

  /**
   * 释放数据库连接
   *
   * @param connection Connection
   */
  public void release(Connection connection) {

    DataSourceUtils.releaseConnection(connection, this.getDataSource());
  }

  public IDialect getDialect() {

    if (this.dialect == null) {
      this.dialect = DialectFactory.getInstance(this.dbType);
    }
    return this.dialect;
  }

  public DataSource getDataSource() {

    return dataSource;
  }

  public DBTemplate setDataSource(DataSource dataSource) {

    this.dataSource = dataSource;
    return this;
  }

  public String getDbType() {

    return dbType;
  }

  public DBTemplate setDbType(String dbType) {

    this.dbType = dbType;
    return this;
  }

  public int getBatchSize() {

    return batchSize;
  }

  public DBTemplate setBatchSize(int batchSize) {

    this.batchSize = batchSize;
    return this;
  }

  public int getFetchSize() {

    return fetchSize;
  }

  public DBTemplate setFetchSize(int fetchSize) {

    this.fetchSize = fetchSize;
    return this;
  }

  public int getQueryTimeout() {

    return queryTimeout;
  }

  public DBTemplate setQueryTimeout(int queryTimeout) {

    this.queryTimeout = queryTimeout;
    return this;
  }

  public long getWorkerId() {

    return workerId;
  }

  public DBTemplate setWorkerId(long workerId) {

    this.workerId = workerId;
    return this;
  }

  public long getDataCenterId() {

    return dataCenterId;
  }

  public DBTemplate setDataCenterId(long dataCenterId) {

    this.dataCenterId = dataCenterId;
    return this;
  }

}
