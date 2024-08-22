package work.myfavs.framework.orm.meta.clause;


import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.enumeration.FuzzyMode;
import work.myfavs.framework.orm.meta.enumeration.Operator;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.util.common.ArrayUtil;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.convert.ConvertUtil;
import work.myfavs.framework.orm.util.reflection.FieldVisitor;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

import static work.myfavs.framework.orm.util.common.Constant.*;

/**
 * SQL 条件构建
 */
public class Cond extends Clause {

  /**
   * 构造方法
   */
  private Cond() {}

  /**
   * 构造方法
   *
   * @param sql SQL语句
   */
  private Cond(String sql) {

    super(sql);
  }

  /**
   * 构造方法
   *
   * @param sql    SQL 语句
   * @param param  参数
   * @param params 更多参数
   */
  private Cond(String sql, Object param, Object... params) {

    super(sql);
    super.params.add(param);
    if (ArrayUtil.isNotEmpty(params)) {
      super.params.addAll(Arrays.asList(params));
    }
  }

  private Cond(String sql, Collection<?> params) {

    super(sql);
    super.params.addAll(params);
  }


  /**
   * 创建逻辑删除条件<br/>
   * 如果 logicDelete 不为空，则创建逻辑删除条件
   *
   * @param logicDelete {@link Attribute} 逻辑删除标记字段
   * @return {@link Cond}
   */
  public static Cond logicalDelete(Attribute logicDelete) {

    if (null == logicDelete) return new Cond();
    if (!logicDelete.isLogicDelete()) return new Cond();
    return Cond.eq(logicDelete.getColumnName(), 0);
  }

  /**
   * 创建 = 条件，如 field = param，如果 param 为 null， 则忽略当前条件
   *
   * @param field 字段
   * @param param 参数值
   * @return {@link Cond}
   */
  public static Cond eq(String field, Object param) {

    return eq(field, param, true);
  }

  /**
   * 创建 = 条件，如 field = param，如果 param 为 null， 且 ignoreNull = false 时，创建 field IS NULL
   *
   * @param field      字段
   * @param param      参数值
   * @param ignoreNull 是否忽略 null 值
   * @return {@link Cond}
   */
  public static Cond eq(String field, Object param, boolean ignoreNull) {

    if (null == param) {
      return ignoreNull ? new Cond() : isNull(field);
    }

    Cond cond = new Cond(String.format(" %s = ?", field), param);

    if (param instanceof String && StringUtil.length(param) == 0) {
      return ignoreNull ? new Cond() : cond;
    }

    return cond;
  }

  /**
   * 创建 != 条件，如 field != param，如果 param 为 null， 则忽略当前条件
   *
   * @param field 字段
   * @param param 参数值
   * @return {@link Cond}
   */
  public static Cond ne(String field, Object param) {

    return ne(field, param, true);
  }

  /**
   * 创建 != 条件，如 field != param，如果 param 为 null， 且 ignoreNull = false 时，创建 field IS NOT NULL
   *
   * @param field      字段
   * @param param      参数值
   * @param ignoreNull 是否忽略 null 值
   * @return {@link Cond}
   */
  public static Cond ne(String field, Object param, boolean ignoreNull) {

    if (null == param) {
      return ignoreNull ? new Cond() : isNotNull(field);
    }
    return new Cond(String.format(" %s <> ?", field), param);
  }

  /**
   * 创建 IS NULL 条件
   *
   * @param field 字段
   * @return {@link Cond}
   */
  public static Cond isNull(String field) {

    return new Cond(String.format(" %s IS NULL", field));
  }

  /**
   * 创建 IS NOT NULL 条件
   *
   * @param field 字段
   * @return {@link Cond}
   */
  public static Cond isNotNull(String field) {

    return new Cond(String.format(" %s IS NOT NULL", field));
  }

  /**
   * 创建 field &gt; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return {@link Cond}
   */
  public static Cond gt(String field, Object param) {

    if (null == param) {
      return new Cond();
    }
    return new Cond(String.format(" %s > ?", field), param);
  }

  /**
   * 创建 field &ge; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return {@link Cond}
   */
  public static Cond ge(String field, Object param) {

    if (null == param) {
      return new Cond();
    }
    return new Cond(String.format(" %s >= ?", field), param);
  }

  /**
   * 创建 field &lt; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return {@link Cond}
   */
  public static Cond lt(String field, Object param) {

    if (null == param) {
      return new Cond();
    }
    return new Cond(String.format(" %s < ?", field), param);
  }

  /**
   * 创建 field &le; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return {@link Cond}
   */
  public static Cond le(String field, Object param) {

    if (null == param) {
      return new Cond();
    }
    return new Cond(String.format(" %s <= ?", field), param);
  }

  /**
   * 创建 field LIKE ? 条件 如果参数中检测不到通配符（%, _）则改为 field = ? 语句
   *
   * @param field 字段
   * @param param 参数
   * @return {@link Cond}
   */
  public static Cond like(String field, Object param) {

    return like(field, param, FuzzyMode.ALL);
  }

  /**
   * 根据 {@link FuzzyMode} 创建 field LIKE ? 条件：<br/>
   * {@link FuzzyMode#ALL} : 参数 {@code param} 中如检测到任意通配符 {@code %} 或 {@code _}，使用模糊查询<br/>
   * {@link FuzzyMode#SINGLE} : 参数 {@code param} 中如检测到任意通配符 {@code _}，使用模糊查询，并把 {@code %} 转义为 {@code ¦%}<br/>
   * {@link FuzzyMode#MULTIPLE} : 参数 {@code param} 中如检测到任意通配符 {@code %}，使用模糊查询，并把 {@code _} 转义为 {@code ¦_}<br/>
   * <br/>
   * 示例：查询参数 param 的值为："_ABC%"<br/>
   * 如果 {@code fuzzyMode = } {@link FuzzyMode#ALL} 返回 {@code field LIKE '_ABC%'} ;<br/>
   * 如果 {@code fuzzyMode = } {@link FuzzyMode#SINGLE} 返回 {@code field LIKE '_ABC¦%' ESCAPE '¦'} ;<br/>
   * 如果 {@code fuzzyMode = } {@link FuzzyMode#MULTIPLE} 返回 {@code field LIKE '¦_ABC%' ESCAPE '¦'} ;<br/>
   *
   * @param field     字段
   * @param param     参数
   * @param fuzzyMode 模糊模式
   * @return {@link Cond}
   */
  public static Cond like(String field, Object param, FuzzyMode fuzzyMode) {
    if (null == param) return new Cond();

    String paramVal = param.toString();

    if (StringUtil.onlyMatchAny(paramVal, FUZZY_SINGLE, FUZZY_MULTIPLE))
      return new Cond();

    String likeClause = String.format(" %s LIKE ?", field);
    if (fuzzyMode == FuzzyMode.SINGLE && StringUtil.contains(paramVal, FUZZY_SINGLE)) {
      return escapeFuzzy(likeClause, paramVal, FUZZY_MULTIPLE);
    }

    if (fuzzyMode == FuzzyMode.MULTIPLE && StringUtil.contains(paramVal, FUZZY_MULTIPLE)) {
      return escapeFuzzy(likeClause, paramVal, FUZZY_SINGLE);
    }

    if (StringUtil.contains(paramVal, FUZZY_MULTIPLE) || StringUtil.contains(paramVal, FUZZY_SINGLE)) {
      return escapeFuzzy(likeClause, paramVal, null);
    }

    return eq(field, param);
  }

  /**
   * 转义模糊查询条件
   *
   * @param sql             原 SQL
   * @param param           参数值
   * @param fuzzySearchChar 需要转义的模糊查询通配符
   * @return {@link Cond}
   */
  private static Cond escapeFuzzy(String sql, String param, Character fuzzySearchChar) {
    if (null == fuzzySearchChar || !StringUtil.contains(param, fuzzySearchChar))
      return new Cond(sql, param);

    String paramVal = StringUtil.replace(param, Character.toString(fuzzySearchChar), "" + FUZZY_ESCAPE + fuzzySearchChar);
    return new Cond(sql, paramVal).escape();
  }

  /**
   * 创建 field BETWEEN ? AND ? 条件 如果参数1 不为 null， 参数2 为 null，则构建 field &ge; ? 条件 如果参数1 为 null， 参数2 不为
   * null，则构建 field &le; ? 条件
   *
   * @param field  字段
   * @param param1 参数1
   * @param param2 参数2
   * @return {@link Cond}
   */
  public static Cond between(String field, Object param1, Object param2) {

    if (null == param1 && null == param2) {
      return new Cond();
    }
    if (null == param2) {
      return ge(field, param1);
    }
    if (null == param1) {
      return le(field, param2);
    }
    return new Cond(String.format(" %s BETWEEN ? AND ?", field), param1, param2);
  }

  /**
   * 构建 field IN (?,?..?) 语句 如果 params 集合为空，则不构建语句 如果 params 集合数量为 1， 则构建语句 field = ? 如果 params
   * 集合数量大于 1， 则构建 field IN (?,?..?) 语句
   *
   * @param field  字段
   * @param params 参数
   * @return {@link Cond}
   */
  public static Cond in(String field, Collection<?> params) {

    return in(field, params, true);
  }

  /**
   * 构建 field IN (?,?..?) 语句 如果 params 集合为空，且 ignoreEmpty 为 true，则不构建语句 如果 params 集合为空，且 ignoreEmpty
   * 为 false，则构建语句 1 &gt; 2 如果 params 集合数量为 1， 则构建语句 field = ? 如果 params 集合数量大于 1， 则构建 field IN
   * (?,?..?) 语句
   *
   * @param field       字段
   * @param params      参数
   * @param ignoreEmpty 是否忽略空参数集合
   * @return {@link Cond}
   */
  public static Cond in(String field, Collection<?> params, boolean ignoreEmpty) {

    Sql          inClauseSql = createInClauseParams(params);
    String       sql         = inClauseSql.sql.toString();
    List<Object> sqlParams   = inClauseSql.params;
    int          paramCnt    = sqlParams.size();

    if (paramCnt == 0) {
      return ignoreEmpty ? new Cond() : new Cond(" 1 > 2");
    }

    if (paramCnt == 1) {
      return eq(field, sqlParams.get(0), false);
    }
    return new Cond(String.format(" %s IN (%s)", field, sql), sqlParams);
  }

  /**
   * 构建 field IN ({sql}) 条件
   *
   * @param field 字段
   * @param sql   SQL
   * @return {@link Cond}
   */
  public static Cond in(String field, Sql sql) {
    if (null == sql) return new Cond();
    return new Cond(String.format(" %s IN (%s)", field, sql.sql), sql.params);
  }

  /**
   * 构建 field NOT IN (?,?..?) 语句 如果 params 集合为空，则不构建语句 如果 params 集合数量为 1， 则构建语句 field = ? 如果 params
   * 集合数量大于 1， 则构建 field NOT IN (?,?..?) 语句
   *
   * @param field  字段
   * @param params 参数
   * @return {@link Cond}
   */
  public static Cond notIn(String field, Collection<?> params) {

    return notIn(field, params, true);
  }

  /**
   * 构建 field NOT IN (?,?..?) 语句 如果 params 集合为空，且 ignoreEmpty 为 true，则不构建语句 如果 params 集合为空，且
   * ignoreEmpty 为 false，则构建语句 1 &gt; 2 如果 params 集合数量为 1， 则构建语句 field != ? 如果 params 集合数量大于 1， 则构建
   * field NOT IN (?,?..?) 语句
   *
   * @param field       字段
   * @param params      参数
   * @param ignoreEmpty 是否忽略空参数集合
   * @return {@link Cond}
   */
  public static Cond notIn(String field, Collection<?> params, boolean ignoreEmpty) {

    Sql          inClauseSql;
    String       sql;
    List<Object> sqlParams;
    int          paramCnt;

    inClauseSql = createInClauseParams(params);
    sql = inClauseSql.sql.toString();
    sqlParams = inClauseSql.params;

    paramCnt = sqlParams.size();

    if (paramCnt == 0) {
      return ignoreEmpty ? new Cond() : new Cond(" 1 > 2");
    }

    if (paramCnt == 1) {
      return ne(field, sqlParams.get(0), false);
    }
    return new Cond(String.format(" %s NOT IN (%s)", field, sql), sqlParams);
  }

  /**
   * 构建 field NOT IN ({sql}) 条件
   *
   * @param field 字段
   * @param sql   SQL
   * @return {@link Cond}
   */
  public static Cond notIn(String field, Sql sql) {
    if (null == sql) return new Cond();
    return new Cond(String.format(" %s NOT IN (%s)", field, sql.sql), sql.params);
  }

  private static Sql createInClauseParams(Collection<?> params) {

    Sql           sql;
    StringBuilder sqlBuilder;
    List<Object>  sqlParams;

    sqlBuilder = new StringBuilder();
    sqlParams = new ArrayList<>();
    if (CollectionUtil.isNotEmpty(params)) {
      for (Object param : params) {
        if (null == param) {
          continue;
        }

        sqlBuilder.append("?,");
        sqlParams.add(param);
      }
      if (!sqlParams.isEmpty()) {
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(","));
      }
    }

    sql = new Sql(sqlBuilder.toString(), sqlParams);
    return sql;
  }

  /**
   * 构建 EXIST ({subSql}) 条件
   *
   * @param subSql SQL
   * @return {@link Cond}
   */
  public static Cond exists(Sql subSql) {
    if (null == subSql) return new Cond();
    return new Cond(String.format(" EXISTS (%s)", subSql.sql), subSql.params.toArray());
  }

  /**
   * 构建 EXIST ({supplier}) 条件
   *
   * @param supplier Supplier
   * @return {@link Cond}
   */
  public static Cond exists(Supplier<Sql> supplier) {

    return exists(supplier.get());
  }

  /**
   * 构建 NOT EXIST ({subSql}) 条件
   *
   * @param subSql SQL
   * @return {@link Cond}
   */
  public static Cond notExists(Sql subSql) {
    if (null == subSql) return new Cond();
    return new Cond(String.format(" NOT EXISTS (%s)", subSql.sql), subSql.params.toArray());
  }

  /**
   * 构建 NOT EXIST ({supplier}) 条件
   *
   * @param supplier Supplier
   * @return {@link Cond}
   */
  public static Cond notExists(Supplier<Sql> supplier) {

    return notExists(supplier.get());
  }

  /**
   * 使用 AND {cond} 拼接多个条件
   *
   * @param cond Cond
   * @return {@link Cond}
   */
  public Cond and(Cond cond) {

    if (StringUtil.isBlank(cond.sql)) {
      return this;
    }
    this.sql.append(String.format(" AND %s", StringUtil.trimStart(cond.sql)));
    this.params.addAll(cond.params);
    return this;
  }

  /**
   * 使用 OR {cond} 拼接多个条件
   *
   * @param cond Cond
   * @return {@link Cond}
   */
  public Cond or(Cond cond) {

    if (StringUtil.isBlank(cond.sql)) {
      return this;
    }
    this.sql.append(String.format(" OR %s", StringUtil.trimStart(cond.sql)));
    this.params.addAll(cond.params);
    return this;
  }

  /**
   * 根据@Condition 注解创建Cond
   *
   * @param object 包含@Condition注解Field的对象
   * @return {@link Cond}
   */
  public static Cond createByCriteria(Object object) {

    return createByCriteria(object, Criterion.Default.class);
  }

  /**
   * 根据@Condition 注解创建Cond
   *
   * @param object        包含@Condition注解Field的对象
   * @param criteriaGroup 条件组名
   * @return {@link Cond}
   */
  public static Cond createByCriteria(Object object, Class<?> criteriaGroup) {

    Cond                   cond              = null;
    List<ConditionMatcher> conditionMatchers = new ArrayList<>();
    final List<Field>      fields            = ReflectUtil.getFields(object.getClass());
    for (Field field : fields) {
      final Criterion[] annotations = field.getAnnotationsByType(Criterion.class);
      for (Criterion annotation : annotations) {
        if (criteriaGroup != annotation.group()) {
          continue;
        }
        ConditionMatcher conditionMatcher = new ConditionMatcher();
        conditionMatcher.fieldName =
            StringUtil.isBlank(annotation.value()) ? field.getName() : annotation.value();
        conditionMatcher.fieldValue = new FieldVisitor(field).getValue(object);
        conditionMatcher.operator = annotation.operator();
        conditionMatcher.order = annotation.order();
        conditionMatchers.add(conditionMatcher);
      }
    }

    conditionMatchers.sort(Comparator.comparingInt(o -> o.order));

    for (ConditionMatcher condMat : conditionMatchers) {
      if (null == cond) {
        cond = createCondByMatcher(condMat);
      } else {
        cond.and(createCondByMatcher(condMat));
      }
    }

    return cond;
  }

  static class ConditionMatcher {

    String   fieldName;
    Object   fieldValue;
    Operator operator;
    int      order;
  }

  private static Cond createCondByMatcher(ConditionMatcher conditionMatcher) {
    Operator operator  = conditionMatcher.operator;
    String   fieldName = conditionMatcher.fieldName;
    Object   paramVal  = conditionMatcher.fieldValue;

    List<String> fieldNames = StringUtil.split(fieldName, ",");
    if (fieldNames.size() > 1) {
      Iterator<String> iterator = fieldNames.iterator();
      Cond             cond     = createCond(operator, iterator.next(), paramVal);
      while (iterator.hasNext()) {
        cond.or(createCond(operator, iterator.next(), paramVal));
      }

      return new Cond(String.format("(%s)", StringUtil.trimStart(cond.sql)), cond.params);
    }

    return createCond(operator, fieldName, paramVal);
  }

  private static Cond createCond(Operator operator, String fieldName, Object paramVal) {
    switch (operator) {
      case EQUALS:
        return Cond.eq(fieldName, paramVal);
      case EQUALS_INC_NULL:
        return Cond.eq(fieldName, paramVal, false);
      case NOT_EQUALS:
        return Cond.ne(fieldName, paramVal);
      case NOT_EQUALS_INC_NULL:
        return Cond.ne(fieldName, paramVal, false);
      case LIKE:
        return Cond.like(fieldName, paramVal);
      case IS_NULL:
        return Cond.isNull(fieldName);
      case IS_NOT_NULL:
        return Cond.isNotNull(fieldName);
      case GREATER_THAN:
        return Cond.gt(fieldName, paramVal);
      case BETWEEN_START:
      case GREATER_THAN_OR_EQUALS:
        return Cond.ge(fieldName, paramVal);
      case LESS_THAN:
        return Cond.lt(fieldName, paramVal);
      case BETWEEN_END:
      case LESS_THAN_OR_EQUALS:
        return Cond.le(fieldName, paramVal);
      case IN:
        return Cond.in(fieldName, ConvertUtil.toCollection(paramVal));
      case IN_INC_EMPTY:
        return Cond.in(fieldName, ConvertUtil.toCollection(paramVal), false);
      case NOT_IN:
        return Cond.notIn(fieldName, ConvertUtil.toCollection(paramVal));
      case NOT_IN_INC_EMPTY:
        return Cond.notIn(fieldName, ConvertUtil.toCollection(paramVal), false);
      default:
        throw new IllegalArgumentException("The operator is not supported");
    }
  }

  @Override
  public String toString() {

    return this.sql.toString();
  }

  /**
   * 设置转义符，用于使用 {@link Cond#like(String, Object, FuzzyMode)} 方法后设置
   *
   * @return {@link Cond}
   */
  private Cond escape() {
    this.sql.append(String.format(" ESCAPE '%s'", FUZZY_ESCAPE));
    return this;
  }
}
