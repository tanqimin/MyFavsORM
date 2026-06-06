package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.util.common.Constant;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * {@code OffsetDateTime} 类型的属性处理器.
 * <p>处理 Java {@code OffsetDateTime} 类型与 JDBC {@code TIMESTAMP WITH TIMEZONE} 类型之间的相互转换.
 * 支持时间戳毫秒值存储模式.</p>
 */
public class OffsetDateTimePropertyHandler extends AbstractTemporalAccessorPropertyHandler<OffsetDateTime> {

  /**
   * 构造 {@code OffsetDateTimePropertyHandler} 实例.
   */
  public OffsetDateTimePropertyHandler() {
    super();
  }

  /**
   * 构造 {@code OffsetDateTimePropertyHandler} 实例.
   *
   * @param usingEpochMilli 是否使用时间戳毫秒值存储
   */
  public OffsetDateTimePropertyHandler(boolean usingEpochMilli) {
    super(usingEpochMilli);
  }

  /**
   * 将 {@code Instant} 转换为 {@code OffsetDateTime}.
   *
   * @param instant Instant 对象
   * @return OffsetDateTime 对象
   */
  @Override
  protected OffsetDateTime fromInstant(Instant instant) {
    return OffsetDateTime.ofInstant(instant, Constant.ZONE_ID);
  }

  /**
   * 将 {@code OffsetDateTime} 参数添加到 PreparedStatement.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQLException
   */
  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, OffsetDateTime param)
      throws SQLException {
    if (usingEpochMilli) {
      ps.setLong(paramIndex, param.atZoneSameInstant(Constant.ZONE_ID).toInstant().toEpochMilli());
      return;
    }
    ps.setTimestamp(paramIndex, Timestamp.valueOf(param.toLocalDateTime()));
  }

  /**
   * 获取 SQL 类型代码.
   *
   * @return {@code Types.TIMESTAMP_WITH_TIMEZONE}
   */
  @Override
  public int getSqlType() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }
}
