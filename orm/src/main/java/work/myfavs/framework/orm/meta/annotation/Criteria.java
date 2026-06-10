package work.myfavs.framework.orm.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 可重复条件注解的容器，用于在同一字段上聚合多个 {@link Criterion} 条件。
 * <p>当需要在同一个字段上定义多个不同运算符的条件时，使用本注解包裹多个 {@code @Criterion}。</p>
 * <p>通常配合 {@link work.myfavs.framework.orm.meta.clause.Cond#criteria(Object)} 使用。</p>
 *
 * @see Criterion
 * @see work.myfavs.framework.orm.meta.clause.Cond#criteria(Object)
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface Criteria {

  /**
   * 条件数组.
   *
   * @return {@link Criterion} 数组
   */
  Criterion[] value();
}
