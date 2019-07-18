package work.myfavs.framework.orm.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 数据库字段注释，用于定义实体类中的属性与数据库字段之间的匹配
 * <p>
 * Created by tanqimin on 2015/10/28.
 */
@java.lang.annotation.Inherited
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface Column {

  /**
   * 数据库字段
   *
   * @return 数据库字段
   */
  String value() default "";

  /**
   * 是否只读？
   * 视图映射的实体，字段需设置该属性为 {@code false}
   *
   * @return 如果只读返回 {@code true}
   */
  boolean readonly() default false;

}
