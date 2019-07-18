package work.myfavs.framework.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;
import work.myfavs.framework.orm.repository.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.DBUtil;

@Slf4j
@Data
public class DBTemplate
    implements Cloneable, AutoCloseable {

  //数据源
  private DataSource dataSource;
  //数据库类型
  private String     dbType       = "sqlserver";
  //一次批量插入数据的数量
  private int        batchSize    = 1000;
  //查询每次抓取数据的数量
  private int        fetchSize    = 500;
  //查询超时时间，单位：秒
  private int        queryTimeout = 60;

  @Override
  public void close()
      throws Exception {

  }

  /**
   * 构造方法
   */
  public DBTemplate() {

  }

  /**
   * 构造方法
   *
   * @param dataSource DataSource
   */
  public DBTemplate(DataSource dataSource) {

    this.dataSource = dataSource;
  }

  /**
   * 注册属性类型转换器
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
   * 获取数据库链接
   *
   * @return Connection
   */
  public Connection createConnection() {

    return DBUtil.createConnection(this.getDataSource());
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

}
