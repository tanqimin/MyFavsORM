package work.myfavs.framework.orm.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import work.myfavs.framework.orm.meta.clause.Sql;

/** SQL 执行前事件 */
public class SqlExecutingEvent {

  private Sql sql;
  private Map<String, Object> context = new HashMap<>();

  public Sql getSql() {

    return sql;
  }

  public void setSql(Sql sql) {

    this.sql = sql;
  }

  public Map<String, Object> getContext() {

    return context;
  }

  public void setContext(Map<String, Object> context) {

    this.context = context;
  }

  public SqlExecutingEvent() {}

  public SqlExecutingEvent(Sql sql) {

    this.sql = sql;
  }

  public SqlExecutingEvent(String sql, List params) {

    this.sql = new Sql(sql, params);
  }
}
