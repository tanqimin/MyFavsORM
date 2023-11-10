package work.myfavs.framework.orm;

import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.util.JdbcUtil;
import work.myfavs.framework.orm.util.SqlLog;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;
import work.myfavs.framework.orm.util.func.ThrowingRunnable;
import work.myfavs.framework.orm.util.func.ThrowingSupplier;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

abstract public class MyfavsConnection {

  protected final DBTemplate  dbTemplate;
  protected final SqlLog      sqlLog;
  protected final ConnFactory connFactory;

  public MyfavsConnection(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
    this.connFactory = dbTemplate.getConnectionFactory();
    this.sqlLog = new SqlLog(dbTemplate);
  }

  /**
   * 是否使用SQL Server数据库
   *
   * @return 如果当前数据库为SQL Server，返回true，否则返回false
   */
  protected boolean isSqlServer() {
    return StrUtil.equals(getDbConfig().getDbType(), DbType.SQL_SERVER)
        || StrUtil.equals(getDbConfig().getDbType(), DbType.SQL_SERVER_2012);
  }

  protected DBConfig getDbConfig() {
    return this.dbTemplate.getDbConfig();
  }

  protected IDialect getDialect() {
    return this.dbTemplate.getDbConfig().getDialect();
  }

  protected Connection getCurrentConnection() {
    return this.connFactory.getCurrentConnection();
  }

  public Connection open() {
    return this.connFactory.openConnection();
  }


  public void close() {
    this.connFactory.closeConnection(getCurrentConnection());
  }

  public Savepoint setSavepoint() {
    return JdbcUtil.setSavepoint(getCurrentConnection());
  }

  public Savepoint setSavepoint(String name) {
    return JdbcUtil.setSavepoint(getCurrentConnection(), name);
  }

  public void rollback() {
    JdbcUtil.rollback(getCurrentConnection());
  }

  public void rollback(Savepoint savepoint) {
    JdbcUtil.rollback(getCurrentConnection(), savepoint);
  }

  public void commit() {
    JdbcUtil.commit(getCurrentConnection());
  }

  public <TResult> TResult tx(ThrowingSupplier<TResult, SQLException> supplier) {
    try {
      this.open();
      return supplier.get();
    } catch (Exception e) {
      this.rollback();
      throw new DBException(e);
    } finally {
      this.close();
    }
  }


  public void tx(ThrowingRunnable<SQLException> runnable) {
    try {
      this.open();
      runnable.run();
    } catch (Exception e) {
      this.rollback();
      throw new DBException(e);
    } finally {
      this.close();
    }
  }

  public <TResult> TResult jdbcTx(ThrowingFunction<Connection, TResult, SQLException> func) {
    try {
      Connection conn = this.open();
      return func.apply(conn);
    } catch (Exception e) {
      this.rollback();
      throw new DBException(e);
    } finally {
      this.close();
    }
  }

  public void jdbcTx(ThrowingConsumer<Connection, SQLException> consumer) {
    try {
      consumer.accept(this.open());
    } catch (Exception e) {
      this.rollback();
      throw new DBException(e);
    } finally {
      this.close();
    }
  }

}
