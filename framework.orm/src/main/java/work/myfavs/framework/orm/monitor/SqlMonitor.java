package work.myfavs.framework.orm.monitor;

/**
 * SQL监控器
 */
public class SqlMonitor {
//
//  private StopWatch         stopWatch;
//  private SqlExecutingEvent sqlExecutingEvent;
//  private SqlExecutedEvent  sqlExecutedEvent;
//
//  private SqlMonitor() {
//
//    stopWatch = new StopWatch();
//    sqlExecutingEvent = new SqlExecutingEvent();
//    sqlExecutedEvent = new SqlExecutedEvent(new SqlAnalysis());
//  }
//
//  /**
//   * 创建实例
//   *
//   * @return SQL监控器实例
//   */
//  public static SqlMonitor createInstance() {
//
//    return new SqlMonitor();
//  }
//
//  /**
//   * 启动性能监控
//   *
//   * @param sql    SQL语句
//   * @param params SQL参数
//   *
//   * @return SqlExecutingContext
//   */
//  public SqlExecutingEvent start(String sql, List<Object> params) {
//
//    sqlExecutingEvent.setSql(new Sql(sql, params));
//    sqlExecutedEvent.setSql(new Sql(sql, params));
//    stopWatch.start();
//    return sqlExecutingEvent;
//  }
//
//  /**
//   * 重启性能监控（查询用）
//   */
//  public void restart() {
//
//    stopWatch.stop();
//    sqlExecutedEvent.getAnalysis().setElapsed(stopWatch.getLastTaskTimeMillis());
//    stopWatch.start();
//  }
//
//  /**
//   * 停止查询监控
//   *
//   * @param affectedRows 结果数量
//   *
//   * @return SqlExecutedContext
//   */
//  public SqlExecutedEvent stopQuery(int affectedRows) {
//
//    if (stopWatch.isRunning()) {
//      stopWatch.stop();
//    }
//    sqlExecutedEvent.getAnalysis().setMappingElapsed(stopWatch.getLastTaskTimeMillis());
//    sqlExecutedEvent.getAnalysis().setAffectedRows(affectedRows);
//    return sqlExecutedEvent;
//  }
//
//  /**
//   * 停止执行监控
//   *
//   * @param affectedRows 影响行数
//   *
//   * @return SqlExecutedContext
//   */
//  public SqlExecutedEvent stopExec(int affectedRows) {
//
//    if (stopWatch.isRunning()) {
//      stopWatch.stop();
//    }
//    sqlExecutedEvent.getAnalysis().setElapsed(stopWatch.getLastTaskTimeMillis());
//    sqlExecutedEvent.getAnalysis().setAffectedRows(affectedRows);
//    return sqlExecutedEvent;
//  }
//
//  /**
//   * 错误处理
//   *
//   * @param throwable Throwable
//   *
//   * @return DBException
//   */
//  public DBException error(Throwable throwable) {
//
//    if (stopWatch.isRunning()) {
//      stopWatch.stop();
//    }
//    sqlExecutedEvent.getAnalysis().setHasError(true);
//    sqlExecutedEvent.getAnalysis().setThrowable(throwable);
//    return new DBException(throwable);
//  }

}
