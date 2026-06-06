package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * {@link TemporalAccessor} 类型属性处理器的抽象基类.
 * <p>支持从 {@link ResultSet} 中读取日期时间值，并通过 {@link Instant} 转换为目标类型.
 *
 * @param <T> TemporalAccessor 的子类型
 */
public abstract class AbstractTemporalAccessorPropertyHandler<T extends TemporalAccessor> extends PropertyHandler<T> {

  protected final boolean usingEpochMilli;

  /**
   * 构造一个不使用毫秒时间戳的处理器实例.
   */
  public AbstractTemporalAccessorPropertyHandler() {
    this.usingEpochMilli = false;
  }

  /**
   * 构造一个处理器实例，指定是否使用毫秒时间戳.
   *
   * @param usingEpochMilli 是否使用毫秒时间戳
   */
  public AbstractTemporalAccessorPropertyHandler(boolean usingEpochMilli) {
    this.usingEpochMilli = usingEpochMilli;
  }

  /**
   * 从 {@link ResultSet} 中读取指定列的值，并转换为时间戳访问对象.
   * <p>根据 {@link #usingEpochMilli} 决定是否按毫秒时间戳处理，
   * 支持 {@link LocalDateTime}、{@link Date}、{@link OffsetDateTime} 等类型.
   *
   * @param rs          ResultSet
   * @param columnIndex 列索引
   * @param clazz       目标类型
   * @return 时间戳访问对象
   * @throws SQLException SQLException
   */
  @Override
  public T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException {
    Object val = rs.getObject(columnIndex);
    if (null == val) return null;

    if (usingEpochMilli) {
      long    timestamp = ((Number) val).longValue();
      Instant instant   = Instant.ofEpochMilli(timestamp);
      return fromInstant(instant);
    }

    if (val instanceof LocalDateTime) {
      Instant instant = ((LocalDateTime) val).atZone(Constant.ZONE_ID).toInstant();
      return fromInstant(instant);
    } else if (val instanceof Date) {
      Instant instant = ((Date) val).toInstant();
      return fromInstant(instant);
    } else if (val instanceof OffsetDateTime) {
      Instant instant = ((OffsetDateTime) val).atZoneSameInstant(Constant.ZONE_ID).toInstant();
      return fromInstant(instant);
    } else {
      throw new InvalidDataAccessException("不能把类型 %s 转换为 %s. ", val.getClass(), clazz.getName());
    }

  }

  /**
   * 从给定的 {@link Instant} 转换为指定类型的时间戳访问对象.
   *
   * @param instant Instant 对象
   * @return 时间戳访问对象
   */
  protected abstract T fromInstant(Instant instant);
}
