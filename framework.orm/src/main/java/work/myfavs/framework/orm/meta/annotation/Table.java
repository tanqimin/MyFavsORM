package work.myfavs.framework.orm.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

/**
 * 用于标记实体名称与数据表名称之间的映射关系
 * <p>
 * Created by tanqimin on 2015/10/28.
 */
@java.lang.annotation.Target({ElementType.TYPE})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface Table {

  /**
   * 数据表名称，默认为""，即与实体名称一致
   *
   * @return 数据表名称
   */
  String value() default "";

  /**
   * 主键生成策略
   * <pre>
   * UUID : 值由系统字段生成;
   * IDENTITY : 数据库自增，值由数据库生成;
   * ASSIGNED : 自然主键，值由用户自定义;
   * COMPOSITE : 联合主键，值由用户自定义;
   * </pre>
   *
   * @return 主键生成策略
   */
  GenerationType strategy() default GenerationType.SNOW_FLAKE;

  /**
   * 逻辑删除字段
   *
   * @return 如果启用，返回true，否则返回false
   */
  String logicalDeleteField() default "";

}
