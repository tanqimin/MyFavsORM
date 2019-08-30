package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

public class LocalDatePropertyHandler
    extends PropertyHandler<LocalDate> {

  @Override
  public LocalDate convert(ResultSet rs, String columnName, Class<LocalDate> clazz)
      throws SQLException {

    Date val = rs.getDate(columnName);
    if (rs.wasNull()) {
      return null;
    }
    return val.toLocalDate();
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, LocalDate param)
      throws SQLException {

    ps.setDate(paramIndex, param == null
        ? null
        : Date.valueOf(param));
  }

}
