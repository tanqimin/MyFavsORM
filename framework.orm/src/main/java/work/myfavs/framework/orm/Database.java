package work.myfavs.framework.orm;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.convert.DBConvert;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;
import work.myfavs.framework.orm.util.func.ThrowingRunnable;
import work.myfavs.framework.orm.util.func.ThrowingSupplier;

public class Database implements IDatabase {

  private final DBTemplate dbTemplate;

  public Database(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
  }

  @Override
  public DBConfig dbConfig() {
    return this.dbTemplate.getDbConfig();
  }

  @Override
  public IDialect dialect() {
    return dbConfig().getDialect();
  }

  @Override
  public ConnFactory connFactory() {
    return this.dbTemplate.getConnectionFactory();
  }

  public Connection open() {
    return connFactory().openConnection();
  }

  public void close() {
    connFactory().closeConnection(connFactory().getCurrentConnection());
  }

  /** 提交事务 */
  public void commit() {

    try {
      this.connFactory().getCurrentConnection().commit();
    } catch (SQLException e) {
      throw new DBException(e, "Fail to commit transaction, error message:");
    }
  }

  public Savepoint setSavepoint() {
    try {
      return this.connFactory().getCurrentConnection().setSavepoint();
    } catch (SQLException e) {
      throw new DBException(e, "Fail to set save point, error message:");
    }
  }

  public Savepoint setSavepoint(String name) {
    try {
      return this.connFactory().getCurrentConnection().setSavepoint(name);
    } catch (SQLException e) {
      throw new DBException(e, "Fail to set save point, error message:");
    }
  }

  /** 回滚事务 */
  public void rollback() {

    try {
      this.connFactory().getCurrentConnection().rollback();
    } catch (SQLException e) {
      throw new DBException(e, "Fail to rollback transaction, error message:");
    }
  }

  public void rollback(Savepoint savepoint) {
    try {
      this.connFactory().getCurrentConnection().rollback(savepoint);
    } catch (SQLException e) {
      throw new DBException(e, "Fail to rollback transaction, error message:");
    }
  }

  @Override
  public <TResult> TResult tx(ThrowingFunction<Connection, TResult, SQLException> func) {
    try {
      return func.apply(open());
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      close();
    }
  }

  @Override
  public <TResult> TResult tx(ThrowingSupplier<TResult, SQLException> supplier) {
    try {
      open();
      return supplier.get();
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      close();
    }
  }

  @Override
  public void tx(ThrowingConsumer<Connection, SQLException> consumer) {
    try {
      consumer.accept(open());
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      close();
    }
  }

  @Override
  public void tx(ThrowingRunnable<SQLException> runnable) {
    try {
      open();
      runnable.run();
    } catch (Exception e) {
      rollback();
      throw new DBException(e);
    } finally {
      close();
    }
  }

  @Override
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection params) {

    Metadata.get(viewClass);

    Connection conn;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<TView> result;

    try {
      conn = this.open();
      pstmt = DBUtil.getPstForQuery(conn, sql, params);
      pstmt.setQueryTimeout(dbConfig().getQueryTimeout());
      pstmt.setFetchSize(dbConfig().getFetchSize());
      rs = pstmt.executeQuery();

      result = DBConvert.toList(viewClass, rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt, rs);
      this.close();
    }

    return result;
  }

  @Override
  public int execute(String sql, Collection params, int queryTimeOut) {

    Connection conn;
    PreparedStatement pstmt = null;

    try {
      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, false, sql, params);
      pstmt.setQueryTimeout(queryTimeOut);
      return DBUtil.executeUpdate(pstmt);
    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      DBUtil.close(pstmt);
      this.close();
    }
  }

  public int execute(
      String sql, ThrowingConsumer<PreparedStatement, SQLException> consumer, int queryTimeOut) {
    Connection conn;
    PreparedStatement pstmt = null;

    try {
      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, false, sql);
      consumer.accept(pstmt);
      pstmt.setQueryTimeout(queryTimeOut);
      return DBUtil.executeUpdate(pstmt);
    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      DBUtil.close(pstmt);
      this.close();
    }
  }

  @Override
  public int create(
      String sql,
      Collection params,
      boolean autoGeneratedPK,
      ThrowingConsumer<ResultSet, SQLException> consumer) {
    int result;
    Connection conn;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, autoGeneratedPK, sql, params);
      pstmt.setQueryTimeout(this.dbConfig().getQueryTimeout());
      result = DBUtil.executeUpdate(pstmt);

      if (autoGeneratedPK) {
        rs = pstmt.getGeneratedKeys();
        consumer.accept(rs);
      }
      return result;

    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      DBUtil.close(pstmt, rs);
      this.close();
    }
  }

  @Override
  public int createBatch(
      String sql,
      Collection<Collection> paramsList,
      boolean autoGeneratedPK,
      ThrowingConsumer<ResultSet, SQLException> consumer) {
    int result;
    Connection conn;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, autoGeneratedPK, sql);
      pstmt.setQueryTimeout(this.dbConfig().getQueryTimeout());
      result = DBUtil.executeBatch(pstmt, paramsList, this.dbConfig().getBatchSize());

      if (autoGeneratedPK) {
        rs = pstmt.getGeneratedKeys();
        consumer.accept(rs);
      }

      return result;
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt, rs);
      this.close();
    }
  }

  @Override
  public int updateBatch(String sql, Collection<Collection> paramsList) {
    Connection conn;
    PreparedStatement pstmt = null;

    try {
      conn = this.open();
      pstmt = DBUtil.getPstForUpdate(conn, false, sql);
      pstmt.setQueryTimeout(this.dbConfig().getQueryTimeout());
      return DBUtil.executeBatch(pstmt, paramsList, this.dbConfig().getBatchSize());

    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(pstmt);
      this.close();
    }
  }
}
