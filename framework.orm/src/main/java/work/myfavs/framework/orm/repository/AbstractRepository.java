package work.myfavs.framework.orm.repository;

import cn.hutool.core.util.StrUtil;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.IDialect;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.repository.monitor.SqlAnalysis;
import work.myfavs.framework.orm.repository.monitor.SqlExecutedContext;
import work.myfavs.framework.orm.repository.monitor.SqlExecutingContext;
import work.myfavs.framework.orm.util.DBConvert;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.exception.DBException;

@Slf4j
abstract public class AbstractRepository {

  protected IDialect   dialect;
  protected DBTemplate dbTemplate;
  private   boolean    showSql;
  private   boolean    showResult;

  private AbstractRepository() {}

  public AbstractRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
    dialect = dbTemplate.getDialect();
    showSql = dbTemplate.getShowSql();
    showResult = dbTemplate.getShowResult();
  }


  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  protected <TView> List<TView> find(Class<TView> viewClass, String sql, List<Object> params) {

    Metadata.get(viewClass);

    Connection        conn  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;
    List<TView>       result;

    SqlAnalysis         sqlAnalysis         = new SqlAnalysis();
    StopWatch           stopWatch           = new StopWatch();
    SqlExecutingContext sqlExecutingContext = new SqlExecutingContext(new Sql(sql, params));
    this.beforeQuery(sqlExecutingContext);

    try {
      this.showSql(sql, params);
      stopWatch.start(StrUtil.format("[{}]SQL QUERY", getThreadInfo()));

      conn = this.dbTemplate.createConnection();
      pstmt = params == null || params.size() == 0
          ? DBUtil.getPs(conn, sql)
          : DBUtil.getPs(conn, sql, params);
      rs = pstmt.executeQuery();

      stopWatch.stop();
      sqlAnalysis.setElapsed(stopWatch.getLastTaskTimeMillis());
      stopWatch.start(StrUtil.format("[{}]CONVERT TO ENTITY", getThreadInfo()));

      result = DBConvert.toList(viewClass, rs);

      stopWatch.stop();
      sqlAnalysis.setMappingElapsed(stopWatch.getLastTaskTimeMillis());
      sqlAnalysis.setAffectedRows(result.size());
      this.showResult(rs);
      return result;
    } catch (SQLException e) {
      sqlAnalysis.setHasError(true);
      sqlAnalysis.setThrowable(e);

      throw new DBException(e);
    } finally {
      this.dbTemplate.release(conn, pstmt, rs);

      if (stopWatch.isRunning()) {
        stopWatch.stop();
      }
      this.afterQuery(new SqlExecutedContext(sqlExecutingContext.getSql(), sqlAnalysis));
    }
  }

  protected void afterQuery(SqlExecutedContext context)     {}

  protected void beforeQuery(SqlExecutingContext context)   {}

  protected void afterExecute(SqlExecutedContext context)   {}

  protected void beforeExecute(SqlExecutingContext context) {}

  /**
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  protected <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    return this.find(viewClass, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  protected <TView> List<TView> findTop(Class<TView> viewClass, long top, String sql, List<Object> params) {

    Sql querySql = this.dialect.selectTop(1L, top, sql, params);
    return this.find(viewClass, querySql);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   *
   * @return 结果集
   */
  protected <TView> List<TView> findTop(Class<TView> viewClass, long top, Sql sql) {

    return this.findTop(viewClass, top, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   *
   * @return 记录
   */
  protected <TView> TView get(Class<TView> viewClass, String sql, List<Object> params) {

    Iterator<TView> iterator = this.findTop(viewClass, 1L, sql, params).iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
   * @param <TView>   结果集类型泛型
   *
   * @return 记录
   */
  protected <TView> TView get(Class<TView> viewClass, Sql sql) {

    return this.get(viewClass, sql.getSql().toString(), sql.getParams());
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 行数
   */
  protected long count(String sql, List<Object> params) {

    return this.get(Number.class, this.dialect.count(sql, params)).longValue();
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql SQL
   *
   * @return 行数
   */
  protected long count(Sql sql) {

    return this.count(sql.getSql().toString(), sql.getParams());
  }

  /**
   * 获取线程信息
   *
   * @return
   */
  protected String getThreadInfo() {

    Thread currentThread = Thread.currentThread();
    return StrUtil.format("{} - {}", currentThread.getName(), currentThread.getId());
  }

  void showSql(String sql, List<Object> params) {

    if (showSql && log.isInfoEnabled()) {
      StringBuilder logStr = new StringBuilder();
      logStr.append(System.lineSeparator());
      logStr.append(StrUtil.format("          SQL: {}", sql));
      logStr.append(System.lineSeparator());
      logStr.append(StrUtil.format("   PARAMETERS: {}", showParams(params)));
      logStr.append(System.lineSeparator());
      log.info(logStr.toString());
    }
  }

  private static String showParams(List<Object> params) {

    StringBuilder stringBuilder;
    stringBuilder = new StringBuilder();
    if (params == null || params.size() == 0) {
      return stringBuilder.toString();
    }
    for (Object param : params) {
      stringBuilder.append(StrUtil.toString(param)).append(", ");
    }
    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
    return stringBuilder.toString();
  }

  void showBatchSql(String sql, List<List<Object>> paramsList) {

    if (showSql && log.isInfoEnabled()) {
      StringBuilder logStr = new StringBuilder(System.lineSeparator());
      logStr.append("          SQL: ").append(sql);
      if (paramsList != null && paramsList.size() > 0) {
        logStr.append(System.lineSeparator()).append("   PARAMETERS: ").append(System.lineSeparator());
        int i = 0;
        for (List<Object> params : paramsList) {
          logStr.append("PARAM[").append(i++).append("]: ");
          logStr.append(StrUtil.format("{}", showParams(params)));
          logStr.append(System.lineSeparator());
        }
      }
      logStr.append(System.lineSeparator());
      log.info(logStr.toString());
    }
  }

  void showAffectedRows(int result) {

    if (showSql && log.isInfoEnabled()) {
      log.info("AFFECTED ROWS: {}", result);
    }
  }

  private void showResult(ResultSet rs)
      throws SQLException {

    if (showResult && log.isInfoEnabled()) {
      ResultSetMetaData metaData;
      StringBuilder     logStr;
      int               columnCount;
      String            columnLabel;
      Object            columnVal;
      int               rows = 0;

      metaData = rs.getMetaData();
      columnCount = metaData.getColumnCount();

      logStr = new StringBuilder(System.lineSeparator());
      logStr.append(" QUERY RESULT:");
      logStr.append(System.lineSeparator());

      rs.beforeFirst();
      while (rs.next()) {
        rows++;
        logStr.append(StrUtil.format("ROW[{}]: {", rs.getRow()));
        for (int i = 1;
             i <= columnCount;
             i++) {
          columnLabel = metaData.getColumnLabel(i);
          columnVal = rs.getObject(i);
          if (rs.wasNull()) {
            logStr.append(StrUtil.format("\"{}\":null, ", columnLabel));
          } else {
            logStr.append(StrUtil.format("\"{}\":\"{}\", ", columnLabel, columnVal));
          }
        }
        logStr.deleteCharAt(logStr.lastIndexOf(", ")).append("}").append(System.lineSeparator());
      }
      logStr.append(StrUtil.format("TOTAL RECORDS: {}", rows));
      logStr.append(System.lineSeparator());
      log.info(logStr.toString());
    }
  }

}
