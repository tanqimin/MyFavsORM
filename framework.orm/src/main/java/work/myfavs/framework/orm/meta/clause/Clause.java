package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** SQL 语句基类 */
@SuppressWarnings("unchecked")
public abstract class Clause {

  protected static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";
  protected StringBuilder sql;
  protected List params;

  // region Getter && Setter
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
  // endregion

  // region Constructor
  public Clause() {

    sql = new StringBuilder();
    params = new ArrayList<>();
  }

  public Clause(String sql) {

    this();
    this.sql.append(sql);
  }

  public Clause(String sql, Collection params) {

    this(sql);
    if (params != null && params.size() > 0) {
      this.params.addAll(params);
    }
  }
  // endregion

  @Override
  public String toString() {
    return StrUtil.toString(sql);
  }

  public Clause deleteLastChar(String str) {
    this.sql.deleteCharAt(this.sql.lastIndexOf(str));
    return this;
  }

  protected static String checkInjection(String sql) {
    if (StrUtil.isNotEmpty(sql) && !sql.matches(SQL_PATTERN)) {
      throw new DBException("参数 {} 存在非法字符串", sql);
    }

    return sql;
  }
}
