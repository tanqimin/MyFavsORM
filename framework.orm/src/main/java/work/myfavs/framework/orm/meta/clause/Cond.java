package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.enumeration.FuzzyMode;
import work.myfavs.framework.orm.meta.enumeration.Operator;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.convert.ObjectConvert;

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
    if (params != null && params.length > 0) {
      super.params.addAll(Arrays.asList(params));
    }
  }

  private Cond(String sql, Collection<?> params) {

    super(sql);
    super.params.addAll(params);
  }

  /**
   * 创建逻辑删除条件
   *
   * @param classMeta 类元数据
   * @return Cond
   */
  public static Cond logicalDeleteCond(ClassMeta classMeta) {
    if (classMeta.getLogicDelete() == null) return new Cond();
    return Cond.eq(classMeta.getLogicDelete().getColumnName(), 0);
  }

  /**
   * 创建 = 条件，如 field = param，如果 param 为 null， 则忽略当前条件
   *
   * @param field 字段
   * @param param 参数值
   * @return Cond
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
   * @return Cond
   */
  public static Cond eq(String field, Object param, boolean ignoreNull) {

    if (StrUtil.isBlankIfStr(param)) {
      return ignoreNull ? new Cond() : isNull(field);
    }
    return new Cond(StrUtil.format(" {} = ?", field), param);
  }

  /**
   * 创建 != 条件，如 field != param，如果 param 为 null， 则忽略当前条件
   *
   * @param field 字段
   * @param param 参数值
   * @return Cond
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
   * @return Cond
   */
  public static Cond ne(String field, Object param, boolean ignoreNull) {

    if (StrUtil.isBlankIfStr(param)) {
      return ignoreNull ? new Cond() : isNotNull(field);
    }
    return new Cond(StrUtil.format(" {} <> ?", field), param);
  }

  /**
   * 创建 IS NULL 条件
   *
   * @param field 字段
   * @return Cond
   */
  public static Cond isNull(String field) {

    return new Cond(StrUtil.format(" {} IS NULL", field));
  }

  /**
   * 创建 IS NOT NULL 条件
   *
   * @param field 字段
   * @return Cond
   */
  public static Cond isNotNull(String field) {

    return new Cond(StrUtil.format(" {} IS NOT NULL", field));
  }

  /**
   * 创建 field &gt; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return Cond
   */
  public static Cond gt(String field, Object param) {

    if (StrUtil.isBlankIfStr(param)) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} > ?", field), param);
  }

  /**
   * 创建 field &ge; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return Cond
   */
  public static Cond ge(String field, Object param) {

    if (StrUtil.isBlankIfStr(param)) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} >= ?", field), param);
  }

  /**
   * 创建 field &lt; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return Cond
   */
  public static Cond lt(String field, Object param) {

    if (StrUtil.isBlankIfStr(param)) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} < ?", field), param);
  }

  /**
   * 创建 field &le; ? 条件
   *
   * @param field 字段
   * @param param 参数
   * @return Cond
   */
  public static Cond le(String field, Object param) {

    if (StrUtil.isBlankIfStr(param)) {
      return new Cond();
    }
    return new Cond(StrUtil.format(" {} <= ?", field), param);
  }

  /**
   * 创建 field LIKE ? 条件 如果参数中检测不到通配符（%, _）则改为 field = ? 语句
   *
   * @param field 字段
   * @param param 参数
   * @return Cond
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
   * 如果 {@code fuzzyMode = } {@link FuzzyMode#ALL} 返回 {@code field_name LIKE '_ABC%'} ;<br/>
   * 如果 {@code fuzzyMode = } {@link FuzzyMode#SINGLE} 返回 {@code field_name LIKE '_ABC¦%' ESCAPE '¦'} ;<br/>
   * 如果 {@code fuzzyMode = } {@link FuzzyMode#MULTIPLE} 返回 {@code field_name LIKE '¦_ABC%' ESCAPE '¦'} ;<br/>
   *
   * @param field     字段
   * @param param     参数
   * @param fuzzyMode 模糊模式
   * @return {@link Cond}
   */
  public static Cond like(String field, Object param, FuzzyMode fuzzyMode) {
    if (StrUtil.isBlankIfStr(param)) return new Cond();

    String paramVal = param.toString();
    if (fuzzyMode == FuzzyMode.SINGLE && paramVal.contains(Constant.FUZZY_SINGLE)) {
      return new Cond(
          StrUtil.format(" {} LIKE ?", field),
          StrUtil.replace(paramVal,
                          Constant.FUZZY_MULTIPLE,
                          Constant.FUZZY_ESCAPE.concat(Constant.FUZZY_MULTIPLE)))
          .escape(Constant.FUZZY_ESCAPE);
    }

    if (fuzzyMode == FuzzyMode.MULTIPLE && paramVal.contains(Constant.FUZZY_MULTIPLE)) {
      return new Cond(
          StrUtil.format(" {} LIKE ?", field),
          StrUtil.replace(paramVal,
                          Constant.FUZZY_SINGLE,
                          Constant.FUZZY_ESCAPE.concat(Constant.FUZZY_SINGLE)))
          .escape(Constant.FUZZY_ESCAPE);
    }

    if (paramVal.contains(Constant.FUZZY_MULTIPLE) || paramVal.contains(Constant.FUZZY_SINGLE)) {
      return new Cond(StrUtil.format(" {} LIKE ?", field), param);
    }

    return eq(field, param);
  }

  /**
   * 创建 field BETWEEN ? AND ? 条件 如果参数1 不为 null， 参数2 为 null，则构建 field &ge; ? 条件 如果参数1 为 null， 参数2 不为
   * null，则构建 field &le; ? 条件
   *
   * @param field  字段
   * @param param1 参数1
   * @param param2 参数2
   * @return Cond
   */
  public static Cond between(String field, Object param1, Object param2) {

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

  /**
   * 构建 field IN (?,?..?) 语句 如果 params 集合为空，则不构建语句 如果 params 集合数量为 1， 则构建语句 field = ? 如果 params
   * 集合数量大于 1， 则构建 field IN (?,?..?) 语句
   *
   * @param field  字段
   * @param params 参数
   * @return Cond
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
   * @return Cond
   */
  public static Cond in(String field, Collection<?> params, boolean ignoreEmpty) {

    Sql          inClauseSql = createInClauseParams(params);
    String       sql         = inClauseSql.sql.toString();
    List<Object> sqlParams   = inClauseSql.params;
    int          paramCnt    = sqlParams.size();

    if (paramCnt == 0) {
      return ignoreEmpty ? new Cond() : new Cond(StrUtil.format(" 1 > 2"));
    }

    if (paramCnt == 1) {
      return eq(field, sqlParams.get(0));
    }
    return new Cond(StrUtil.format(" {} IN ({})", field, sql), sqlParams);
  }

  /**
   * 构建 field IN ({sql}) 条件
   *
   * @param field 字段
   * @param sql   SQL
   * @return Cond
   */
  public static Cond in(String field, Sql sql) {
    if (Objects.isNull(sql)) return new Cond();
    return new Cond(StrUtil.format(" {} IN ({})", field, sql.sql), sql.params);
  }

  /**
   * 构建 field NOT IN (?,?..?) 语句 如果 params 集合为空，则不构建语句 如果 params 集合数量为 1， 则构建语句 field = ? 如果 params
   * 集合数量大于 1， 则构建 field NOT IN (?,?..?) 语句
   *
   * @param field  字段
   * @param params 参数
   * @return Cond
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
   * @return Cond
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
      return ignoreEmpty ? new Cond() : new Cond(StrUtil.format(" 1 > 2"));
    }

    if (paramCnt == 1) {
      return ne(field, sqlParams.get(0));
    }
    return new Cond(StrUtil.format(" {} NOT IN ({})", field, sql), sqlParams);
  }

  /**
   * 构建 field NOT IN ({sql}) 条件
   *
   * @param field 字段
   * @param sql   SQL
   * @return Cond
   */
  public static Cond notIn(String field, Sql sql) {
    if (Objects.isNull(sql)) return new Cond();
    return new Cond(StrUtil.format(" {} NOT IN ({})", field, sql.sql), sql.params);
  }

  private static Sql createInClauseParams(Collection<?> params) {

    Sql           sql;
    StringBuilder sqlBuilder;
    List<Object>  sqlParams;

    sqlBuilder = new StringBuilder();
    sqlParams = new ArrayList<>();
    if (CollUtil.isNotEmpty(params)) {
      for (Object param : params) {
        if (StrUtil.isBlankIfStr(param)) {
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
   * @return Cond
   */
  public static Cond exists(Sql subSql) {
    if (Objects.isNull(subSql)) return new Cond();
    return new Cond(StrUtil.format(" EXISTS ({})", subSql.sql), subSql.params.toArray());
  }

  /**
   * 构建 EXIST ({supplier}) 条件
   *
   * @param supplier Supplier
   * @return Cond
   */
  public static Cond exists(Supplier<Sql> supplier) {

    return exists(supplier.get());
  }

  /**
   * 构建 NOT EXIST ({subSql}) 条件
   *
   * @param subSql SQL
   * @return Cond
   */
  public static Cond notExists(Sql subSql) {
    if (Objects.isNull(subSql)) return new Cond();
    return new Cond(StrUtil.format(" NOT EXISTS ({})", subSql.sql), subSql.params.toArray());
  }

  /**
   * 构建 NOT EXIST ({supplier}) 条件
   *
   * @param supplier Supplier
   * @return Cond
   */
  public static Cond notExists(Supplier<Sql> supplier) {

    return notExists(supplier.get());
  }

  /**
   * 使用 AND {cond} 拼接多个条件
   *
   * @param cond Cond
   * @return Cond
   */
  public Cond and(Cond cond) {

    if (StrUtil.isBlankIfStr(cond.sql)) {
      return this;
    }
    this.sql.append(StrUtil.format(" AND {}", StrUtil.trimStart(cond.sql)));
    this.params.addAll(cond.params);
    return this;
  }

  /**
   * 使用 OR {cond} 拼接多个条件
   *
   * @param cond Cond
   * @return Cond
   */
  public Cond or(Cond cond) {

    if (StrUtil.isBlankIfStr(cond.sql)) {
      return this;
    }
    this.sql.append(StrUtil.format(" OR {}", StrUtil.trimStart(cond.sql)));
    this.params.addAll(cond.params);
    return this;
  }

  /**
   * 根据@Condition 注解创建Cond
   *
   * @param object 包含@Condition注解Field的对象
   * @return Cond
   */
  public static Cond createByCriteria(Object object) {

    return createByCriteria(object, Criterion.Default.class);
  }

  /**
   * 根据@Condition 注解创建Cond
   *
   * @param object        包含@Condition注解Field的对象
   * @param criteriaGroup 条件组名
   * @return Cond
   */
  public static Cond createByCriteria(Object object, Class<?> criteriaGroup) {

    Cond                   cond              = null;
    List<ConditionMatcher> conditionMatchers = new ArrayList<>();
    final Field[]          fields            = ReflectUtil.getFields(object.getClass());
    for (Field field : fields) {
      final Criterion[] annotations = field.getAnnotationsByType(Criterion.class);
      for (Criterion annotation : annotations) {
        if (criteriaGroup != annotation.group()) {
          continue;
        }
        ConditionMatcher conditionMatcher = new ConditionMatcher();
        conditionMatcher.fieldName =
            StrUtil.isBlank(annotation.value()) ? field.getName() : annotation.value();
        conditionMatcher.fieldValue = ReflectUtil.getFieldValue(object, field);
        conditionMatcher.operator = annotation.operator();
        conditionMatcher.order = annotation.order();
        conditionMatchers.add(conditionMatcher);
      }
    }

    conditionMatchers.sort(Comparator.comparingInt(o -> o.order));

    for (ConditionMatcher condMat : conditionMatchers) {
      if (cond == null) {
        cond = createCondByOperator(condMat.operator, condMat.fieldName, condMat.fieldValue);
      } else {
        cond.and(createCondByOperator(condMat.operator, condMat.fieldName, condMat.fieldValue));
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

  private static Cond createCondByOperator(Operator operator, String fieldName, Object paramVal) {

    switch (operator) {
      case EQUALS:
        return Cond.eq(fieldName, paramVal);
      case NOT_EQUALS:
        return Cond.ne(fieldName, paramVal);
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
        Collection<?> inParam = ObjectConvert.toCollection(paramVal);
        if (inParam.isEmpty()) return new Cond();
        return Cond.in(fieldName, inParam);
      case NOT_IN:
        Collection<?> notInParam = ObjectConvert.toCollection(paramVal);
        if (notInParam.isEmpty()) return new Cond();
        return Cond.notIn(fieldName, notInParam);
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
   * @param escape 转义符
   * @return {@link Cond}
   */
  private Cond escape(String escape) {
    if (StrUtil.isEmpty(escape)) return this;
    this.sql.append(StrUtil.format(" ESCAPE '{}'", escape));
    return this;
  }
}
