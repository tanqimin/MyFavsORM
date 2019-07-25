package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BytePropertyHandler
    extends PropertyHandler<Byte> {


  @Override
  public Byte convert(ResultSet rs, String columnName, Class<Byte> clazz)
      throws SQLException {

    byte i = rs.getByte(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Byte param)
      throws SQLException {

    ps.setByte(paramIndex, param);
  }

}
