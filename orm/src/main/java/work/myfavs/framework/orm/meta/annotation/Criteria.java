package work.myfavs.framework.orm.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 可重复条件注解的容器，用于在同一字段上聚合多个{@link Criterion}条件.
 *
 * <p>Created by tanqimin on 2015/10/28.
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
