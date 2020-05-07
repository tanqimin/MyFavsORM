package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SQL 语句基类
 */
@SuppressWarnings("unchecked")
abstract public class Clause {

  protected StringBuilder sql;
  protected List params;

  //region Getter && Setter
  public StringBuilder getSql() {

    return sql;
  }

  public void setSql(StringBuilder sql) {

    this.sql = sql;
  }

  public List getParams() {

    return params;
  }

  public void setParams(Collection params) {

    this.params = new ArrayList(params);
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
      Collection params) {

    this(sql);
    if (params != null && params.size() > 0) {
      this.params.addAll(params);
    }
  }
  //endregion

  public String getSqlString() {

    return StrUtil.toString(sql);
  }

  @Override
  public String toString() {
    return StrUtil.toString(sql);
  }

  public Clause deleteLastChar(String str) {
    this.sql.deleteCharAt(this.sql.lastIndexOf(str));
    return this;
  }
}
