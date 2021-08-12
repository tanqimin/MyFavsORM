package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/** Created by tanqimin on 2016/1/29. */
public class DatePropertyHandler extends PropertyHandler<Date> {

  @Override
  public Date convert(ResultSet rs, String columnName, Class<Date> clazz) throws SQLException {

    Timestamp date = rs.getTimestamp(columnName);
    return rs.wasNull() ? null : new Date(date.getTime());
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Date param) throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, Types.TIMESTAMP);
      return;
    }
    ps.setTimestamp(paramIndex, new Timestamp(param.getTime()));
  }
}
