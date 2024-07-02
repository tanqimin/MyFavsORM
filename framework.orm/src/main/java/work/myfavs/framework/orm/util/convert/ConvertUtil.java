package work.myfavs.framework.orm.util.convert;


import work.myfavs.framework.orm.util.common.ArrayUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * 转换工具类
 */
public class ConvertUtil {
  /**
   * 把对象转换成集合
   *
   * @param object 对象
   * @return 集合
   */
  public static Collection<?> toCollection(Object object) {
    Collection<Object> collection = new ArrayList<>();
    if (null == object) return collection;

    if (ArrayUtil.isArray(object)) {
      Class<?> componentType = object.getClass().getComponentType();
      if (componentType.isPrimitive()) {
        int size = Array.getLength(object);
        for (int i = 0; i < size; i++) {
          collection.add(Array.get(object, i));
        }
        return collection;
      } else {
        return List.of((Object[]) object);
      }
    } else if (object instanceof Collection<?>) {
      return (Collection<?>) object;
    }

    throw new DBException("不能把类型 %s 转换为 Collection. ", object.getClass().getName());
  }

  /**
   * 把对象转换为数字类型
   *
   * @param clazz      目标数字类型的类
   * @param value      转换的对象
   * @param numberFunc Number转换到目标数字类型的方法
   * @param stringFunc String转换到目标数字类型的方法
   * @param <T>        目标数字类型泛型
   * @return 目标数字类型
   */
  public static <T> T toNumber(Class<T> clazz,
                               Object value,
                               Function<Number, T> numberFunc,
                               Function<String, T> stringFunc) {
    if (null == value)
      return clazz.isPrimitive() ? numberFunc.apply(0) : null;

    if (value instanceof Number)
      return numberFunc.apply((Number) value);

    if (value instanceof String) {
      String str = ((String) value).trim();
      str = str.isEmpty() ? null : str;
      if (null == str)
        return clazz.isPrimitive() ? numberFunc.apply(0) : null;
      return stringFunc.apply(str);
    }
    throw new DBException("不能把类型 %s 转换为 %s. ", value.getClass().getName(), clazz.getName());
  }

  /**
   * 对象转换为 Integer
   *
   * @param value 对象
   * @return integer值
   */
  public static Integer toInt(Object value) {
    return toNumber(Integer.class, value, Number::intValue, Integer::parseInt);
  }

  /**
   * 对象转换为 Short
   *
   * @param value 对象
   * @return short值
   */
  public static Short toShort(Object value) {
    return toNumber(Short.class, value, Number::shortValue, Short::parseShort);
  }

  /**
   * 对象转换为 Long
   *
   * @param value 对象
   * @return long值
   */
  public static Long toLong(Object value) {
    return toNumber(Long.class, value, Number::longValue, Long::parseLong);
  }

  /**
   * 对象转换为 Float
   *
   * @param value 对象
   * @return float值
   */
  public static Float toFloat(Object value) {
    return toNumber(Float.class, value, Number::floatValue, Float::parseFloat);
  }

  /**
   * 对象转换为 Double
   *
   * @param value 对象
   * @return double值
   */
  public static Double toDouble(Object value) {
    return toNumber(Double.class, value, Number::doubleValue, Double::parseDouble);
  }

  /**
   * 对象转换为 Byte
   *
   * @param value 对象
   * @return Byte值
   */
  public static Byte toByte(Object value) {
    return toNumber(Byte.class, value, Number::byteValue, Byte::parseByte);
  }

  /**
   * 对象转换为 BigDecimal
   *
   * @param value 对象
   * @return BigDecimal值
   */
  public static BigDecimal toBigDecimal(Object value) {
    return toNumber(BigDecimal.class, value,
                    num -> num instanceof BigDecimal ? (BigDecimal) num : BigDecimal.valueOf(num.doubleValue()),
                    str -> BigDecimal.valueOf(Double.parseDouble(str)));
  }

  /**
   * 对象转换为 Bool
   *
   * @param value       对象
   * @param isPrimitive 是否原始类型
   * @return Bool值
   */
  public static Boolean toBool(Object value, boolean isPrimitive) {
    if (null == value)
      return isPrimitive ? false : null;

    if (value instanceof Boolean) {
      return (Boolean) value;
    }

    if (value instanceof Number) {
      return ((Number) value).intValue() != 0;
    }

    if (value instanceof Character) {
      // cast to char is required to compile with java 8
      return (char) value == 'Y'
          || (char) value == 'T'
          || (char) value == 'J';
    }

    if (value instanceof String) {
      String strVal = ((String) value).trim();
      return "Y".equalsIgnoreCase(strVal) || "YES".equalsIgnoreCase(strVal) || "TRUE".equalsIgnoreCase(strVal) ||
          "T".equalsIgnoreCase(strVal) || "J".equalsIgnoreCase(strVal);
    }

    throw new DBException("不能把类型 %s 转换为 %s. ", value.getClass().getName(), Boolean.class.getName());
  }

  /**
   * 对象转换为枚举类型
   *
   * @param clazz 枚举类
   * @param value 值
   * @param <T>   枚举类型泛型
   * @return 枚举
   */
  public static <T extends Enum<T>> T toEnum(Class<T> clazz, Object value) {
    if (null == value) return null;

    String str;
    if (value instanceof String) {
      str = ((String) value).trim();
      if (StringUtil.isEmpty(str))
        return null;
    } else {
      str = StringUtil.toStr(value);
    }

    try {
      return Enum.valueOf(clazz, str);
    } catch (IllegalArgumentException e) {
      throw new DBException("不能把类型 %s 转换为枚举 %s. ", value.getClass(), clazz.getName());
    }
  }

  /**
   * 对象转换为String
   *
   * @param value 对象
   * @return String值
   */
  public static String toStr(Object value) {
    return StringUtil.toStr(value);
  }

  /**
   * 对象转换成日期
   *
   * @param clazz        目标日期类型
   * @param value        对象
   * @param dateFunction 处理函数
   * @param <T>          目标日期类型泛型
   * @return 日期值
   */
  @SuppressWarnings("unchecked")
  public static <T extends Date> T toDate(Class<T> clazz, Object value, Function<Long, T> dateFunction) {
    if (null == value) return null;
    if (clazz.isInstance(value)) return (T) value;
    if (value instanceof Date) return dateFunction.apply(((Date) value).getTime());
    if (value instanceof Number) return dateFunction.apply(((Number) value).longValue());
    throw new DBException("不能把类型 %s 转换为 java.util.Date. ", value.getClass());
  }

  /**
   * 对象转换成日期
   *
   * @param value 对象
   * @return 日期值
   */
  public static Date toDate(Object value) {
    return toDate(Date.class, value, Date::new);
  }

  /**
   * 对象转换成UUID
   *
   * @param value 对象
   * @return UUID
   */
  public static UUID toUUID(Object value) {
    if (null == value)
      return null;

    if (value instanceof String) {
      String str = ((String) value).trim();
      str = str.isEmpty() ? null : str;
      if (null == str)
        return null;
      return UUID.fromString(str);
    }

    throw new DBException("不能把类型 %s 转换为 %s. ", value.getClass().getName(), UUID.class.getName());
  }
}
