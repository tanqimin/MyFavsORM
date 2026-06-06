package work.myfavs.framework.orm;

import work.myfavs.framework.orm.util.exception.DBException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC 连接工厂
 *
 * @author tanqimin
 */
public class JdbcConnFactory extends ConnFactory {

  private final ThreadLocal<Connection> connectionHolder     = new ThreadLocal<>();
  private final ThreadLocal<Integer>    connectionDeepHolder = new ThreadLocal<>();

  public JdbcConnFactory(DataSource dataSource) {

    super(dataSource);
  }

  @Override
  public Connection openConnection() {

    Connection connection = getCurrentConnection();
    if (null == connection) {
      connectionDeepHolder.set(1);
      connection = createConnection();
      connectionHolder.set(connection);
      return connection;
    }

    connectionDeepHolder.set(connectionDeepHolder.get() + 1);
    return connection;
  }

  @Override
  public Connection getCurrentConnection() {

    return connectionHolder.get();
  }

  @Override
  public void closeConnection(Connection connection) {
    final Integer connDeep = connectionDeepHolder.get();
    if (connDeep > 1) {
      connectionDeepHolder.set(connDeep - 1);
      return;
    }

    Connection conn = connection;
    if (null == conn)
      conn = getCurrentConnection();

    releaseConnection(conn);
    connectionHolder.remove();
    connectionDeepHolder.remove();
  }

  /**
   * 创建JDBC 数据库链接
   *
   * @return 数据库链接
   */
  protected Connection createConnection() {

    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new DBException(e, "从数据源中获取数据库连接时发生异常: %s", e.getMessage());
    }
  }

  /**
   * 释放数据库链接
   *
   * @param conn 数据库链接
   */
  protected void releaseConnection(Connection conn) {

    if (null == conn) return;

    try {
      if (withoutCommit(conn)) return;

      conn.commit();
    } catch (SQLException e) {
      throw new DBException(e, "提交事务时发生异常: %s", e.getMessage());
    } finally {
      closeConn(conn);
    }
  }

  private static boolean withoutCommit(Connection conn) throws SQLException {
    return conn.getAutoCommit() || conn.isClosed();
  }

  private static void closeConn(Connection conn) {
    try {
      if (!withoutCommit(conn)) {
        conn.rollback();
      }
      conn.close();
    } catch (SQLException e) {
      throw new DBException(e, "关闭数据库连接时发生异常: %s", e.getMessage());
    }
  }
}
