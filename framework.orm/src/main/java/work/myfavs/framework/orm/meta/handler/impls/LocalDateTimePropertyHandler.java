package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.*;
import java.time.LocalDateTime;

public class LocalDateTimePropertyHandler extends PropertyHandler<LocalDateTime> {

  @Override
  public LocalDateTime convert(ResultSet rs, int columnIndex, Class<LocalDateTime> clazz) throws SQLException {
    Timestamp timestamp = rs.getTimestamp(columnIndex);
    if (null == timestamp) return null;
    return timestamp.toLocalDateTime();
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, LocalDateTime param) throws SQLException {
    ps.setTimestamp(paramIndex, Timestamp.valueOf(param));
  }

  @Override
  public int getSqlType() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }
}
