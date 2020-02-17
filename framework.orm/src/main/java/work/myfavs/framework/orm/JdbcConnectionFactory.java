package work.myfavs.framework.orm;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.exception.DBException;

@Slf4j
public class JdbcConnectionFactory
    extends ConnectionFactory {

  private final ThreadLocal<Connection> connectionHolder     = new ThreadLocal<>();
  private final ThreadLocal<Integer>    connectionDeepHolder = new ThreadLocal<>();

  public JdbcConnectionFactory(DataSource dataSource) {

    super(dataSource);
  }

  @Override
  public Connection openConnection() {

    Connection connection = getCurrentConnection();
    if (connection == null) {
      log.debug("Could not found connection from thread local cache.");
      log.debug("Create connection from datasource.");
      connection = createConnection();
      connectionHolder.set(connection);
      connectionDeepHolder.set(1);
    } else {
      Integer connDeep = connectionDeepHolder.get();
      connectionDeepHolder.set(connDeep + 1);
    }

    log.debug("Current connection deep : {}", connectionDeepHolder.get());
    return connection;
  }

  @Override
  public Connection getCurrentConnection() {

    log.debug("Get connection from thread local cache.");
    return connectionHolder.get();
  }

  @Override
  public void closeConnection(Connection connection) {

    final Integer connDeep = connectionDeepHolder.get();
    log.debug("Current connection deep : {}", connDeep);
    if (connDeep == 1) {
      log.debug("Release connection");
      Connection conn = connection == null
          ? getCurrentConnection()
          : connection;
      releaseConnection(conn);
      connectionHolder.remove();
      connectionDeepHolder.remove();
    } else {
      log.debug("Reduce connection.");
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
      return DBUtil.createConnection(dataSource);
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

    try {
      if (conn.getAutoCommit() == false) {
        conn.commit();
      }
    } catch (SQLException e) {
      throw new DBException(e, "Fail to committed transaction, error message : ");
    }
    DBUtil.close(conn);
  }

}
