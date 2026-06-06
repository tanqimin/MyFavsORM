package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * {@link Date} 类型属性处理器的抽象基类.
 * <p>提供将毫秒时间戳转换为 {@link Date} 子类对象的能力.
 *
 * @param <T> Date 的子类型
 */
public abstract class AbstractDatePropertyHandler<T extends Date> extends PropertyHandler<T> {

  /**
   * 从给定的毫秒时间戳转换为指定类型的日期对象.
   *
   * @param millisecond 毫秒时间戳
   * @return 日期对象
   */
  protected abstract T fromMilliseconds(long millisecond);

  protected final boolean usingEpochMilli;

  /**
   * 构造一个不使用毫秒时间戳的处理器实例.
   */
  public AbstractDatePropertyHandler() {
    this.usingEpochMilli = false;
  }

  /**
   * 构造一个处理器实例，指定是否使用毫秒时间戳.
   *
   * @param usingEpochMilli 是否使用毫秒时间戳
   */
  public AbstractDatePropertyHandler(boolean usingEpochMilli) {
    this.usingEpochMilli = usingEpochMilli;
  }

  /**
   * 从 {@link ResultSet} 中读取指定列的值，并转换为日期对象.
   *
   * @param rs          ResultSet
   * @param columnIndex 列索引
   * @param clazz       目标类型
   * @return 日期对象
   * @throws SQLException SQLException
   */
  @Override
  public T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException {
    Object val = rs.getObject(columnIndex);
    return ConvertUtil.toDate(clazz, val, this::fromMilliseconds);
  }
}
