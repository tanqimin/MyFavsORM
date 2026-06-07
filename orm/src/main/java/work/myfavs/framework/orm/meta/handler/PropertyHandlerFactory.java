package work.myfavs.framework.orm.meta.handler;

import work.myfavs.framework.orm.meta.handler.impls.*;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link PropertyHandler} 工厂类，负责注册和管理属性处理器实例
 */
public class PropertyHandlerFactory {

  private static final Map<String, PropertyHandler<?>> HANDLER_MAP             = new ConcurrentHashMap<>();
  private static final EnumPropertyHandler             ENUM_PROPERTY_HANDLER   = new EnumPropertyHandler();
  private static final ObjectPropertyHandler           OBJECT_PROPERTY_HANDLER = new ObjectPropertyHandler();

  private PropertyHandlerFactory() {}

  /**
   * 注册默认的PropertyHandler
   */
  public static void registerDefault() {

    register(String.class, new StringPropertyHandler());
    register(NVarchar.class, new NVarcharPropertyHandler());
    register(java.util.Date.class, new DatePropertyHandler());
    register(LocalDateTime.class, new LocalDateTimePropertyHandler());
    register(OffsetDateTime.class, new OffsetDateTimePropertyHandler());
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
   * @param clazz           目标类型
   * @param propertyHandler 属性处理器实例
   */
  @SuppressWarnings("rawtypes")
  public static void register(Class<?> clazz, PropertyHandler propertyHandler) {

    HANDLER_MAP.put(clazz.getName(), propertyHandler);
  }

  /**
   * 获取指定 {@link Class} 对应的 {@link PropertyHandler} 实例
   * <p>如果未注册，枚举类型返回 {@link EnumPropertyHandler}，其他类型返回 {@link ObjectPropertyHandler}</p>
   *
   * @param clazz 目标类型
   * @return {@link PropertyHandler} 实例
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static PropertyHandler getInstance(Class<?> clazz) {

    return HANDLER_MAP.computeIfAbsent(clazz.getName(), key ->
        clazz.isEnum() ? ENUM_PROPERTY_HANDLER : OBJECT_PROPERTY_HANDLER
    );
  }
}
