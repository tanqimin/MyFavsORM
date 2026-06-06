package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * {@link java.util.Date} 类型的属性处理器.
 * <p>用于处理 Java {@link java.util.Date} 类型与数据库 {@code TIMESTAMP} 类型之间的相互转换.</p>
 */
public class DatePropertyHandler extends AbstractDatePropertyHandler<Date> {

  @Override
  protected Date fromMilliseconds(long millisecond) {
    return new Date(millisecond);
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
  public void addParameter(PreparedStatement ps, int paramIndex, Date param) throws SQLException {

    if (usingEpochMilli) {
      ps.setLong(paramIndex, param.getTime());
      return;
    }

    if (param instanceof Timestamp) {
      ps.setTimestamp(paramIndex, (Timestamp) param);
      return;
    }

    ps.setTimestamp(paramIndex, new Timestamp(param.getTime()));

  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#TIMESTAMP}
   */
  @Override
  public int getSqlType() {

    return Types.TIMESTAMP;
  }
}
