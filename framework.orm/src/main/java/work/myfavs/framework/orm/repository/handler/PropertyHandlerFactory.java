package work.myfavs.framework.orm.repository.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import work.myfavs.framework.orm.repository.handler.impls.*;

public class PropertyHandlerFactory {

  private final static Map<String, PropertyHandler> HANDLER_MAP             = new HashMap<>();
  private final static EnumPropertyHandler          ENUM_PROPERTY_HANDLER   = new EnumPropertyHandler();
  private final static ObjectPropertyHandler        OBJECT_PROPERTY_HANDLER = new ObjectPropertyHandler();

  static {
    HANDLER_MAP.put(String.class.getName(), new StringPropertyHandler());
    HANDLER_MAP.put(java.util.Date.class.getName(), new DatePropertyHandler());
    HANDLER_MAP.put(java.sql.Date.class.getName(), new SqlDatePropertyHandler());
    HANDLER_MAP.put(java.sql.Time.class.getName(), new TimePropertyHandler());
    HANDLER_MAP.put(java.sql.Timestamp.class.getName(), new TimestampPropertyHandler());
    HANDLER_MAP.put(BigDecimal.class.getName(), new BigDecimalPropertyHandler());

    HANDLER_MAP.put(Boolean.class.getName(), new BooleanPropertyHandler());
    HANDLER_MAP.put(Boolean.TYPE.getName(), new BooleanPropertyHandler());
    HANDLER_MAP.put(Integer.class.getName(), new IntegerPropertyHandler());
    HANDLER_MAP.put(Integer.TYPE.getName(), new IntegerPropertyHandler());
    HANDLER_MAP.put(Long.class.getName(), new LongPropertyHandler());
    HANDLER_MAP.put(Long.TYPE.getName(), new LongPropertyHandler());
    HANDLER_MAP.put(Short.class.getName(), new ShortPropertyHandler());
    HANDLER_MAP.put(Short.TYPE.getName(), new ShortPropertyHandler());
    HANDLER_MAP.put(Double.class.getName(), new DoublePropertyHandler());
    HANDLER_MAP.put(Double.TYPE.getName(), new DoublePropertyHandler());
    HANDLER_MAP.put(Float.class.getName(), new FloatPropertyHandler());
    HANDLER_MAP.put(Float.TYPE.getName(), new FloatPropertyHandler());
    HANDLER_MAP.put(Byte.class.getName(), new BytePropertyHandler());
    HANDLER_MAP.put(Byte.TYPE.getName(), new BytePropertyHandler());
  }

  private PropertyHandlerFactory() {

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
