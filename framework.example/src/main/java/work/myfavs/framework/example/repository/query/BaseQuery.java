package work.myfavs.framework.example.repository.query;

import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.repository.Query;
import work.myfavs.framework.orm.repository.monitor.SqlAnalysis;
import work.myfavs.framework.orm.repository.monitor.SqlExecutedContext;
import work.myfavs.framework.orm.repository.monitor.SqlExecutingContext;

@Slf4j
public class BaseQuery
    extends Query {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  public BaseQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  @Override
  protected void afterQuery(SqlExecutedContext context) {

    logContext(context);
  }

  @Override
  protected void beforeQuery(SqlExecutingContext context) {

  }

  @Override
  protected void afterExecute(SqlExecutedContext context) {

    logContext(context);
  }

  @Override
  protected void beforeExecute(SqlExecutingContext context) {

  }


  private void logContext(SqlExecutedContext context) {

    if (log.isInfoEnabled()) {
      Sql         sql      = context.getSql();
      SqlAnalysis analysis = context.getAnalysis();
      log.info("            SQL: {}", sql.getSql());
      log.info("     PARAMETERS: {}", sql.getParams());
      if (analysis.isHasError()) {
        log.info("      EXCEPTION: {}", analysis.getThrowable().getClass());
        log.info("       MESSAGES: {}", analysis.getThrowable().getMessage());
      } else {
        log.info("  QUERY ELAPSED: {}", analysis.getElapsed());
        log.info("MAPPING ELAPSED: {}", analysis.getMappingElapsed());
        log.info("  AFFECTED ROWS: {}", analysis.getAffectedRows());
      }
    }
  }

}
