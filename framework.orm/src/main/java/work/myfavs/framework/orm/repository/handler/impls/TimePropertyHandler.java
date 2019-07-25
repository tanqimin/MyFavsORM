package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class TimePropertyHandler
    extends PropertyHandler<Time> {

  @Override
  public Time convert(ResultSet rs, String columnName, Class<Time> clazz)
      throws SQLException {

    Time i = rs.getTime(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Time param)
      throws SQLException {

    ps.setTime(paramIndex, param);
  }

}
