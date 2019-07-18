package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class DatePropertyHandler
    implements PropertyHandler {

  @Override
  public Object convert(ResultSet rs, String columnName, Class<?> clazz)
      throws SQLException {

    Timestamp timestamp = rs.getTimestamp(columnName);
    return rs.wasNull()
        ? null
        : new Date(timestamp.getTime());
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param)
      throws SQLException {

    ps.setTimestamp(paramIndex, new Timestamp(((Date) param).getTime()));
  }

}
