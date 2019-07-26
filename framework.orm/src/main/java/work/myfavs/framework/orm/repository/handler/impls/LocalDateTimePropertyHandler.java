package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

public class LocalDateTimePropertyHandler
    extends PropertyHandler<LocalDateTime> {

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

    ps.setTimestamp(paramIndex, param == null
        ? null
        : Timestamp.valueOf(param));
  }

}
