package work.myfavs.framework.orm;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库链接工厂
 *
 * @author tanqimin
 */
public abstract class ConnFactory {

  protected final DataSource dataSource;

  public ConnFactory(DataSource dataSource) {

    this.dataSource = dataSource;
  }

  /**
   * 打开数据库链接
   *
   * @return 数据库链接
   */
  public abstract Connection openConnection();

  /**
   * 获取当前数据库链接
   *
   * @return 数据库链接
   */
  public abstract Connection getCurrentConnection();

  /**
   * 关闭数据库链接
   *
   * @param connection 数据库链接
   */
  public abstract void closeConnection(Connection connection);
}
