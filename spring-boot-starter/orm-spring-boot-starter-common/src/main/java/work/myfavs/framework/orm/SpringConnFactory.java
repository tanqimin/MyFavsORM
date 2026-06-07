package work.myfavs.framework.orm;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Spring 连接工厂，将数据库连接的生命周期委托给 Spring 管理.
 * <p>通过 {@link DataSourceUtils#getConnection} 和 {@link DataSourceUtils#releaseConnection}
 * 与 Spring 事务管理器集成，使 ORM 在 {@code @Transactional} 方法中复用同一个连接.</p>
 *
 * @see JdbcConnFactory
 * @see DataSourceUtils
 * @author tanqimin
 */
public class SpringConnFactory extends JdbcConnFactory {

  /**
   * 构造方法.
   *
   * @param dataSource {@link DataSource} 实例
   */
  public SpringConnFactory(DataSource dataSource) {
    super(dataSource);
  }

  /**
   * 由 Spring 接管创建数据库连接.
   *
   * @return 数据库连接，由 {@link DataSourceUtils#getConnection} 管理
   */
  @Override
  protected Connection createConnection() {
    return DataSourceUtils.getConnection(super.dataSource);
  }

  /**
   * 由 Spring 接管释放数据库连接.
   *
   * @param conn 要释放的数据库连接
   */
  @Override
  protected void releaseConnection(Connection conn) {
    DataSourceUtils.releaseConnection(conn, super.dataSource);
  }
}
