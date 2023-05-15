package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/** Created by tanqimin on 2016/1/29. */
public class BytePropertyHandler extends PropertyHandler<Byte> {

  private boolean isPrimitive;

  public BytePropertyHandler() {}

  public BytePropertyHandler(boolean isPrimitive) {

    this.isPrimitive = isPrimitive;
  }

  @Override
  public Byte convert(ResultSet rs, String columnName, Class<Byte> clazz) throws SQLException {

    byte i = rs.getByte(columnName);
    if (rs.wasNull()) {
      if (isPrimitive) {
        return 0;
      } else {
        return null;
      }
    }
    return i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Byte param) throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }
    ps.setByte(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.VARBINARY;
  }
}
