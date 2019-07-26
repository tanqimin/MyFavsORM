package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class ByteArrayPropertyHandler
    extends PropertyHandler<byte[]> {


  @Override
  public byte[] convert(ResultSet rs, String columnName, Class<byte[]> clazz)
      throws SQLException {

    byte[] val = rs.getBytes(columnName);

    return rs.wasNull()
        ? null
        : val;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, byte[] param)
      throws SQLException {

    ps.setBytes(paramIndex, param);
  }

}
