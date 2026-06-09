package work.myfavs.framework.orm.util.reflection;

import java.lang.reflect.Field;

/**
 * Field 访问器
 */
public class FieldVisitor {
  private final Field field;

  /**
   * 构造 FieldVisitor 实例.
   *
   * @param field 字段对象
   */
  public FieldVisitor(Field field) {
    this.field = field;
    this.field.setAccessible(true);
  }

  /**
   * 获取实体对象中该字段的值.
   *
   * @param entity 实体对象
   * @param <T>    字段值的类型
   * @return 字段值
   */
  public <T> T getValue(Object entity) {
    return ReflectUtil.<T>getFieldValue(this.field, entity);
  }

  /**
   * 为实体对象中的该字段设置值.
   *
   * @param entity 实体对象
   * @param value  字段值
   */
  public void setValue(Object entity, Object value) {
    ReflectUtil.setFieldValue(this.field, entity, value);
  }

  /**
   * 获取 {@link Field} 对象.
   *
   * @return {@link Field} 对象
   */
  public Field getField() {
    return this.field;
  }

  /**
   * 获取字段名称.
   *
   * @return 字段名称
   */
  public String getName() {
    return this.field.getName();
  }

  /**
   * 获取字段类型.
   *
   * @return 字段类型
   */
  public Class<?> getType() {
    return this.field.getType();
  }
}
