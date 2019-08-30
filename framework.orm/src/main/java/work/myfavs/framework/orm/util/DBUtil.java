package work.myfavs.framework.orm.util;

import java.sql.*;
import java.util.Iterator;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 数据库工具类
 */
public class DBUtil {

  /**
   * 创建数据库链接
   *
   * @param dataSource DataSource
   *
   * @return Connection
   *
   * @throws SQLException SQLException
   */
  public static Connection createConnection(DataSource dataSource)
      throws SQLException {

    Connection connection = DataSourceUtils.getConnection(dataSource);
    if (connection.getAutoCommit()) {
      connection.setAutoCommit(false);
    }
    return connection;
  }

  /**
   * 获取 PreparedStatement
   *
   * @param conn Connection
   * @param sql  Sql语句
   *
   * @return PreparedStatement
   *
   * @throws SQLException SQLException
   */
  public static PreparedStatement getPs(Connection conn, String sql)
      throws SQLException {

    return getPs(conn, false, sql);
  }

  /**
   * 获取 PreparedStatement
   *
   * @param conn   Connection
   * @param sql    Sql语句
   * @param params 参数数组
   *
   * @return PreparedStatement
   *
   * @throws SQLException SQLException
   */
  public static PreparedStatement getPs(Connection conn, String sql, List<Object> params)
      throws SQLException {

    return getPs(conn, false, sql, params);
  }

  /**
   * 获取 PreparedStatement
   *
   * @param conn            Connection
   * @param autoGeneratedPK 是否自增主键
   * @param sql             Sql语句
   *
   * @return PreparedStatement
   *
   * @throws SQLException SQLException
   */
  public static PreparedStatement getPs(Connection conn, boolean autoGeneratedPK, Sql sql)
      throws SQLException {

    return getPs(conn, autoGeneratedPK, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 获取 PreparedStatement
   *
   * @param conn            Connection
   * @param autoGeneratedPK 是否自增主键
   * @param sql             Sql语句
   * @param params          参数数组
   *
   * @return PreparedStatement
   *
   * @throws SQLException SQLException
   */
  public static PreparedStatement getPs(Connection conn, boolean autoGeneratedPK, String sql, List<Object> params)
      throws SQLException {

    PreparedStatement pst = getPs(conn, autoGeneratedPK, sql);
    setParams(pst, params);
    return pst;
  }

  /**
   * 从 Connection 获取 PreparedStatement
   *
   * @param connection      数据库链接
   * @param autoGeneratedPK 是否自动生成主键
   * @param sql             Sql语句
   *
   * @return PreparedStatement
   *
   * @throws SQLException SQLException
   */
  public static PreparedStatement getPs(Connection connection, boolean autoGeneratedPK, String sql)
      throws SQLException {

    PreparedStatement pst;
    if (autoGeneratedPK) {
      pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    } else {
      pst = connection.prepareStatement(sql);
    }
    return pst;
  }

  /**
   * 获取sql执行对象
   *
   * @param conn            Connection
   * @param autoGeneratedPK 是否自增主键
   * @param sql             Sql语句
   * @param params          参数数组集合
   * @param batchSize       批处理大小
   *
   * @return PreparedStatement
   *
   * @throws SQLException SQLException
   */
  public static PreparedStatement getPsForUpdate(Connection conn, boolean autoGeneratedPK, String sql, List<List> params, int batchSize)
      throws SQLException {

    PreparedStatement pst = getPs(conn, autoGeneratedPK, sql);
    int               i   = 0;
    for (Iterator<List> iterator = params.iterator();
         iterator.hasNext(); ) {
      setParams(pst, iterator.next());
      pst.addBatch();
      if (++i % batchSize == 0) {
        pst.executeBatch();
        pst.clearBatch();
      }
    }
    return pst;
  }

  /**
   * 设置 PreparedStatement 参数
   *
   * @param preparedStatement PreparedStatement
   * @param params            参数数组
   *
   * @return PreparedStatement
   *
   * @throws SQLException SQLException
   */
  private static PreparedStatement setParams(PreparedStatement preparedStatement, List params)
      throws SQLException {

    if (params != null && params.size() > 0) {
      for (int i = 0;
           i < params.size();
           i++) {
        PropertyHandlerFactory.addParameter(preparedStatement, i + 1, params.get(i));
      }
    }
    return preparedStatement;
  }

  public static int executeUpdate(PreparedStatement preparedStatement)
      throws SQLException {

    int        result;
    Connection connection;

    connection = preparedStatement.getConnection();
    result = preparedStatement.executeUpdate();

    return result;
  }

  public static int executeBatch(PreparedStatement preparedStatement)
      throws SQLException {

    int        result = 0;
    Connection connection;

    connection = preparedStatement.getConnection();
    int[] res = preparedStatement.executeBatch();
    preparedStatement.clearBatch();

    for (int i : res) {
      result += i;
    }

    return result;
  }

  /**
   * 关闭数据库连接
   *
   * @param connection Connection
   */
  public static void close(Connection connection) {

    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        throw new DBException(e);
      }
    }
  }

  /**
   * 关闭Statment
   *
   * @param statement Statement
   */
  public static void close(Statement statement) {

    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        throw new DBException(e);
      }
    }
  }

  /**
   * 关闭ResultSet
   *
   * @param resultSet ResultSet
   */
  public static void close(ResultSet resultSet) {

    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (SQLException e) {
        throw new DBException(e);
      }
    }
  }

  /**
   * 关闭Connection、Statement、ResultSet
   *
   * @param connection Connection
   * @param statement  Statement
   * @param resultSet  ResultSet
   */
  public static void close(Connection connection, Statement statement, ResultSet resultSet) {

    close(resultSet);
    close(connection, statement);
  }

  /**
   * 关闭Connection、Statement
   *
   * @param connection Connection
   * @param statement  Statement
   */
  public static void close(Connection connection, Statement statement) {

    close(statement);
    close(connection);
  }

  /**
   * Statement、ResultSet
   *
   * @param statement Statement
   * @param resultSet ResultSet
   */
  public static void close(Statement statement, ResultSet resultSet) {

    close(statement);
    close(resultSet);
  }

}
