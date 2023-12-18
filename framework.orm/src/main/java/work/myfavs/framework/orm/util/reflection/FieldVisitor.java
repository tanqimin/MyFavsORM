package work.myfavs.framework.orm.util.reflection;

import work.myfavs.framework.orm.util.exception.DBException;

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

  public Object getValue(Object entity) {
    try {
      return this.field.get(entity);
    } catch (IllegalAccessException e) {
      throw new DBException(e, "could not get field {} on class {}", this.field.getName(), entity.getClass().toString());
    }
  }

  public void setValue(Object entity, Object value) {
    if (value == null && this.field.getType().isPrimitive()) {
      return; // dont try set null to a primitive field
    }

    try {
      this.field.set(entity, value);
    } catch (IllegalAccessException e) {
      throw new DBException(e, "could not set field {} on class {}", this.field.getName(), entity.getClass().toString());
    }
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