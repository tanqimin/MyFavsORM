package work.myfavs.framework.orm.monitor;

import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * SQL 执行后事件
 */
public class SqlExecutedEvent
    extends SqlExecutingEvent {

  private SqlAnalysis analysis;

  public SqlAnalysis getAnalysis() {

    return analysis;
  }

  public void setAnalysis(SqlAnalysis analysis) {

    this.analysis = analysis;
  }

  public SqlExecutedEvent(SqlAnalysis analysis) {

    this.analysis = analysis;
  }

  public SqlExecutedEvent(Sql sql,
                          SqlAnalysis analysis) {

    super(sql);
    this.analysis = analysis;
  }

}
