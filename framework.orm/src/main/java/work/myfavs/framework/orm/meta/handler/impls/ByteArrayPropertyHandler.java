package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

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

    if (param == null) {
      ps.setNull(paramIndex, Types.VARBINARY);
      return;
    }
    ps.setBytes(paramIndex, param);
  }

}
