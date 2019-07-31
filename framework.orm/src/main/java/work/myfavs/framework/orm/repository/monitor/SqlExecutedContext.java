package work.myfavs.framework.orm.repository.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import work.myfavs.framework.orm.meta.clause.Sql;

@EqualsAndHashCode(callSuper = true)
@Data
public class SqlExecutedContext
    extends SqlExecutingContext {

  private SqlAnalysis analysis;

  public SqlExecutedContext(SqlAnalysis analysis) {

    this.analysis = analysis;
  }

  public SqlExecutedContext(Sql sql, SqlAnalysis analysis) {

    super(sql);
    this.analysis = analysis;
  }

}
