package work.myfavs.framework.orm.util.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Record基本类型getter接口
 *
 * @param <K> attr类型
 */
public interface RecordValueGetter<K> {

  /**
   * 获取Object属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  Object getObj(K attr);

  /**
   * 获取字符串型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  String getStr(K attr);

  /**
   * 获取int型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  Integer getInt(K attr);

  /**
   * 获取short型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  Short getShort(K attr);

  /**
   * 获取boolean型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  Boolean getBool(K attr);

  /**
   * 获取long型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  Long getLong(K attr);

  /**
   * 获取float型属性值<br>
   *
   * @param attr 属性名
   * @return 属性值
   */
  Float getFloat(K attr);

  /**
   * 获取double型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  Double getDouble(K attr);

  /**
   * 获取byte型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  Byte getByte(K attr);

  /**
   * 获取BigDecimal型属性值
   *
   * @param attr 属性名
   * @return 属性值
   */
  BigDecimal getBigDecimal(K attr);

  /**
   * 获得Enum类型的值
   *
   * @param <E>   枚举类型
   * @param clazz Enum的Class
   * @param attr  attr
   * @return Enum类型的值，无则返回Null
   */
  <E extends Enum<E>> E getEnum(Class<E> clazz, K attr);

  /**
   * 获取Date类型值
   *
   * @param attr 属性名
   * @return Date类型属性值
   */
  Date getDate(K attr);

  /**
   * 获取UUID类型的值
   *
   * @param attr 属性名
   * @return UUID类型属性值
   */
  UUID getUUID(K attr);
}
