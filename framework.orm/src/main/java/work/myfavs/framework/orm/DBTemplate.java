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
  private        String     dbType       = "mysql";
  //一次批量插入数据的数量
  private        int        batchSize    = 200;
  //查询每次抓取数据的数量
  private        int        fetchSize    = 100;
  //查询超时时间，单位：秒
  private        int        queryTimeout = 60;
  //终端ID(雪花算法生成主键用)
  private        long       workerId     = 1L;
  //数据中心ID(雪花算法生成主键用)
  private        long       dataCenterId = 1L;
  //是否显示SQL
  private        boolean    showSql      = false;
  //是否显示查询结果
  private        boolean    showResult   = false;
  //每页最大记录数
  private        long       maxPageSize  = -1;

  @Override
  public void close() {

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

  /**
   * 创建一个 DBTemplate 实例
   *
   * @return DBTemplate 实例
   */
  public static DBTemplate build() {

    return new DBTemplate();
  }

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
   * 获取 UUID 主键值
   *
   * @return UUID 主键值
   */
  public String nextUUID() {

    return IdUtil.randomUUID();
  }

  /**
   * 注册 PropertyHandler
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
   * 注册默认的 PropertyHandler
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
   * 关闭 ResultSet、Statement 并且释放数据库连接
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
   * 关闭 Statement 并且释放数据库连接
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
   * 如果使用雪花算法生成主键时，需设置终端ID
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
   * 如果使用雪花算法生成主键时，需设置工作站ID
   *
   * @param dataCenterId 工作站ID
   *
   * @return DBTemplate
   */
  public DBTemplate setDataCenterId(long dataCenterId) {

    this.dataCenterId = dataCenterId;
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
   */
  public DBTemplate setMaxPageSize(long maxPageSize) {

    this.maxPageSize = maxPageSize;
    return this;
  }

}
