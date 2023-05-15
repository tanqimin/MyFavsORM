package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalTime;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/**
 * TIMESTAMP WITHOUT TIMEZONE
 *
 * @author tanqimin
 */
public class LocalTimePropertyHandler extends PropertyHandler<LocalTime> {

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

    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }
    ps.setTime(paramIndex, Time.valueOf(param));
  }

  @Override
  public int getSqlType() {
    return Types.TIME;
  }
}
