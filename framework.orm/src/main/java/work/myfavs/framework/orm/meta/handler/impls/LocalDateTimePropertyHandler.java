package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

public class LocalDateTimePropertyHandler extends PropertyHandler<LocalDateTime> {

  @Override
  public LocalDateTime convert(ResultSet rs, String columnName, Class<LocalDateTime> clazz)
      throws SQLException {

    Timestamp val = rs.getTimestamp(columnName);
    if (rs.wasNull()) {
      return null;
    }
    return val.toLocalDateTime();
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, LocalDateTime param)
      throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, Types.TIMESTAMP);
      return;
    }
    ps.setTimestamp(paramIndex, Timestamp.valueOf(param));
  }
}
