package work.myfavs.framework.orm.repository.monitor;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import work.myfavs.framework.orm.meta.clause.Sql;

@Data
public class SqlExecutingContext {

  private Sql                 sql;
  private Map<String, Object> context = new HashMap<>();

  public SqlExecutingContext(Sql sql) {

    this.sql = sql;
  }

}
