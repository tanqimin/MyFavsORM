package work.myfavs.framework.orm.util.reflection;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 反射工具类
 */
public class ReflectUtil {

  private final static Map<Class<?>, List<Field>>       CLASS_CACHE       = new WeakHashMap<>();
  @SuppressWarnings("rawtypes")
  private final static Map<Class<?>, ConstructorAccess> CONSTRUCTOR_CACHE = new WeakHashMap<>();

  /**
   * 获取指定类的所有 {@link Field}，并设置 Accessible 为 {@code true}
   *
   * @param clazz 类型
   * @return 所有
   */
  public static List<Field> getFields(Class<?> clazz) {
    List<Field> fields = CLASS_CACHE.get(clazz);
    if (null != fields) return fields;

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
      throw new DBException(e, "从类型 %s 中获取 %s 字段时发生异常: %s",
                            field.getName(),
                            StringUtil.toStr(entity.getClass()),
                            e.getMessage());
    }
  }

  public static void setFieldValue(Field field, Object entity, Object value) {
    if (null == value && field.getType().isPrimitive()) {
      return; // 基础类型不能设置null值
    }

    try {
      field.set(entity, value);
    } catch (IllegalAccessException e) {
      throw new DBException(e, "从类型 %s 中对 %s 字段赋值为 %s 时发生异常: %s",
                            field.getName(),
                            StringUtil.toStr(entity.getClass()),
                            StringUtil.toStr(value),
                            e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> clazz) {

    ConstructorAccess<T> constructorAccess = CONSTRUCTOR_CACHE.get(clazz);
    if (null == constructorAccess) {
      constructorAccess = ConstructorAccess.get(clazz);
      CONSTRUCTOR_CACHE.put(clazz, constructorAccess);
    }

    return constructorAccess.newInstance();
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getGenericActualTypeArguments(Class<?> clazz) {
    return (Class<T>)
        ((ParameterizedType) clazz.getGenericSuperclass())
            .getActualTypeArguments()[0];
  }

//  public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
//    Objects.requireNonNull(clazz);
//    try {
//      return clazz.getDeclaredConstructor(parameterTypes);
//    } catch (NoSuchMethodException e) {
//      throw new DBException(e, "获取 %s 类型的构造方法时发生异常: %s", clazz.getName(), e.getMessage());
//    }
//  }
//
//  @SuppressWarnings("unchecked")
//  public static <T> T newInstance(Class<T> clazz, Object... params) {
//
//    try {
//      if (null == params)
//        return (T) getConstructor(clazz).newInstance();
//      return (T) getConstructor(clazz, getClasses(params)).newInstance(params);
//    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//      throw new DBException(e, "创建 %s 类型实例时发生异常: %s", clazz.getName(), e.getMessage());
//    }
//  }
//
//  public static Class<?>[] getClasses(Object... objects) {
//    Class<?>[] classes = new Class<?>[objects.length];
//    Object     obj;
//    for (int i = 0; i < objects.length; i++) {
//      obj = objects[i];
//      if (null == obj) {
//        classes[i] = Object.class;
//      } else {
//        classes[i] = obj.getClass();
//      }
//    }
//    return classes;
//  }
}
