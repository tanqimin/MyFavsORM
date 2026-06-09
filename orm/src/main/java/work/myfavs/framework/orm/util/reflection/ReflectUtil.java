package work.myfavs.framework.orm.util.reflection;

import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具类
 */
public class ReflectUtil {

  /**
   * 类字段列表缓存（key=Class，value=该类的所有字段列表）
   */
  private final static Map<Class<?>, List<Field>> CLASS_CACHE = new ConcurrentHashMap<>();

  /**
   * 按名称查找字段的缓存（key=Class，value=字段名→Field 的映射）
   * 避免 {@link #getField(Class, String)} 反复调用 {@link Class#getDeclaredField(String)}
   */
  private final static Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

  /**
   * 无参构造方法缓存（key=Class，value=Constructor）
   * 避免 {@link #newInstance(Class, Object...)} 反复调用 {@link Class#getDeclaredConstructor(Class[])}
   */
  private final static Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

  /**
   * 获取指定类的所有 {@link Field}，并设置 Accessible 为 {@code true}
   *
   * @param clazz 类型
   * @return 所有
   */
  public static List<Field> getFields(Class<?> clazz) {
    return CLASS_CACHE.computeIfAbsent(clazz, key -> {
      List<Field> fields = new ArrayList<>();
      Class<?> searchClass = key;
      while (searchClass != null) {
        Field[] declaredFields = searchClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
          declaredField.setAccessible(true);
          fields.add(declaredField);
        }
        searchClass = searchClass.getSuperclass();
      }
      return fields;
    });
  }

  /**
   * 获取指定类中指定名称的字段，包括继承的字段.
   * <p>结果会被缓存，首次查找后不会重复调用 {@link Class#getDeclaredField(String)}</p>
   *
   * @param clazz     类型
   * @param fieldName 字段名称
   * @return {@link Field} 对象，未找到时返回 null
   */
  public static Field getField(Class<?> clazz, String fieldName) {
    // 按 clazz 级别缓存整个类的字段名→Field 映射
    Map<String, Field> fieldMap = FIELD_CACHE.computeIfAbsent(clazz, key -> {
      Map<String, Field> map = new HashMap<>();
      Class<?> searchClass = key;
      while (searchClass != null) {
        for (Field declaredField : searchClass.getDeclaredFields()) {
          declaredField.setAccessible(true);
          map.put(declaredField.getName(), declaredField);
        }
        searchClass = searchClass.getSuperclass();
      }
      return map;
    });
    return fieldMap.get(fieldName);
  }

  /**
   * 获取实体对象中指定字段的值.
   *
   * @param field  字段对象
   * @param entity 实体对象
   * @param <T>    字段值的类型
   * @return 字段值
   * @throws InvalidDataAccessException 获取字段值失败时抛出
   */
  @SuppressWarnings("unchecked")
  public static <T> T getFieldValue(Field field, Object entity) {
    try {
      return (T) field.get(entity);
    } catch (Exception e) {
      throw new InvalidDataAccessException(e, "从类型 %s 中获取 %s 字段时发生异常: %s",
                            field.getName(),
                            StringUtil.toStr(entity.getClass()),
                            e.getMessage());
    }
  }

  /**
   * 为实体对象中指定字段设置值.
   *
   * @param field  字段对象
   * @param entity 实体对象
   * @param value  要设置的值
   * @throws InvalidDataAccessException 设置字段值失败时抛出
   */
  public static void setFieldValue(Field field, Object entity, Object value) {
    if (null == value && field.getType().isPrimitive()) {
      return; // 基础类型不能设置null值
    }

    try {
      field.set(entity, value);
    } catch (Exception e) {
      throw new InvalidDataAccessException(e, "从类型 %s 中对 %s 字段赋值为 %s 时发生异常: %s",
                            field.getName(),
                            StringUtil.toStr(entity.getClass()),
                            StringUtil.toStr(value),
                            e.getMessage());
    }
  }

  /**
   * 获取父类泛型参数的实际类型.
   *
   * @param clazz 类型
   * @param <T>   泛型参数的类型
   * @return 泛型参数的实际类型
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> getGenericActualTypeArguments(Class<?> clazz) {
    return (Class<T>)
        ((ParameterizedType) clazz.getGenericSuperclass())
            .getActualTypeArguments()[0];
  }

  /**
   * 获取指定类型的构造方法.
   *
   * @param clazz          类型
   * @param parameterTypes 参数类型列表
   * @return {@link Constructor} 对象
   * @throws InvalidDataAccessException 获取构造方法失败时抛出
   */
  public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
    Objects.requireNonNull(clazz);
    try {
      if (parameterTypes == null || parameterTypes.length == 0) {
        return CONSTRUCTOR_CACHE.computeIfAbsent(clazz, key -> {
          try {
            return key.getDeclaredConstructor();
          } catch (NoSuchMethodException e) {
            throw new InvalidDataAccessException(e, "获取 %s 类型的构造方法时发生异常: %s", key.getName(), e.getMessage());
          }
        });
      }
      return clazz.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException e) {
      throw new InvalidDataAccessException(e, "获取 %s 类型的构造方法时发生异常: %s", clazz.getName(), e.getMessage());
    }
  }

  /**
   * 创建指定类型的实例.
   *
   * @param clazz  类型
   * @param params 构造方法参数
   * @param <T>    类型泛型
   * @return 类型实例
   * @throws InvalidDataAccessException 创建实例失败时抛出
   */
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> clazz, Object... params) {

    try {
      if (null == params)
        return (T) getConstructor(clazz).newInstance();
      return (T) getConstructor(clazz, getClasses(params)).newInstance(params);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new InvalidDataAccessException(e, "创建 %s 类型实例时发生异常: %s", clazz.getName(), e.getMessage());
    }
  }

  /**
   * 获取对象数组中每个对象的 {@link Class}.
   *
   * @param objects 对象数组
   * @return {@link Class} 数组
   */
  public static Class<?>[] getClasses(Object... objects) {
    Class<?>[] classes = new Class<?>[objects.length];
    Object     obj;
    for (int i = 0; i < objects.length; i++) {
      obj = objects[i];
      if (null == obj) {
        classes[i] = Object.class;
      } else {
        classes[i] = obj.getClass();
      }
    }
    return classes;
  }
}
