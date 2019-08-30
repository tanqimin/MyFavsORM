package work.myfavs.framework.orm.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * SQL 执行后事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SqlExecutedEvent
    extends SqlExecutingEvent {

  private SqlAnalysis analysis;

  public SqlExecutedEvent(SqlAnalysis analysis) {

    this.analysis = analysis;
  }

  public SqlExecutedEvent(Sql sql, SqlAnalysis analysis) {

    super(sql);
    this.analysis = analysis;
  }

}
