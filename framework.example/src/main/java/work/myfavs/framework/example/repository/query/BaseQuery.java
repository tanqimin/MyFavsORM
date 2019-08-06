package work.myfavs.framework.example.repository.query;

import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Query;
import work.myfavs.framework.orm.repository.monitor.SqlAnalysis;
import work.myfavs.framework.orm.repository.monitor.SqlExecutedEvent;
import work.myfavs.framework.orm.repository.monitor.SqlExecutingEvent;

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
  protected void afterQuery(SqlExecutedEvent context) {
    SqlAnalysis analysis = context.getAnalysis();
    log.info("            SQL: {}", context.getSql().getSql().toString());
    log.info("  AFFECTED ROWS: {}", analysis.getAffectedRows());
    log.info("  QUERY ELAPSED: {}", analysis.getElapsed());
    log.info("MAPPING ELAPSED: {}", analysis.getMappingElapsed());
  }

  @Override
  protected void beforeQuery(SqlExecutingEvent context) {

  }

}
