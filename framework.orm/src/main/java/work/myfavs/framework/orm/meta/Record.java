package work.myfavs.framework.orm.meta;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import work.myfavs.framework.orm.util.common.RecordValueGetter;

/** ORM查询记录对象 */
@SuppressWarnings("unchecked")
public class Record extends LinkedHashMap<String, Object> implements RecordValueGetter<String> {

  private static final long serialVersionUID = 4437164782221886837L;

  /**
   * 创建
   *
   * @return Record
   */
  public static Record create() {
    return new Record();
  }

  /**
   * 设置属性
   *
   * @param attr 属性名称
   * @param value 属性值
   * @return Record
   */
  public Record set(String attr, Object value) {
    super.put(attr, value);
    return this;
  }

  /**
   * 获取属性
   *
   * @param attr 属性名称
   * @param defaultValue 如果获取属性为null，则返回defaultValue
   * @param <T> 属性类型
   * @return 属性值
   */
  public <T> T get(String attr, T defaultValue) {
    final Object obj = super.get(attr);
    if (obj == null) {
      return defaultValue;
    }
    return (T) obj;
  }

  /**
   * 获取属性
   *
   * @param attr 属性名称
   * @param <T> 属性类型
   * @return 属性值
   */
  public <T> T getBean(String attr) {
    return get(attr, null);
  }

  @Override
  public Object getObj(String attr) {
    return super.get(attr);
  }

  @Override
  public String getStr(String attr) {
    return Convert.toStr(get(attr));
  }

  @Override
  public Integer getInt(String attr) {
    return Convert.toInt(get(attr));
  }

  @Override
  public Short getShort(String attr) {
    return Convert.toShort(get(attr));
  }

  @Override
  public Boolean getBool(String attr) {
    return Convert.toBool(get(attr));
  }

  @Override
  public Long getLong(String attr) {
    return Convert.toLong(get(attr));
  }

  @Override
  public Character getChar(String attr) {
    return Convert.toChar(get(attr));
  }

  @Override
  public Float getFloat(String attr) {
    return Convert.toFloat(get(attr));
  }

  @Override
  public Double getDouble(String attr) {
    return Convert.toDouble(get(attr));
  }

  @Override
  public Byte getByte(String attr) {
    return Convert.toByte(get(attr));
  }

  @Override
  public BigDecimal getBigDecimal(String attr) {
    return Convert.toBigDecimal(get(attr));
  }

  @Override
  public BigInteger getBigInteger(String attr) {
    return Convert.toBigInteger(get(attr));
  }

  @Override
  public <E extends Enum<E>> E getEnum(Class<E> clazz, String attr) {
    return Convert.toEnum(clazz, get(attr));
  }

  @Override
  public Date getDate(String attr) {
    return Convert.toDate(get(attr));
  }

  @Override
  public LocalDateTime getLocalDateTime(String attr) {
    return Convert.toLocalDateTime(get(attr));
  }

  /**
   * 获取byte[]类型值
   *
   * @param attr 属性名
   * @return byte[]类型属性值
   */
  public byte[] getBytes(String attr) {
    return get(attr, null);
  }

  /**
   * 获取LocalDate类型值
   *
   * @param attr 属性名
   * @return LocalDate类型属性值
   */
  public LocalDate getLocalDate(String attr) {
    return getLocalDateTime(attr).toLocalDate();
  }

  /**
   * 获取Time类型值
   *
   * @param attr 属性名
   * @return Time类型属性值
   */
  public Time getTime(String attr) {
    return get(attr, null);
  }

  /**
   * 获取Timestamp类型值
   *
   * @param attr 属性名
   * @return Timestamp类型属性值
   */
  public Timestamp getTimestamp(String attr) {
    return get(attr, null);
  }

  /**
   * 获取Number类型值
   *
   * @param attr 属性名
   * @return Number类型属性值
   */
  public Number getNumber(String attr) {
    return get(attr, null);
  }

  /**
   * 转换为Bean对象
   *
   * @param bean Bean
   * @param <T> Bean类型
   * @return Bean
   */
  public <T> T toBean(T bean) {
    BeanUtil.fillBeanWithMap(this, bean, false, false);
    return bean;
  }

  /**
   * 转换为Bean对象
   *
   * @param tClass Bean类
   * @param <T> Bean类型
   * @return Bean
   */
  public <T> T toBean(Class<T> tClass) {
    return BeanUtil.mapToBean(this, tClass, false);
  }

  /**
   * 转换为Bean对象，忽略属性大小写
   *
   * @param tClass Bean类
   * @param <T> Bean类型
   * @return Bean
   */
  public <T> T toBeanIgnoreCase(Class<T> tClass) {
    return BeanUtil.mapToBeanIgnoreCase(this, tClass, false);
  }

  /**
   * Bean转换为Record
   *
   * @param bean Bean
   * @param <T> Bean类型
   * @return Record
   */
  public <T> Record toRecord(T bean) {
    this.putAll(BeanUtil.beanToMap(bean));
    return this;
  }

  @Override
  public Record clone() {
    return (Record) super.clone();
  }
}
