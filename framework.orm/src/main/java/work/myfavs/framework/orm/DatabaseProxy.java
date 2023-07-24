package work.myfavs.framework.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collection;
import java.util.List;
import work.myfavs.framework.orm.util.SqlLog;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;
import work.myfavs.framework.orm.util.func.ThrowingRunnable;
import work.myfavs.framework.orm.util.func.ThrowingSupplier;

public class DatabaseProxy implements IDatabase {
  private final IDatabase database;
  private final SqlLog sqlLog;

  public DatabaseProxy(DBTemplate dbTemplate) {
    this.database = new Database(dbTemplate);
    this.sqlLog = new SqlLog(dbTemplate);
  }

  @Override
  public Connection open() {
    return this.database.open();
  }

  @Override
  public void close() {
    this.database.close();
  }

  @Override
  public void commit() {
    this.database.commit();
  }

  @Override
  public Savepoint setSavepoint() {
    return this.database.setSavepoint();
  }

  @Override
  public Savepoint setSavepoint(String name) {
    return this.database.setSavepoint(name);
  }

  @Override
  public void rollback() {
    this.database.rollback();
  }

  @Override
  public void rollback(Savepoint savepoint) {
    this.database.rollback(savepoint);
  }

  @Override
  public <TResult> TResult tx(ThrowingFunction<Connection, TResult, SQLException> func) {
    return this.database.tx(func);
  }

  @Override
  public <TResult> TResult tx(ThrowingSupplier<TResult, SQLException> supplier) {
    return this.database.tx(supplier);
  }

  @Override
  public void tx(ThrowingConsumer<Connection, SQLException> consumer) {
    this.database.tx(consumer);
  }

  @Override
  public void tx(ThrowingRunnable<SQLException> runnable) {
    this.database.tx(runnable);
  }

  @Override
  public <TView> List<TView> find(Class<TView> tViewClass, String sql, Collection<?> params) {
    sqlLog.showSql(sql, params);
    List<TView> result = this.database.find(tViewClass, sql, params);
    sqlLog.showResult(result);
    return result;
  }

  @Override
  public int execute(String sql, Collection<?> params, int queryTimeOut) {
    sqlLog.showSql(sql, params);
    int execute = this.database.execute(sql, params, queryTimeOut);
    this.sqlLog.showAffectedRows(execute);
    return execute;
  }

  @Override
  public int create(
      String sql,
      Collection<?> params,
      boolean autoGeneratedPK,
      ThrowingConsumer<ResultSet, SQLException> pkConsumer) {
    this.sqlLog.showSql(sql, params);
    int result = this.database.create(sql, params, autoGeneratedPK, pkConsumer);
    this.sqlLog.showAffectedRows(result);
    return result;
  }

  @Override
  public int createBatch(
      String sql,
      Collection<Collection<?>> paramsList,
      boolean autoGeneratedPK,
      ThrowingConsumer<ResultSet, SQLException> consumer) {
    this.sqlLog.showBatchSql(sql, paramsList);
    int result = this.database.createBatch(sql, paramsList, autoGeneratedPK, consumer);
    this.sqlLog.showAffectedRows(result);
    return result;
  }

  @Override
  public int updateBatch(String sql, Collection<Collection<?>> paramsList) {
    this.sqlLog.showBatchSql(sql, paramsList);
    int result = this.database.updateBatch(sql, paramsList);
    this.sqlLog.showAffectedRows(result);
    return result;
  }
}