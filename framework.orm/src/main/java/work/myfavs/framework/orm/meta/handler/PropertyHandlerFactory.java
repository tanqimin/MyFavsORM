package work.myfavs.framework.orm.meta.handler;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import work.myfavs.framework.orm.meta.handler.impls.*;

public class PropertyHandlerFactory {

  private final static Map<String, PropertyHandler<?>> HANDLER_MAP             = new HashMap<>();
  private final static EnumPropertyHandler             ENUM_PROPERTY_HANDLER   = new EnumPropertyHandler();
  private final static ObjectPropertyHandler           OBJECT_PROPERTY_HANDLER = new ObjectPropertyHandler();

  private PropertyHandlerFactory() {

  }

  /**
   * 注册默认的PropertyHandler
   */
  public static void registerDefault() {

    register(String.class, new StringPropertyHandler());
    register(java.util.Date.class, new DatePropertyHandler());
    register(LocalDateTime.class, new LocalDateTimePropertyHandler());
    register(LocalDate.class, new LocalDatePropertyHandler());
    register(LocalTime.class, new LocalTimePropertyHandler());
    register(BigDecimal.class, new BigDecimalPropertyHandler());
    register(Boolean.class, new BooleanPropertyHandler());
    register(Boolean.TYPE, new BooleanPropertyHandler());
    register(Integer.class, new IntegerPropertyHandler());
    register(Integer.TYPE, new IntegerPropertyHandler());
    register(Long.class, new LongPropertyHandler());
    register(Long.TYPE, new LongPropertyHandler());
    register(UUID.class, new UUIDPropertyHandler());
    register(Short.class, new ShortPropertyHandler());
    register(Short.TYPE, new ShortPropertyHandler());
    register(Double.class, new DoublePropertyHandler());
    register(Double.TYPE, new DoublePropertyHandler());
    register(Float.class, new FloatPropertyHandler());
    register(Float.TYPE, new FloatPropertyHandler());
    register(Byte.class, new BytePropertyHandler());
    register(Byte.TYPE, new BytePropertyHandler());
    register(byte[].class, new ByteArrayPropertyHandler());
    register(Byte[].class, new ByteArrayPropertyHandler());
    register(Blob.class, new BlobPropertyHandler());
    register(Clob.class, new ClobPropertyHandler());
  }

  /**
   * 注册解析器类型
   *
   * @param clazz           Class
   * @param propertyHandler PropertyHandler
   */
  public static void register(Class<?> clazz, PropertyHandler propertyHandler) {

    HANDLER_MAP.put(clazz.getName(), propertyHandler);
  }

  /**
   * 注册解析器类型
   *
   * @param map Map
   */
  public static void register(Map<Class<?>, PropertyHandler> map) {

    for (Entry<Class<?>, PropertyHandler> entry : map.entrySet()) {
      HANDLER_MAP.put(entry.getKey().getName(), entry.getValue());
    }
  }

  public static <T> T convert(ResultSet rs, String columnName, Class<T> tClass)
      throws SQLException {

    return (T) getInstance(tClass).convert(rs, columnName, tClass);
  }

  public static void addParameter(PreparedStatement ps, int index, Object param)
      throws SQLException {

    if (param == null) {
      ps.setObject(index, null);
      return;
    }

    getInstance(param.getClass()).addParameter(ps, index, param);
  }

  public static PropertyHandler getInstance(Class<?> clazz) {

    String clazzName = clazz.getName();
    if (HANDLER_MAP.containsKey(clazzName)) {
      return HANDLER_MAP.get(clazzName);
    }

    if (clazz.isEnum()) {
      return ENUM_PROPERTY_HANDLER;
    }

    return OBJECT_PROPERTY_HANDLER;
  }

}
