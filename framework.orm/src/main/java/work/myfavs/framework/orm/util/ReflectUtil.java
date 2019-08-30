package work.myfavs.framework.orm.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.exception.UnexpectedNewInstanceException;

@SuppressWarnings("unchecked")
public class ReflectUtil {

  private final static Map<Class<?>, List<Field>>                    FIELDS_CACHE = new ConcurrentHashMap<>();
  private final static Map<Class<? extends Enum>, Map<String, Enum>> ENUM_CACHE   = new ConcurrentHashMap<>();

  /**
   * 获取指定类型的泛型参数
   *
   * @param clazz 包含泛型的类型
   * @param index 泛型参数类型index，从0开始
   * @param <T>   泛型参数类型
   *
   * @return 泛型参数类型类
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> getActualClassArg(Class<?> clazz, int index) {

    return (Class<T>) getActualTypeArg(clazz, index);
  }

  /**
   * 获取指定类型的泛型参数
   *
   * @param clazz 包含泛型的类型
   * @param index 泛型参数类型index，从0开始
   *
   * @return 泛型参数类型类
   */
  public static Type getActualTypeArg(Class<?> clazz, int index) {

    return ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[index];
  }

  /**
   * 把字符串转换为枚举
   *
   * @param enumClass 枚举类
   * @param name      枚举值的名字. `name` 不区分大小写
   * @param <T>       枚举类型
   *
   * @return 返回对应的枚举值，如果枚举值不存在，则返回null
   */
  public static <T extends Enum<T>> T asEnum(final Class<T> enumClass, final String name) {

    return asEnum(enumClass, name, false);
  }

  /**
   * 把字符串转换为枚举
   *
   * @param enumClass     枚举类
   * @param name          枚举值的名字. `name` 不区分大小写
   * @param caseSensitive 是否区分大小写
   * @param <T>           枚举类型
   *
   * @return 返回对应的枚举值，如果枚举值不存在，则返回null
   */
  public static <T extends Enum<T>> T asEnum(final Class<T> enumClass, final String name, final boolean caseSensitive) {

    if (name == null || name.length() == 0) {
      return null;
    }
    Map<String, Enum> map = ENUM_CACHE.get(enumClass);
    if (null == map) {
      T[] values = enumClass.getEnumConstants();
      map = new HashMap<>(values.length * 2);
      for (T value : values) {
        map.put(value.name().toUpperCase(), value);
      }
      ENUM_CACHE.putIfAbsent(enumClass, map);
    }
    String key    = name.toUpperCase();
    T      retVal = (T) map.get(key);
    return caseSensitive && !retVal.name().equals(name)
        ? null
        : retVal;
  }

  /**
   * 使用反射创建实例
   *
   * @param clazz 类型
   * @param <T>   类型泛型
   *
   * @return 实例对象
   */
  public static <T> T newInstance(Class<T> clazz) {

    try {
      Constructor<T> ct = clazz.getDeclaredConstructor();
      ct.setAccessible(true);
      return ct.newInstance();
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof RuntimeException) {
        throw ((RuntimeException) t);
      } else {
        throw new UnexpectedNewInstanceException(e);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnexpectedNewInstanceException(e);
    }
  }

  /**
   * 获取字段的值
   *
   * @param obj       对象
   * @param fieldName 字段名
   *
   * @return 字段值
   */
  public static Object getFieldValue(Object obj, String fieldName) {

    if (obj == null || fieldName == null) {
      return null;
    }
    Field field = fieldOf(obj.getClass(), fieldName);

    if (field == null) {
      return null;
    }
    field.setAccessible(true);
    Object result = null;
    try {
      result = field.get(obj);
    } catch (IllegalAccessException e) {
      throw new DBException(e);
    }
    return result;
  }

  /**
   * 设置字段值
   *
   * @param obj       对象
   * @param fieldName 字段名
   * @param val       字段值
   */
  public static void setFieldValue(Object obj, String fieldName, Object val) {

    ValidUtil.notNull(obj);
    ValidUtil.notEmpty(fieldName);

    Field field = fieldOf(obj.getClass(), fieldName);

    ValidUtil.notNull(field);

    field.setAccessible(true);
    try {
      field.set(obj, val);
    } catch (IllegalAccessException e) {
      throw new DBException(e);
    }
  }

  public static Field fieldOf(Class<?> clazz, String fieldName) {

    final List<Field> fields = fieldsOf(clazz);
    if (fields != null && fields.size() > 0) {
      for (Field field : fields) {
        if (fieldName.equals(field.getName())) {
          return field;
        }
      }
    }
    return null;
  }

  /**
   * 获得一个类中所有字段列表，包括其父类中的字段
   *
   * @param clazz 类
   *
   * @return 字段列表
   *
   * @throws SecurityException 安全检查异常
   */
  public static List<Field> fieldsOf(Class<?> clazz)
      throws SecurityException {

    List<Field> allFields = FIELDS_CACHE.get(clazz);
    if (null != allFields) {
      return allFields;
    }

    allFields = new ArrayList<>();
    addFieldsToList(allFields, clazz, Object.class, null);
    FIELDS_CACHE.put(clazz, allFields);
    return allFields;
  }

  /**
   * 返回类（包含所有父类）中的所有包含指定Annotation类的字段，注意，由于使用了 {@link Field#setAccessible(boolean)} 所以会返回所有字段
   *
   * @param clazz           Class
   * @param annotationClass Annotation Class
   *
   * @return Field 集合
   */
  public static List<Field> fieldsOf(Class<?> clazz, Class<? extends Annotation> annotationClass) {

    return fieldsOf(clazz, field -> field.getAnnotation(annotationClass) != null);
  }

  /**
   * 返回指定类得所有Field，包括所有父级Field，注意，所有返回的字段都会调用
   * {@link Field#setAccessible(boolean)} 设置为 `true`
   *
   * @param clazz  Class
   * @param filter 筛选条件，如果结果不为'null'值，则返回该字段
   *
   * @return a list of fields
   */
  public static List<Field> fieldsOf(Class<?> clazz, Function<Field, Boolean> filter) {

    return fieldsOf(clazz, Object.class, filter);
  }

  /**
   * 返回指定类得所有Field，包括所有父级Field，直至父级为rootClass为止，注意，所有返回的字段都会调用
   * {@link Field#setAccessible(boolean)} 设置为 `true`
   *
   * @param clazz     the class
   * @param rootClass 根字段，到此字段后停止继续向上级查找
   * @param filter    筛选条件，如果结果不为'null'值，则返回该字段
   *
   * @return the list of fields
   */
  public static List<Field> fieldsOf(Class<?> clazz, Class<?> rootClass, Function<Field, Boolean> filter) {

    List<Field> fields = new ArrayList<Field>();
    addFieldsToList(fields, clazz, rootClass, filter);
    return fields;
  }


  /**
   * 返回指定类得所有Field，包括所有父级Field，直至父级为rootClass为止
   *
   * @param clazz       the class
   * @param classFilter 类筛选器
   * @param fieldFilter 筛选条件，如果结果不为'null'值，则返回该字段
   *
   * @return
   */
  public static List<Field> fieldsOf(Class<?> clazz, Function<Class<?>, Boolean> classFilter, Function<Field, Boolean> fieldFilter) {

    List<Field> fields = new ArrayList<Field>();
    addFieldsToList(fields, clazz, classFilter, fieldFilter);
    return fields;
  }

  private static void addFieldsToList(List<Field> list, Class<?> clazz, Class<?> rootClass, Function<Field, Boolean> filter) {

    if (clazz.isInterface()) {
      return;
    }
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (null != filter && !filter.apply(field)) {
        continue;
      }
      field.setAccessible(true);
      list.add(field);
    }
    if (clazz != rootClass) {
      clazz = clazz.getSuperclass();
      if (null != clazz) {
        addFieldsToList(list, clazz, rootClass, filter);
      }
    }
  }

  private static void addFieldsToList(List<Field> list, Class<?> clazz, Function<Class<?>, Boolean> classFilter,
                                      Function<Field, Boolean> fieldFilter) {

    if (clazz.isInterface()) {
      return;
    }
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (null != fieldFilter && !fieldFilter.apply(field)) {
        continue;
      }
      field.setAccessible(true);
      list.add(field);
    }
    if (null != classFilter) {
      clazz = clazz.getSuperclass();
      if (null != clazz && classFilter.apply(clazz)) {
        addFieldsToList(list, clazz, classFilter, fieldFilter);
      }
    }
  }

}
