package work.myfavs.framework.orm.meta.clause;

import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SQL 语句基类
 */
public abstract class Clause {

  protected static String        SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";
  protected        StringBuilder sql;
  protected        List<Object>  params;

  // region Getter && Setter
  public StringBuilder getSql() {

    return sql;
  }

  public void setSql(StringBuilder sql) {

    this.sql = sql;
  }

  public List<Object> getParams() {

    return params;
  }

  public void setParams(Collection<?> params) {

    this.params = new ArrayList<>(params);
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

  public Clause(String sql, Collection<?> params) {

    this(sql);
    if (CollectionUtil.isNotEmpty(params)) {
      this.params.addAll(params);
    }
  }
  // endregion

  @Override
  public String toString() {
    return StringUtil.toStr(sql);
  }

  public Clause deleteLastChar(String str) {
    this.sql.deleteCharAt(this.sql.lastIndexOf(str));
    return this;
  }

  protected static String checkInjection(String sql) {
    if (StringUtil.isNotEmpty(sql) && !sql.matches(SQL_PATTERN)) {
      throw new DBException("参数 %s 中的内容存在注入风险, 请检查!", sql);
    }

    return sql;
  }
}
