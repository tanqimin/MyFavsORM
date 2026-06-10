package work.myfavs.framework.orm.meta.annotation;

import work.myfavs.framework.orm.meta.enumeration.Operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;

/**
 * 查询条件注解，用于声明式构建动态查询条件。
 * <p>标注在实体的字段上，配合 {@link work.myfavs.framework.orm.meta.clause.Cond#criteria(Object)}
 * 或 {@link work.myfavs.framework.orm.meta.clause.Cond#criteria(Object, Class)} 方法，
 * 可根据字段值自动生成 SQL 条件子句。</p>
 * <p>支持 {@code @Repeatable}，同一字段可定义多个不同运算符的条件。</p>
 *
 * @see Criteria
 * @see work.myfavs.framework.orm.meta.clause.Cond#criteria(Object)
 * @see work.myfavs.framework.orm.meta.enumeration.Operator
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@Repeatable(value = Criteria.class)
public @interface Criterion {
  interface Default {}

  /**
   * 数据库条件参数名称
   *
   * @return 数据库条件参数名称
   */
  String value() default "";

  /**
   * 条件运算符
   *
   * @return 条件运算符，默认为EQUALS
   */
  Operator operator() default Operator.EQUALS;

  /**
   * 条件顺序
   *
   * @return 条件顺序
   */
  int order() default 1;

  /**
   * 组名，默认DEFAULT，用于区分不同场景的条件
   *
   * @return 组名
   */
  Class<?> group() default Default.class;
}
