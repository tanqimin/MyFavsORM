package work.myfavs.framework.orm;

import java.sql.*;
import java.util.Collection;
import java.util.List;

import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.convert.DBConvert;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;
import work.myfavs.framework.orm.util.func.ThrowingRunnable;
import work.myfavs.framework.orm.util.func.ThrowingSupplier;

public class Database implements IDatabase {

  private final DBConfig dbConfig;
  private final ConnFactory connFactory;

  public Database(DBTemplate dbTemplate) {
    this.dbConfig = dbTemplate.getDbConfig();
    this.connFactory = dbTemplate.getConnectionFactory();
  }

  private Connection openConnection() {
    return this.connFactory.openConnection();
  }

  private Connection getCurrentConnection() {
    return this.connFactory.getCurrentConnection();
  }

  private void closeCollection() {
    this.connFactory.closeConnection(getCurrentConnection());
  }

  private void close(ResultSet resultSet, Statement statement){
    DBUtil.close(resultSet, statement, this::closeCollection);
  }

  private void close(Statement statement){
    DBUtil.close(null, statement, this::closeCollection);
  }

  /** 提交事务 */
  public void commit() {
    DBUtil.commit(getCurrentConnection());
  }

  public Savepoint setSavepoint() {
    return DBUtil.setSavepoint(getCurrentConnection());
  }

  public Savepoint setSavepoint(String name) {
    return DBUtil.setSavepoint(getCurrentConnection(), name);
  }

  /** 回滚事务 */
  public void rollback() {
    DBUtil.rollback(getCurrentConnection());
  }

  public void rollback(Savepoint savepoint) {
    DBUtil.rollback(getCurrentConnection(), savepoint);
  }

  @Override
  public <TResult> TResult tx(ThrowingFunction<Connection, TResult, SQLException> func) {
    try {
      Connection conn = openConnection();
      return func.apply(conn);
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      this.closeCollection();
    }
  }

  @Override
  public <TResult> TResult tx(ThrowingSupplier<TResult, SQLException> supplier) {
    try {
      this.openConnection();
      return supplier.get();
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      this.closeCollection();
    }
  }

  @Override
  public void tx(ThrowingConsumer<Connection, SQLException> consumer) {
    try {
      consumer.accept(this.openConnection());
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      this.closeCollection();
    }
  }

  @Override
  public void tx(ThrowingRunnable<SQLException> runnable) {
    try {
      this.openConnection();
      runnable.run();
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      this.closeCollection();
    }
  }

  @Override
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {

    Metadata.get(viewClass);

    Connection conn;
    PreparedStatement statement = null;
    ResultSet rs = null;
    List<TView> result;

    try {
      conn = this.openConnection();
      statement = DBUtil.getPstForQuery(conn, sql, params);
      statement.setQueryTimeout(this.dbConfig.getQueryTimeout());
      statement.setFetchSize(this.dbConfig.getFetchSize());
      rs = statement.executeQuery();

      result = DBConvert.toList(viewClass, rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.close(rs, statement);
    }

    return result;
  }

  @Override
  public List<Record> findRecords(String sql, Collection<?> params) {
    Connection conn;
    PreparedStatement statement = null;
    ResultSet rs = null;
    List<Record> result;

    try {
      conn = this.openConnection();
      statement = DBUtil.getPstForQuery(conn, sql, params);
      statement.setQueryTimeout(this.dbConfig.getQueryTimeout());
      statement.setFetchSize(this.dbConfig.getFetchSize());
      rs = statement.executeQuery();

      result = DBConvert.toRecords(rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.close(rs, statement);
    }

    return result;
  }

  @Override
  public int execute(String sql, Collection<?> params, int queryTimeOut) {

    Connection conn;
    PreparedStatement statement = null;

    try {
      conn = this.openConnection();
      statement = DBUtil.getPstForUpdate(conn, false, sql, params);
      statement.setQueryTimeout(queryTimeOut);
      return DBUtil.executeUpdate(statement);
    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      this.close(statement);
    }
  }

  public int execute(
      String sql, ThrowingConsumer<PreparedStatement, SQLException> consumer, int queryTimeOut) {
    Connection conn;
    PreparedStatement statement = null;

    try {
      conn = this.openConnection();
      statement = DBUtil.getPstForUpdate(conn, false, sql);
      consumer.accept(statement);
      statement.setQueryTimeout(queryTimeOut);
      return DBUtil.executeUpdate(statement);
    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      this.close(statement);
    }
  }

  @Override
  public int create(
      String sql,
      Collection<?> params,
      boolean autoGeneratedPK,
      ThrowingConsumer<ResultSet, SQLException> consumer) {
    int result;
    Connection conn;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      conn = this.openConnection();
      statement = DBUtil.getPstForUpdate(conn, autoGeneratedPK, sql, params);
      statement.setQueryTimeout(this.dbConfig.getQueryTimeout());
      result = DBUtil.executeUpdate(statement);

      if (autoGeneratedPK) {
        rs = statement.getGeneratedKeys();
        consumer.accept(rs);
      }
      return result;

    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      this.close(rs, statement);
    }
  }

  @Override
  public int createBatch(
      String sql,
      Collection<Collection<?>> paramsList,
      boolean autoGeneratedPK,
      ThrowingConsumer<ResultSet, SQLException> consumer) {
    int result;
    Connection conn;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {

      conn = this.openConnection();
      statement = DBUtil.getPstForUpdate(conn, autoGeneratedPK, sql);
      statement.setQueryTimeout(this.dbConfig.getQueryTimeout());
      result = DBUtil.executeBatch(statement, paramsList, this.dbConfig.getBatchSize());

      if (autoGeneratedPK) {
        rs = statement.getGeneratedKeys();
        consumer.accept(rs);
      }

      return result;
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.close(rs, statement);
    }
  }

  @Override
  public int updateBatch(String sql, Collection<Collection<?>> paramsList) {
    Connection conn;
    PreparedStatement statement = null;

    try {
      conn = this.openConnection();
      statement = DBUtil.getPstForUpdate(conn, false, sql);
      statement.setQueryTimeout(this.dbConfig.getQueryTimeout());
      return DBUtil.executeBatch(statement, paramsList, this.dbConfig.getBatchSize());

    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.close(statement);
    }
  }
}
