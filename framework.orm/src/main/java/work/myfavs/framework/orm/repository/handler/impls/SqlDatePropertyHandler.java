package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class SqlDatePropertyHandler
    extends PropertyHandler<Date> {

  @Override
  public Date convert(ResultSet rs, String columnName, Class<Date> clazz)
      throws SQLException {

    Date i = rs.getDate(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Date param)
      throws SQLException {

    ps.setDate(paramIndex, param);
  }

}
