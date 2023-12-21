package work.myfavs.framework.orm.util.convert;


import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.common.ArrayUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class ConvertUtil {
  /**
   * 把对象转换成集合
   *
   * @param object 对象
   * @return 集合
   */
  public static Collection<?> toCollection(Object object) {
    Collection<Object> collection = new ArrayList<>();
    if (Objects.isNull(object)) return collection;

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

    throw new DBException("The argument (Type: %s) can't convert to Collection", object.getClass().getName());
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
    if (Objects.isNull(value))
      return clazz.isPrimitive() ? numberFunc.apply(0) : null;
    else if (value instanceof Number) return numberFunc.apply((Number) value);
    else if (value instanceof String) {
      String str = ((String) value).trim();
      str = str.isEmpty() ? null : str;
      if (Objects.isNull(str))
        return clazz.isPrimitive() ? numberFunc.apply(0) : null;
      return stringFunc.apply(str);
    }
    throw new DBException("Cannot convert type %s to %s.", value.getClass().getName(), clazz.getName());
  }

  public static Integer toInt(Object value) {
    return toNumber(Integer.class, value, Number::intValue, Integer::parseInt);
  }

  public static Short toShort(Object value) {
    return toNumber(Short.class, value, Number::shortValue, Short::parseShort);
  }

  public static Long toLong(Object value) {
    return toNumber(Long.class, value, Number::longValue, Long::parseLong);
  }

  public static Float toFloat(Object value) {
    return toNumber(Float.class, value, Number::floatValue, Float::parseFloat);
  }

  public static Double toDouble(Object value) {
    return toNumber(Double.class, value, Number::doubleValue, Double::parseDouble);
  }

  public static Byte toByte(Object value) {
    return toNumber(Byte.class, value, Number::byteValue, Byte::parseByte);
  }

  public static BigDecimal toBigDecimal(Object value) {
    return toNumber(BigDecimal.class, value,
                    num -> num instanceof BigDecimal ? (BigDecimal) num : BigDecimal.valueOf(num.doubleValue()),
                    str -> BigDecimal.valueOf(Double.parseDouble(str)));
  }

  public static Boolean toBool(Object value, boolean isPrimitive) {
    if (Objects.isNull(value))
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

    throw new DBException("Don't know how to convert type %s to %s", value.getClass().getName(), Boolean.class.getName());
  }

  public static <T extends Enum<T>> T toEnum(Class<T> clazz, Object value) {
    if (Objects.isNull(value)) return null;

    String str;
    if (value instanceof String) {
      str = ((String) value).trim();
      if (StringUtil.isEmpty(str))
        return null;
    } else {
      str = StringUtil.toString(value);
    }

    try {
      return Enum.valueOf(clazz, str);
    } catch (IllegalArgumentException e) {
      throw new DBException("Cannot convert type %s to enum: %s", value.getClass(), clazz.getName());
    }
  }

  public static String toString(Object value) {
    if (Objects.isNull(value)) return null;
    return value.toString();
  }

  @SuppressWarnings("unchecked")
  public static <T extends Date> T toDate(Class<T> clazz, Object value, Function<Long, T> dateFunction) {
    if (Objects.isNull(value)) return null;
    if (clazz.isInstance(value)) return (T) value;
    if (value instanceof Date) return dateFunction.apply(((Date) value).getTime());
    if (value instanceof Number) return dateFunction.apply(((Number) value).longValue());
    throw new DBException("Cannot convert type %s to java.util.Date", value.getClass());
  }

  public static Date toDate(Object value) {
    return toDate(Date.class, value, Date::new);
  }

  public static UUID toUUID(Object value) {
    if (Objects.isNull(value))
      return null;

    if (value instanceof String) {
      String str = ((String) value).trim();
      str = str.isEmpty() ? null : str;
      if (Objects.isNull(str))
        return null;
      return UUID.fromString(str);
    }

    throw new DBException("Cannot convert type %s to %s.", value.getClass().getName(), UUID.class.getName());
  }
}
