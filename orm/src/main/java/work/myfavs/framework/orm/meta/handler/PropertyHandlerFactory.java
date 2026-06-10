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
 * {@link PropertyHandler} 工厂类，负责注册和管理属性处理器实例。
 * <p><b>注册策略（全默认 or 全自定义）：</b></p>
 * <p>若用户未通过 {@link work.myfavs.framework.orm.DBTemplate.Builder#mapping(java.util.function.Consumer) DBTemplate.Builder.mapping()}
 * 注册任何自定义处理器，则框架自动注册 23 种内置默认 {@link PropertyHandler}（调用 {@link #registerDefault()}）；</p>
 * <p>若用户注册了任意自定义处理器，则<b>仅使用用户注册的处理器</b>，不再注册默认处理器。
 * 即：一旦 user-custom 介入，框架不再做任何自动注册。</p>
 * <p>因此，使用自定义注册时，用户需要自行注册所有需要用到的类型（包括基础类型与包装类需分别注册，
 * 如 {@code long.class} 和 {@code Long.class} 为两个不同的 key）。</p>
 */
public class PropertyHandlerFactory {

  private static final Map<String, PropertyHandler<?>> HANDLER_MAP             = new ConcurrentHashMap<>();
  private static final EnumPropertyHandler             ENUM_PROPERTY_HANDLER   = new EnumPropertyHandler();
  private static final ObjectPropertyHandler           OBJECT_PROPERTY_HANDLER = new ObjectPropertyHandler();

  private PropertyHandlerFactory() {}

  /**
   * 注册框架内置的 23 种默认 {@link PropertyHandler}。
   * <p>包含：String、NVarchar、Date、LocalDateTime、OffsetDateTime、BigDecimal、Boolean(包装类+基础类型)、
   * Integer(包装类+基础类型)、Long(包装类+基础类型)、UUID、Short(包装类+基础类型)、
   * Double(包装类+基础类型)、Float(包装类+基础类型)、Byte(包装类+基础类型)、
   * byte[]、Byte[]、Blob、Clob。</p>
   * <p>此方法在用户<b>未</b>通过 {@link work.myfavs.framework.orm.DBTemplate.Builder#mapping(java.util.function.Consumer)} 
   * 注册任何自定义处理器时自动调用。</p>
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
    register(int.class, new IntegerPropertyHandler(true));
    register(Integer.class, new IntegerPropertyHandler());
    register(long.class, new LongPropertyHandler(true));
    register(Long.class, new LongPropertyHandler());
    register(UUID.class, new UUIDPropertyHandler());
    register(short.class, new ShortPropertyHandler(true));
    register(Short.class, new ShortPropertyHandler());
    register(double.class, new DoublePropertyHandler(true));
    register(Double.class, new DoublePropertyHandler());
    register(float.class, new FloatPropertyHandler(true));
    register(Float.class, new FloatPropertyHandler());
    register(byte.class, new BytePropertyHandler(true));
    register(Byte.class, new BytePropertyHandler());
    register(byte[].class, new ByteArrayPropertyHandler());
    register(Byte[].class, new ByteArrayPropertyHandler());
    register(Blob.class, new BlobPropertyHandler());
    register(Clob.class, new ClobPropertyHandler());
  }

  /**
   * 注册属性处理器。
   * <p><b>注意：</b>一旦通过 {@link work.myfavs.framework.orm.DBTemplate.Builder#mapping(java.util.function.Consumer)}
   * 调用了此方法注册任意处理器，框架内置的 23 种默认处理器将全部失效，
   * 用户需要自行注册所有需要用到的类型。基础类型与包装类需分别注册
   * （如 {@code long.class} 与 {@code Long.class} 对应不同的 key）。</p>
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
