package work.myfavs.framework.orm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.util.exception.DBException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * JDBC 连接工厂
 *
 * @author tanqimin
 */
public class JdbcConnFactory extends ConnFactory {
  private static final Logger log = LoggerFactory.getLogger(JdbcConnFactory.class);

  private final ThreadLocal<Connection> connectionHolder     = new ThreadLocal<>();
  private final ThreadLocal<Integer>    connectionDeepHolder = new ThreadLocal<>();

  public JdbcConnFactory(DataSource dataSource) {

    super(dataSource);
  }

  @Override
  public Connection openConnection() {

    Connection connection = getCurrentConnection();
    if (connection == null) {
      connectionDeepHolder.set(1);
      connection = createConnection();
      connectionHolder.set(connection);
    } else {
      connectionDeepHolder.set(connectionDeepHolder.get() + 1);
    }

    return connection;
  }

  @Override
  public Connection getCurrentConnection() {

    return connectionHolder.get();
  }

  @Override
  public void closeConnection(Connection connection) {
    final Integer connDeep = connectionDeepHolder.get();
    if (connDeep == 1) {
      Connection conn = connection;
      if (conn == null) {
        conn = getCurrentConnection();
      }
      releaseConnection(conn);
      connectionHolder.remove();
      connectionDeepHolder.remove();
    } else {
      connectionDeepHolder.set(connDeep - 1);
    }
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
      throw new DBException(e, "Could not get connection from datasource, error message: ");
    }
  }

  /**
   * 释放数据库链接
   *
   * @param conn 数据库链接
   */
  protected void releaseConnection(Connection conn) {
    if (Objects.isNull(conn)) return;

    try {
      if (conn.isClosed()) return;
      if (!conn.getAutoCommit()) {
        conn.commit();
      }
    } catch (SQLException e) {
      throw new DBException(e, "Fail to committed transaction");
    } finally {
      closeConn(conn);
    }
  }

  private static void closeConn(Connection conn) {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new DBException(e, "Fail to close connection");
    }
  }
}
