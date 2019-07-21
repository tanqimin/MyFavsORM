package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Cond
    extends Clause {

  private Cond() {

  }

  private Cond(String sql) {

    super(sql);
  }

  private Cond(String sql,
               Object param) {

    super(sql);
    super.params.add(param);
  }

  private Cond(String sql,
               Object... params) {

    super(sql);
    super.params.addAll(Arrays.asList(params));
  }

  /**
   * 创建 = 条件，如 field = param，如果 param 为 null，
   * 则忽略当前条件
   *
   * @param field 字段
   * @param param 参数值
   *
   * @return Cond
   */
  public static Cond eq(String field,
                        Object param) {

    return eq(field, param, true);
  }

  /**
   * 创建 = 条件，如 field = param，如果 param 为 null，
   * 且 ignoreNull = false 时，创建 field IS NULL
   *
   * @param field      字段
   * @param param      参数值
   * @param ignoreNull 是否忽略 null 值
   *
   * @return Cond
   */
  public static Cond eq(String field,
                        Object param,
                        boolean ignoreNull) {

    if (param == null) {
      return ignoreNull
          ? new Cond()
          : isNull(field);
    }
    return new Cond(StrUtil.format(" {} = ?", field), param);
  }

  /**
   * 创建 != 条件，如 field != param，如果 param 为 null，
   * 则忽略当前条件
   *
   * @param field 字段
   * @param param 参数值
   *
   * @return Cond
   */
  public static Cond ne(String field,
                        Object param) {

    return ne(field, param, true);
  }

  /**
   * 创建 != 条件，如 field != param，如果 param 为 null，
   * 且 ignoreNull = false 时，创建 field IS NOT NULL
   *
   * @param field      字段
   * @param param      参数值
   * @param ignoreNull 是否忽略 null 值
   *
   * @return Cond
   */
  public static Cond ne(String field,
                        Object param,
                        boolean ignoreNull) {

    if (param == null) {
      return ignoreNull
          ? new Cond()
          : isNotNull(field);
    }
    return new Cond(StrUtil.format(" {} <> ?", field), param);
  }

  /**
   * 创建 IS NULL 条件
   *
   * @param field 字段
   *
   * @return Cond
   */
  public static Cond isNull(String field) {

    return new Cond(StrUtil.format(" {} IS NULL", field));
  }

  /**
   * 创建 IS NOT NULL 条件
   *
   * @param field 字段
   *
   * @return Cond
   */
  public static Cond isNotNull(String field) {

    return new Cond(StrUtil.format(" {} IS NOT NULL", field));
  }

  public static Cond gt(String field,
                        Object param) {

    if (param == null) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} > ?", field), param);
  }

  public static Cond ge(String field,
                        Object param) {

    if (param == null) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} >= ?", field), param);
  }

  public static Cond lt(String field,
                        Object param) {

    if (param == null) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} < ?", field), param);
  }

  public static Cond le(String field,
                        Object param) {

    if (param == null) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} <= ?", field), param);
  }

  public static Cond like(String field,
                          Object param) {

    if (param == null) {
      return new Cond();
    }

    if (param.toString()
             .contains("%") || param.toString()
                                    .contains("_")) {
      return new Cond(StrUtil.format(" {} LIKE ?", field), param);
    }

    return eq(field, param);
  }

  public static Cond between(String field,
                             Object param1,
                             Object param2) {

    if (param1 == null && param2 == null) {
      return new Cond();
    }
    if (param2 == null) {
      return ge(field, param1);
    }
    if (param1 == null) {
      return le(field, param2);
    }
    return new Cond(StrUtil.format(" {} BETWEEN ? AND ?", field), param1, param2);
  }

  public static Cond in(String field,
                        List params) {

    return in(field, params, true);
  }

  public static Cond in(String field,
                        List params,
                        boolean ignoreEmptyParams) {

    Sql    inClauseSql;
    String sql;
    List   sqlParams;
    int    paramCnt;

    inClauseSql = createInClauseParams(params);
    sql = inClauseSql.sql.toString();
    sqlParams = inClauseSql.params;

    paramCnt = sqlParams.size();

    if (paramCnt == 0) {
      return ignoreEmptyParams
          ? new Cond()
          : new Cond(StrUtil.format(" 1 > 2"));
    }

    if (paramCnt == 1) {
      return eq(field, sqlParams.get(0));
    }
    return new Cond(StrUtil.format(" {} IN ({})", field, sql), sqlParams.toArray());
  }

  public static Cond in(String field,
                        Sql sql) {

    return new Cond(StrUtil.format(" {} IN ({})", field, sql.sql), sql.params.toArray());
  }

  public static Cond notIn(String field,
                           List params) {

    return notIn(field, params, true);
  }

  public static Cond notIn(String field,
                           List params,
                           boolean ignoreEmptyParams) {

    Sql    inClauseSql;
    String sql;
    List   sqlParams;
    int    paramCnt;

    inClauseSql = createInClauseParams(params);
    sql = inClauseSql.sql.toString();
    sqlParams = inClauseSql.params;

    paramCnt = sqlParams.size();

    if (paramCnt == 0) {
      return ignoreEmptyParams
          ? new Cond()
          : new Cond(StrUtil.format(" 1 > 2"));
    }

    if (paramCnt == 1) {
      return ne(field, sqlParams.get(0));
    }
    return new Cond(StrUtil.format(" {} NOT IN ({})", field, sql), sqlParams.toArray());
  }

  public static Cond notIn(String field,
                           Sql sql) {

    return new Cond(StrUtil.format(" {} NOT IN ({})", field, sql.sql), sql.params.toArray());
  }

  private static Sql createInClauseParams(List params) {

    Sql           sql;
    StringBuilder sqlBuilder;
    List          sqlParams;

    sqlBuilder = new StringBuilder();
    sqlParams = new ArrayList();
    if (params != null && params.size() > 0) {
      for (Object param : params) {
        if (param == null) {
          continue;
        }
        sqlBuilder.append("?,");
        sqlParams.add(param);
      }

      sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(","));
    }

    sql = new Sql(sqlBuilder.toString(), sqlParams);
    return sql;
  }

  public static Cond exists(Sql subSql) {

    return new Cond(StrUtil.format(" EXISTS ({})", subSql.sql), subSql.params.toArray());
  }

  public static Cond exists(Supplier<Sql> supplier) {

    return exists(supplier.get());
  }

  public static Cond notExists(Sql subSql) {

    return new Cond(StrUtil.format(" NOT EXISTS ({})", subSql.sql), subSql.params.toArray());
  }

  public static Cond notExists(Supplier<Sql> supplier) {

    return notExists(supplier.get());
  }

  public Cond and(Cond cond) {

    this.sql.append(StrUtil.format(" AND {}", cond.sql));
    this.params.addAll(cond.params);
    return this;
  }

  public Cond or(Cond cond) {

    this.sql.append(StrUtil.format(" OR {}", cond.sql));
    this.params.addAll(cond.params);
    return this;
  }

}
