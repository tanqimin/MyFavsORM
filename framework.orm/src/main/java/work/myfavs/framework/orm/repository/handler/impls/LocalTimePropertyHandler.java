package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

public class LocalTimePropertyHandler
    extends PropertyHandler<LocalTime> {

  @Override
  public LocalTime convert(ResultSet rs, String columnName, Class<LocalTime> clazz)
      throws SQLException {

    Time val = rs.getTime(columnName);
    if (rs.wasNull()) {
      return null;
    }
    return val.toLocalTime();
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, LocalTime param)
      throws SQLException {

    ps.setTime(paramIndex, param == null
        ? null
        : Time.valueOf(param));
  }

}
