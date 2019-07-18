package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BytePropertyHandler
    implements PropertyHandler {


  @Override
  public Object convert(ResultSet rs, String columnName, Class<?> clazz)
      throws SQLException {

    byte i = rs.getByte(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param)
      throws SQLException {

    ps.setByte(paramIndex, (Byte) param);
  }

}
