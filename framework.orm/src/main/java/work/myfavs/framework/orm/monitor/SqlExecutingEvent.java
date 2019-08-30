package work.myfavs.framework.orm.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * SQL 执行前事件
 */
@Data
public class SqlExecutingEvent {

  private Sql                 sql;
  private Map<String, Object> context = new HashMap<>();

  public SqlExecutingEvent() {

  }

  public SqlExecutingEvent(Sql sql) {

    this.sql = sql;
  }

  public SqlExecutingEvent(String sql, List<Object> params) {

    this.sql = new Sql(sql, params);
  }

}
