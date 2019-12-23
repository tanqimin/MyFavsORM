package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * SQL 语句基类
 */
@Data
abstract public class Clause {

  protected StringBuilder sql;
  protected List<Object>  params;

  public String getSqlString() {

    return StrUtil.toString(sql);
  }

  public Clause() {

    sql = new StringBuilder();
    params = new ArrayList<>();
  }

  public Clause(String sql) {

    this();
    this.sql.append(sql);
  }

  public Clause(String sql, List params) {

    this(sql);
    if (params != null && params.size() > 0) {
      this.params.addAll(params);
    }
  }

}
