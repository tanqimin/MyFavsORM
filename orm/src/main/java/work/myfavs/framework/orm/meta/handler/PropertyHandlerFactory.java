package work.myfavs.framework.orm.meta.handler;

import work.myfavs.framework.orm.meta.handler.impls.*;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link PropertyHandler} 工厂类，负责注册和管理属性处理器实例。
 * <p><b>注册策略（默认 + 自定义覆盖）：</b></p>
 * <p>每次创建 {@link work.myfavs.framework.orm.DBTemplate DBTemplate} 时，
 * 框架自动注册 23 种内置默认 {@link PropertyHandler}（调用 {@link #registerDefault()}）。</p>
 * <p>若用户通过 {@link work.myfavs.framework.orm.DBTemplate.Builder#mapping(java.util.function.Consumer)
 * DBTemplate.Builder.mapping()} 注册了自定义处理器，则自定义处理器会<b>覆盖</b>同类型的默认处理器，
 * 未覆盖的其他类型仍使用默认处理器。</p>
 * <p>基础类型与包装类需分别注册（如 {@code long.class} 和 {@code Long.class} 为两个不同的 key）。</p>
 */
public class PropertyHandlerFactory {

  private static final Map<String, PropertyHandler<?>> HANDLER_MAP             = new ConcurrentHashMap<>();
  private static final EnumPropertyHandler             ENUM_PROPERTY_HANDLER   = new EnumPropertyHandler();
  private static final ObjectPropertyHandler           OBJECT_PROPERTY_HANDLER = new ObjectPropertyHandler();
  private static boolean                               defaultsRegistered      = false;

  private PropertyHandlerFactory() {}

  /**
   * 注册框架内置的 23 种默认 {@link PropertyHandler}。
   * <p>包含：String、NVarchar、Date、LocalDateTime、OffsetDateTime、BigDecimal、Boolean(包装类+基础类型)、
   * Integer(包装类+基础类型)、Long(包装类+基础类型)、UUID、
   * Short(包装类+基础类型)、Double(包装类+基础类型)、Float(包装类+基础类型)、Byte(包装类+基础类型)、
   * byte[]、Byte[]、Blob、Clob。</p>
   * <p>每次创建 {@link work.myfavs.framework.orm.DBTemplate DBTemplate} 时自动调用。
   * 用户通过 {@link work.myfavs.framework.orm.DBTemplate.Builder#mapping(java.util.function.Consumer)}
   * 注册的自定义处理器会按类型覆盖同名的默认处理器。</p>
   */
  public static void registerDefault() {
    if (defaultsRegistered) return;
    defaultsRegistered = true;

    register(String.class, new StringPropertyHandler());
    register(NVarchar.class, new NVarcharPropertyHandler());
    register(java.util.Date.class, new DatePropertyHandler());
    register(LocalDateTime.class, new LocalDateTimePropertyHandler());
    register(BigDecimal.class, new BigDecimalPropertyHandler());
    register(boolean.class, new BooleanPropertyHandler(true));
    register(Boolean.class, new BooleanPropertyHandler());
    register(int.class, new IntegerPropertyHandler(true));
    register(Integer.class, new IntegerPropertyHandler());
    register(long.class, new LongPropertyHandler(true));
    register(Long.class, new LongPropertyHandler());
    register(UUID.class, new UUIDPropertyHandler());
  }

  /**
   * 注册属性处理器。
   * <p>基础类型与包装类需分别注册（如 {@code long.class} 与 {@code Long.class} 对应不同的 key）。</p>
   * <p>自定义处理器会覆盖同类型的默认处理器，未覆盖的类型仍使用默认处理器。</p>
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
  @SuppressWarnings({"rawtypes"})
  public static PropertyHandler getInstance(Class<?> clazz) {

    PropertyHandler<?> handler = HANDLER_MAP.get(clazz.getName());
    if (null != handler) return handler;
    return clazz.isEnum() ? ENUM_PROPERTY_HANDLER : OBJECT_PROPERTY_HANDLER;
  }
}
