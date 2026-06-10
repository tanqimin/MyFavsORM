package work.myfavs.framework.orm.meta.annotation;

import work.myfavs.framework.orm.meta.enumeration.GenerationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 实体类与数据表的映射注解。
 * <p>标记在类上，将实体类映射到指定数据表。</p>
 * <p><b>注意：</b>{@code @Table} 没有 {@code @Inherited} 元注解，子类必须独立标注 {@code @Table} 才能被识别为实体。</p>
 *
 * @see Column
 * @see PrimaryKey
 * @see GenerationType
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
   *
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
}
