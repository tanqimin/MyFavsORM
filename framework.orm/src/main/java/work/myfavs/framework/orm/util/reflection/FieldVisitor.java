package work.myfavs.framework.orm.util.reflection;

import java.lang.reflect.Field;

/**
 * Field 访问器
 */
public class FieldVisitor {
  private final Field field;

  public FieldVisitor(Field field) {
    this.field = field;
    this.field.setAccessible(true);
  }

  public <T> T getValue(Object entity) {
    return ReflectUtil.getFieldValue(this.field, entity);
  }

  public void setValue(Object entity, Object value) {
    ReflectUtil.setFieldValue(this.field, entity, value);
  }

  public Field getField() {
    return this.field;
  }

  public String getName() {
    return this.field.getName();
  }

  public Class<?> getType() {
    return this.field.getType();
  }
}
