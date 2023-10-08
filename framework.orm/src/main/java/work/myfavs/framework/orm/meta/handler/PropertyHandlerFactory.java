package work.myfavs.framework.orm.meta.handler;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import work.myfavs.framework.orm.meta.handler.impls.BigDecimalPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.BlobPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.BooleanPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.ByteArrayPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.BytePropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.ClobPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.DatePropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.DoublePropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.EnumPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.FloatPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.IntegerPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.LocalDatePropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.LocalDateTimePropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.LocalTimePropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.LongPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.ObjectPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.ShortPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.StringPropertyHandler;
import work.myfavs.framework.orm.meta.handler.impls.UUIDPropertyHandler;

public class PropertyHandlerFactory {

  private static final Map<String, PropertyHandler<?>> HANDLER_MAP = new HashMap<>();
  private static final EnumPropertyHandler ENUM_PROPERTY_HANDLER = new EnumPropertyHandler();
  private static final ObjectPropertyHandler OBJECT_PROPERTY_HANDLER = new ObjectPropertyHandler();

  private PropertyHandlerFactory() {}

  /** 注册默认的PropertyHandler */
  public static void registerDefault() {

    register(String.class, new StringPropertyHandler());
    register(java.util.Date.class, new DatePropertyHandler());
    register(LocalDateTime.class, new LocalDateTimePropertyHandler());
    register(LocalDate.class, new LocalDatePropertyHandler());
    register(LocalTime.class, new LocalTimePropertyHandler());
    register(BigDecimal.class, new BigDecimalPropertyHandler());
    register(boolean.class, new BooleanPropertyHandler(true));
    register(Boolean.class, new BooleanPropertyHandler());
    register(Boolean.TYPE, new BooleanPropertyHandler());
    register(int.class, new IntegerPropertyHandler(true));
    register(Integer.class, new IntegerPropertyHandler());
    register(Integer.TYPE, new IntegerPropertyHandler());
    register(long.class, new LongPropertyHandler(true));
    register(Long.class, new LongPropertyHandler());
    register(Long.TYPE, new LongPropertyHandler());
    register(UUID.class, new UUIDPropertyHandler());
    register(short.class, new ShortPropertyHandler(true));
    register(Short.class, new ShortPropertyHandler());
    register(Short.TYPE, new ShortPropertyHandler());
    register(double.class, new DoublePropertyHandler(true));
    register(Double.class, new DoublePropertyHandler());
    register(Double.TYPE, new DoublePropertyHandler());
    register(float.class, new FloatPropertyHandler(true));
    register(Float.class, new FloatPropertyHandler());
    register(Float.TYPE, new FloatPropertyHandler());
    register(byte.class, new BytePropertyHandler(true));
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
   * @param clazz Class
   * @param propertyHandler PropertyHandler
   */
  @SuppressWarnings("rawtypes")
  public static void register(Class<?> clazz, PropertyHandler propertyHandler) {

    HANDLER_MAP.put(clazz.getName(), propertyHandler);
  }

  @SuppressWarnings("rawtypes")
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
