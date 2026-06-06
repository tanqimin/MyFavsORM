package work.myfavs.framework.orm.meta.clause;


import lombok.NonNull;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.enumeration.FuzzyMode;
import work.myfavs.framework.orm.meta.enumeration.Operator;
import work.myfavs.framework.orm.meta.schema.Attribute;
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

  private Cond() {}

  private Cond(@NonNull CharSequence sql) {
    super(sql);
  }

  private Cond(@NonNull CharSequence sql, Object param) {
    super(sql, param);
  }

  private Cond(@NonNull CharSequence sql, Collection<?> params) {
    super(sql, params);
  }

  private static Cond create() {
    return new Cond();
  }

  private static Cond create(CharSequence sql) {
    return new Cond(sql);
  }

  protected Cond append(CharSequence sql) {
    super.concatWithSpace(sql);
    return this;
  }

  protected Cond append(CharSequence sql, Object param) {
    super.concatWithSpace(sql).param(param);
    return this;
  }

  protected Cond append(CharSequence sql, Collection<?> params) {
    super.concatWithSpace(sql).params(params);
    return this;
  }

  /**
   * 拼接 {@link Cond}
   *
   * @param cond {@link Cond}
   * @return {@link Cond}
   */
  protected Cond append(Cond cond) {
    return this.append(cond.sql, cond.params);
  }

  /**
   * 拼接 {@link Clause} 子查询
   *
   * @param sql {@link Clause} 子查询
   * @return {@link Cond}
   */
  private Cond appendSubQuery(Clause sql) {
    if (super.notBlank())
      super.concatWithSpace(String.format("( %s )", StringUtil.trim(sql.sql)))
           .params(sql.params);
    return this;
  }

  /**
   * 创建逻辑删除条件<br/>
   * 如果 logicDelete 不为空，则创建逻辑删除条件
   *
   * @param logicDelete {@link Attribute} 逻辑删除标记字段
   * @return {@link Cond}
   */
  public static Cond logicalDelete(Attribute logicDelete) {

    if (null == logicDelete) return create();
    if (!logicDelete.isLogicDelete()) return create();
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
      return ignoreNull ? create() : isNull(field);
    }
    Cond cond = create(field).append("= ?", param);

    if (param instanceof String && StringUtil.length(param) == 0) {
      return ignoreNull ? create() : cond;
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
      return ignoreNull ? create() : isNotNull(field);
    }
    return create(field).append("<> ?", param);
  }

  /**
   * 创建 IS NULL 条件
   *
   * @param field 字段
   * @return {@link Cond}
   */
  public static Cond isNull(String field) {
    return create(field).append("IS NULL");
  }

  /**
   * 创建 IS NOT NULL 条件
   *
   * @param field 字段
   * @return {@link Cond}
   */
  public static Cond isNotNull(String field) {
    return create(field).append("IS NOT NULL");
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
      return create();
    }
    return create(field).append("> ?", param);
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
      return create();
    }
    return create(field).append(">= ?", param);
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
      return create();
    }
    return create(field).append("< ?", param);
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
      return create();
    }
    return create(field).append("<= ?", param);
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
    if (null == param) return create();

    String paramVal = param.toString();

    if (StringUtil.onlyMatchAny(paramVal, FUZZY_SINGLE, FUZZY_MULTIPLE))
      return create();

    String likeClause = String.format("%s LIKE ?", field);
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
      return create();
    }
    if (null == param2) {
      return ge(field, param1);
    }
    if (null == param1) {
      return le(field, param2);
    }
    return create(field).append("BETWEEN ?", param1).append("AND ?", param2);
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
    List<Object> sqlParams   = inClauseSql.params;
    int          paramCnt    = sqlParams.size();

    if (paramCnt == 0) {
      return ignoreEmpty ? create() : create("1 > 2");
    }

    if (paramCnt == 1) {
      return eq(field, sqlParams.get(0), false);
    }
    return create(field).append("IN").appendSubQuery(inClauseSql);
  }

  /**
   * 构建 field IN ({sql}) 条件
   *
   * @param field 字段
   * @param sql   SQL
   * @return {@link Cond}
   */
  public static Cond in(String field, Sql sql) {
    if (null == sql) return create();
    return create(field).append("IN").appendSubQuery(sql);
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
    List<Object> sqlParams;
    int          paramCnt;

    inClauseSql = createInClauseParams(params);
    sqlParams = inClauseSql.params;

    paramCnt = sqlParams.size();

    if (paramCnt == 0) {
      return ignoreEmpty ? create() : create("1 > 2");
    }

    if (paramCnt == 1) {
      return ne(field, sqlParams.get(0), false);
    }
    return create(field).append("NOT IN").appendSubQuery(inClauseSql);
  }

  /**
   * 构建 field NOT IN ({sql}) 条件
   *
   * @param field 字段
   * @param sql   SQL
   * @return {@link Cond}
   */
  public static Cond notIn(String field, Sql sql) {
    if (null == sql) return create();
    return create(field).append("NOT IN").appendSubQuery(sql);
  }

  private static Sql createInClauseParams(Collection<?> params) {
    Sql sql = new Sql();
    if (CollectionUtil.isEmpty(params))
      return sql;

    Iterator<?> iterator = params.iterator();
    if (iterator.hasNext()) {
      sql.append("?", iterator.next());
      while (iterator.hasNext()) {
        sql.append(", ?", iterator.next());
      }
    }
    return sql;
  }

  /**
   * 构建 EXIST ({subSql}) 条件
   *
   * @param subSql SQL
   * @return {@link Cond}
   */
  public static Cond exists(Sql subSql) {
    if (null == subSql) return create();
    return create("EXISTS").appendSubQuery(subSql);
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
    if (null == subSql) return create();
    return create("NOT EXISTS").appendSubQuery(subSql);
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
    if (cond.isBlank())
      return this;

    return this.append("AND").append(cond);
  }

  /**
   * 使用 AND ({cond}) 拼接条件
   *
   * @param supplier {@link Supplier}{@code <Cond> }
   * @return {@link Cond}
   */
  public Cond and(Supplier<Cond> supplier) {
    Cond cond = supplier.get();
    if (cond.isBlank())
      return this;
    return this.append("AND").appendSubQuery(cond);
  }

  /**
   * 使用 OR {cond} 拼接多个条件
   *
   * @param cond Cond
   * @return {@link Cond}
   */
  public Cond or(Cond cond) {
    if (cond.isBlank())
      return this;
    return this.append("OR").append(cond);
  }

  /**
   * 使用 OR ({cond}) 拼接条件
   *
   * @param supplier {@link Supplier}{@code <Cond> }
   * @return {@link Cond}
   */
  public Cond or(Supplier<Cond> supplier) {
    Cond cond = supplier.get();
    if (cond.isBlank())
      return this;
    return this.append("OR").appendSubQuery(cond);
  }

  /**
   * 根据@Condition 注解创建Cond
   *
   * @param object 包含@Condition注解Field的对象
   * @return {@link Cond}
   */
  public static Cond criteria(Object object) {

    return criteria(object, Criterion.Default.class);
  }

  /**
   * 根据@Condition 注解创建Cond
   *
   * @param object        包含@Condition注解Field的对象
   * @param criteriaGroup 条件组名
   * @return {@link Cond}
   */
  public static Cond criteria(Object object, Class<?> criteriaGroup) {

    Cond                   cond              = create();
    List<ConditionMatcher> conditionMatchers = new ArrayList<>();
    final List<Field>      fields            = ReflectUtil.getFields(object.getClass());
    for (Field field : fields) {
      final Criterion[] annotations = field.getAnnotationsByType(Criterion.class);
      for (Criterion annotation : annotations) {
        if (criteriaGroup != annotation.group())
          continue;

        ConditionMatcher conditionMatcher = new ConditionMatcher();
        conditionMatcher.fieldValue = new FieldVisitor(field).getValue(object);
        conditionMatcher.operator = annotation.operator();
        if (conditionMatcher.ignoreCondition())
          continue;

        conditionMatcher.fieldName = StringUtil.isBlank(annotation.value()) ? field.getName() : annotation.value();
        conditionMatcher.order = annotation.order();
        conditionMatchers.add(conditionMatcher);
      }
    }

    if (CollectionUtil.isEmpty(conditionMatchers))
      return cond;

    //排序
    conditionMatchers.sort(Comparator.comparingInt(o -> o.order));

    //生成条件
    Iterator<ConditionMatcher> iterator = conditionMatchers.iterator();
    if (iterator.hasNext()) {
      cond.append(createCondByMatcher(iterator.next()));
      while (iterator.hasNext()) {
        cond.and(createCondByMatcher(iterator.next()));
      }
    }

    return cond;
  }

  static class ConditionMatcher {

    String   fieldName;
    Object   fieldValue;
    Operator operator;
    int      order;

    public boolean ignoreCondition() {
      if (null != fieldValue) return false;
      switch (operator) {
        case EQUALS_INC_NULL:
        case IN_INC_EMPTY:
        case NOT_EQUALS_INC_NULL:
        case NOT_IN_INC_EMPTY:
          return false;
      }
      return true;
    }
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
    return this.append("ESCAPE '" + FUZZY_ESCAPE + "'");
  }
}
