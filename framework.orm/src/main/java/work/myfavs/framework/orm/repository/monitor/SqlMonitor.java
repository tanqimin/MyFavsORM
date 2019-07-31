package work.myfavs.framework.orm.repository.monitor;

import java.util.List;
import org.springframework.util.StopWatch;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * SQL监控器
 */
public class SqlMonitor {

  private StopWatch           stopWatch;
  private SqlExecutingContext sqlExecutingContext;
  private SqlExecutedContext  sqlExecutedContext;

  private SqlMonitor() {

    stopWatch = new StopWatch();
    sqlExecutingContext = new SqlExecutingContext();
    sqlExecutedContext = new SqlExecutedContext(new SqlAnalysis());
  }

  /**
   * 创建实例
   *
   * @return SQL监控器实例
   */
  public static SqlMonitor createInstance() {

    return new SqlMonitor();
  }

  /**
   * 启动性能监控
   *
   * @param sql    SQL语句
   * @param params SQL参数
   *
   * @return SqlExecutingContext
   */
  public SqlExecutingContext start(String sql, List<Object> params) {

    sqlExecutingContext.setSql(new Sql(sql, params));
    sqlExecutedContext.setSql(new Sql(sql, params));
    stopWatch.start();
    return sqlExecutingContext;
  }

  /**
   * 重启性能监控（查询用）
   */
  public void restart() {

    stopWatch.stop();
    sqlExecutedContext.getAnalysis().setElapsed(stopWatch.getLastTaskTimeMillis());
    stopWatch.start();
  }

  /**
   * 停止查询监控
   *
   * @param affectedRows 结果数量
   *
   * @return SqlExecutedContext
   */
  public SqlExecutedContext stopQuery(int affectedRows) {

    if (stopWatch.isRunning()) {
      stopWatch.stop();
    }
    sqlExecutedContext.getAnalysis().setMappingElapsed(stopWatch.getLastTaskTimeMillis());
    sqlExecutedContext.getAnalysis().setAffectedRows(affectedRows);
    return sqlExecutedContext;
  }

  /**
   * 停止执行监控
   *
   * @param affectedRows 影响行数
   *
   * @return SqlExecutedContext
   */
  public SqlExecutedContext stopExec(int affectedRows) {

    if (stopWatch.isRunning()) {
      stopWatch.stop();
    }
    sqlExecutedContext.getAnalysis().setElapsed(stopWatch.getLastTaskTimeMillis());
    sqlExecutedContext.getAnalysis().setAffectedRows(affectedRows);
    return sqlExecutedContext;
  }

  /**
   * 错误处理
   *
   * @param throwable Throwable
   *
   * @return DBException
   */
  public DBException error(Throwable throwable) {

    if (stopWatch.isRunning()) {
      stopWatch.stop();
    }
    sqlExecutedContext.getAnalysis().setHasError(true);
    sqlExecutedContext.getAnalysis().setThrowable(throwable);
    return new DBException(throwable);
  }

}
