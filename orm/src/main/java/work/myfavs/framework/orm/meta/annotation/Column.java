package work.myfavs.framework.orm.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 数据库字段映射注解，用于定义实体类属性与数据库表字段之间的对应关系。
 * <p>标记了本注解的字段才会被 ORM 框架识别为持久化属性，参与 CRUD 操作。
 * 未标记的字段将被忽略。</p>
 * <p><b>约定：</b></p>
 * <ul>
 *   <li>若 {@link #value()} 为空，将字段名转换为下划线风格作为列名（如 {@code userName} → {@code user_name}）；</li>
 *   <li>若 {@link #readonly()} 为 {@code true}，该字段不会出现在 INSERT 和 UPDATE 语句中；</li>
 *   <li>视图映射的实体，非主键字段需设置 {@code readonly = true}。</li>
 * </ul>
 *
 * @see Table
 * @see PrimaryKey
 * @see LogicDelete
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
   * 是否只读？ 视图映射的实体，字段需设置该属性为 {@code false}
   *
   * @return 如果只读返回 {@code true}
   */
  boolean readonly() default false;
}
