package work.myfavs.framework.orm.repository.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import work.myfavs.framework.orm.meta.clause.Sql;

@Data
public class SqlExecutingContext {

  private Sql                 sql;
  private Map<String, Object> context = new HashMap<>();

  public SqlExecutingContext() {

  }

  public SqlExecutingContext(Sql sql) {

    this.sql = sql;
  }

  public SqlExecutingContext(String sql, List<Object> params) {

    this.sql = new Sql(sql, params);
  }

}
