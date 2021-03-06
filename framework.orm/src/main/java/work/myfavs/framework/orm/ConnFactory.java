package work.myfavs.framework.orm;

import java.sql.Connection;
import javax.sql.DataSource;

/**
 * 数据库链接工厂
 *
 * @author tanqimin
 */
abstract public class ConnFactory {

  protected DataSource dataSource;

  public ConnFactory(DataSource dataSource) {

    this.dataSource = dataSource;
  }

  /**
   * 打开数据库链接
   *
   * @return 数据库链接
   */
  abstract public Connection openConnection();

  /**
   * 获取当前数据库链接
   *
   * @return 数据库链接
   */
  abstract public Connection getCurrentConnection();

  /**
   * 关闭数据库链接
   *
   * @param connection 数据库链接
   */
  abstract public void closeConnection(Connection connection);

}
