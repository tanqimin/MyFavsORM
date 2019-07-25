package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class ObjectPropertyHandler
    extends PropertyHandler<Object> {

  @Override
  public Object convert(ResultSet rs, String columnName, Class<Object> clazz)
      throws SQLException {

    Object val = rs.getObject(columnName);
    return rs.wasNull()
        ? null
        : val;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param)
      throws SQLException {

    ps.setObject(paramIndex, param);
  }

}
