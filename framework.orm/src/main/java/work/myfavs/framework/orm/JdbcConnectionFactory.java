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

    Connection connection = connectionHolder.get();
    if (connection == null) {
      try {
        connection = createConnection();
      } catch (SQLException e) {
        throw new DBException("Could not get datasource, error message: ", e);
      }
      connectionHolder.set(connection);
      connectionDeepHolder.set(1);
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

    if (connectionDeepHolder.get() == 1) {
      Connection conn = connection == null
          ? connectionHolder.get()
          : connection;
      releaseConnection(conn);
      connectionHolder.remove();
      connectionDeepHolder.remove();
    } else {
      connectionDeepHolder.set(connectionDeepHolder.get() - 1);
    }

  }

  protected Connection createConnection()
      throws SQLException {

    return DBUtil.createConnection(dataSource);
  }

  protected void releaseConnection(Connection conn) {

    DBUtil.close(conn);
  }

}
