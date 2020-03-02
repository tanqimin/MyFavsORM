package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL 语句基类
 */
abstract public class Clause {

  protected StringBuilder sql;
  protected List<Object>  params;

  //region Getter && Setter
  public StringBuilder getSql() {

    return sql;
  }

  public void setSql(StringBuilder sql) {

    this.sql = sql;
  }

  public List<Object> getParams() {

    return params;
  }

  public void setParams(List<Object> params) {

    this.params = params;
  }
  //endregion

  //region Constructor
  public Clause() {

    sql = new StringBuilder();
    params = new ArrayList<>();
  }

  public Clause(String sql) {

    this();
    this.sql.append(sql);
  }

  public Clause(String sql,
                List params) {

    this(sql);
    if (params != null && params.size() > 0) {
      this.params.addAll(params);
    }
  }
  //endregion

  public String getSqlString() {

    return StrUtil.toString(sql);
  }

}
