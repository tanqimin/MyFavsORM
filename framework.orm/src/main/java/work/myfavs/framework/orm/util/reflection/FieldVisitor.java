package work.myfavs.framework.orm.util.reflection;

import cn.hutool.core.convert.Convert;
import work.myfavs.framework.orm.util.exception.DBException;

import java.lang.reflect.Field;

/**
 * Field 访问器
 */
public class FieldVisitor {
  private Field field;

  public FieldVisitor(Field field) {
    this.field = field;
    this.field.setAccessible(true);
  }

  public Object getValue(Object obj) {
    try {
      return this.field.get(obj);
    } catch (IllegalAccessException e) {
      throw new DBException(e, "could not get field {} on class {}", this.field.getName(), obj.getClass().toString());
    }
  }

  public void setValue(Object entity, Object value) {
    if (value == null && this.field.getType().isPrimitive()) {
      return; // dont try set null to a primitive field
    }

    try {
      this.field.set(entity, Convert.convert(getType(), value));
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
