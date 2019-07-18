package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BooleanPropertyHandler
    implements PropertyHandler {

  @Override
  public Object convert(ResultSet rs, String columnName, Class<?> clazz)
      throws SQLException {

    boolean i = rs.getBoolean(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param)
      throws SQLException {

    ps.setBoolean(paramIndex, (Boolean) param);
  }

}
