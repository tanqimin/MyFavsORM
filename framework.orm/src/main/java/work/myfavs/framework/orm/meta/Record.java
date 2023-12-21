package work.myfavs.framework.orm.meta;

import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.convert.ConvertUtil;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * ORM查询记录对象
 */
public class Record extends LinkedHashMap<String, Object> implements IRecord<String> {

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
   * @param attr  属性名称
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
   * @param attr         属性名称
   * @param defaultValue 如果获取属性为null，则返回defaultValue
   * @param <T>          属性类型
   * @return 属性值
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String attr, T defaultValue) {
    final Object obj = super.get(attr);

    if (Objects.isNull(obj)) {
      return defaultValue;
    }
    return (T) obj;
  }

  /**
   * 获取属性
   *
   * @param attr 属性名称
   * @param <T>  属性类型
   * @return 属性值
   */
  public <T> T getBean(String attr) {
    return get(attr, null);
  }

  @Override
  public Object getObj(String attr) {
    return get(attr, null);
  }

  @Override
  public String getStr(String attr) {
    return ConvertUtil.toString(get(attr));
  }

  @Override
  public Integer getInt(String attr) {
    return ConvertUtil.toInt(get(attr));
  }

  @Override
  public Short getShort(String attr) {
    return ConvertUtil.toShort(get(attr));
  }

  @Override
  public Boolean getBool(String attr) {
    return ConvertUtil.toBool(get(attr), false);
  }

  @Override
  public Long getLong(String attr) {
    return ConvertUtil.toLong(get(attr));
  }

  @Override
  public Float getFloat(String attr) {
    return ConvertUtil.toFloat(get(attr));
  }

  @Override
  public Double getDouble(String attr) {
    return ConvertUtil.toDouble(get(attr));
  }

  @Override
  public Byte getByte(String attr) {
    return ConvertUtil.toByte(get(attr));
  }

  @Override
  public BigDecimal getBigDecimal(String attr) {
    return ConvertUtil.toBigDecimal(get(attr));
  }


  @Override
  public <E extends Enum<E>> E getEnum(Class<E> clazz, String attr) {
    return ConvertUtil.toEnum(clazz, get(attr));
  }

  @Override
  public Date getDate(String attr) {
    return ConvertUtil.toDate(get(attr));
  }

  @Override
  public UUID getUUID(String attr) {
    return ConvertUtil.toUUID(get(attr));
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
   * 转换为Bean对象
   *
   * @param bean Bean
   * @param <T>  Bean类型
   * @return Bean
   */
  public <T> T toBean(T bean) {
    return toBean(bean, false);
  }

  private <T> T toBean(T bean, boolean ignoreCase) {
    List<Field> fields = ReflectUtil.getFields(bean.getClass());
    Set<String> keySet = this.keySet();
    for (Field field : fields) {
      String fieldName = field.getName();
      for (String key : keySet) {
        if (StringUtil.equals(key, fieldName, ignoreCase)) {
          ReflectUtil.setFieldValue(field, bean, this.get(key));
          break;
        }
      }
    }
    return bean;
  }

  /**
   * 转换为Bean对象
   *
   * @param tClass Bean类
   * @param <T>    Bean类型
   * @return Bean
   */
  public <T> T toBean(Class<T> tClass) {
    T bean = ReflectUtil.newInstance(tClass);

    return toBean(bean);
  }

  /**
   * 转换为Bean对象，忽略属性大小写
   *
   * @param tClass Bean类
   * @param <T>    Bean类型
   * @return Bean
   */
  public <T> T toBeanIgnoreCase(Class<T> tClass) {
    T bean = ReflectUtil.newInstance(tClass);

    return toBean(bean, true);
  }

  /**
   * Bean转换为Record
   *
   * @param bean Bean
   * @param <T>  Bean类型
   * @return Record
   */
  public static <T> Record toRecord(T bean) {
    if (Objects.isNull(bean)) return null;

    Record      record = new Record();
    List<Field> fields = ReflectUtil.getFields(bean.getClass());
    for (Field field : fields) {
      record.put(field.getName(), ReflectUtil.getFieldValue(field, bean));
    }
    return record;
  }

  @Override
  public Record clone() {
    return (Record) super.clone();
  }
}
