package work.myfavs.framework.orm;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * Spring 连接工厂
 *
 * @author tanqimin
 */
public class SpringConnFactory extends JdbcConnFactory {

  public SpringConnFactory(DataSource dataSource) {

    super(dataSource);
  }

  /**
   * 由Spring 接管创建数据库链接
   *
   * @return 数据库链接
   */
  @Override
  protected Connection createConnection() {

    return DataSourceUtils.getConnection(super.dataSource);
  }

  /**
   * 由Spring 接管释放数据库链接
   *
   * @param conn 数据库链接
   */
  @Override
  protected void releaseConnection(Connection conn) {

    DataSourceUtils.releaseConnection(conn, super.dataSource);
  }
}
