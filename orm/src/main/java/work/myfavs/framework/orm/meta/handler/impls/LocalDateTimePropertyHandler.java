package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.util.common.Constant;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * {@link LocalDateTime} 类型的属性处理器.
 * <p>用于处理 Java {@link LocalDateTime} 类型与数据库 {@code TIMESTAMP_WITH_TIMEZONE} 类型之间的相互转换.</p>
 */
public class LocalDateTimePropertyHandler extends AbstractTemporalAccessorPropertyHandler<LocalDateTime> {

  /**
   * 构造 LocalDateTimePropertyHandler.
   */
  public LocalDateTimePropertyHandler() {
    super();
  }

  /**
   * 构造 LocalDateTimePropertyHandler, 指定是否使用毫秒时间戳.
   *
   * @param usingEpochMilli 是否使用毫秒时间戳
   */
  public LocalDateTimePropertyHandler(boolean usingEpochMilli) {
    super(usingEpochMilli);
  }

  @Override
  protected LocalDateTime fromInstant(Instant instant) {
    return LocalDateTime.ofInstant(instant, Constant.ZONE_ID);
  }

  /**
   * 设置 PreparedStatement 参数, 支持毫秒时间戳和 {@link Timestamp} 两种模式.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQL 异常
   */
  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, LocalDateTime param) throws SQLException {
    if (usingEpochMilli) {
      ps.setLong(paramIndex, param.atZone(Constant.ZONE_ID).toInstant().toEpochMilli());
      return;
    }
    ps.setTimestamp(paramIndex, Timestamp.valueOf(param));
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#TIMESTAMP_WITH_TIMEZONE}
   */
  @Override
  public int getSqlType() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }
}
