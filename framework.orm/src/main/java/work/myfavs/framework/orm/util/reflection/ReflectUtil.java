package work.myfavs.framework.orm.util.reflection;

import work.myfavs.framework.orm.util.exception.DBException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ReflectUtil {
  private final static Map<Class<?>, List<Field>> CLASS_CACHE = new WeakHashMap<>();

  public static List<Field> getFields(Class<?> clazz) {
    List<Field> fields = CLASS_CACHE.get(clazz);
    if (Objects.nonNull(fields)) return fields;

    fields = new ArrayList<>();
    Class<?> searchClass = clazz;
    while (searchClass != null) {
      Field[] declaredFields = searchClass.getDeclaredFields();
      for (Field declaredField : declaredFields) {
        declaredField.setAccessible(true);
        fields.add(declaredField);
      }
      searchClass = searchClass.getSuperclass();
    }
    CLASS_CACHE.put(clazz, fields);
    return fields;
  }

  public static Field getField(Class<?> clazz, String fieldName) {
    while (clazz != null) {
      try {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getFieldValue(Field field, Object entity) {
    try {
      return (T) field.get(entity);
    } catch (IllegalAccessException e) {
      throw new DBException(e, "could not get field %s on class %s", field.getName(), entity.getClass().toString());
    }
  }

  public static void setFieldValue(Field field, Object entity, Object value) {
    if (Objects.isNull(value) && field.getType().isPrimitive()) {
      return; // dont try set null to a primitive field
    }

    try {
      field.set(entity, value);
    } catch (IllegalAccessException e) {
      throw new DBException(e, "could not set field %s on class %s", field.getName(), entity.getClass().toString());
    }
  }

  public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
    try {
      return clazz.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException e) {
      throw new DBException("Error get constructor: %s", clazz.getName());
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> clazz) {
    try {
      return (T) getConstructor(clazz).newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new DBException("Error create instance for class: %s", clazz.getName());
    }
  }
}