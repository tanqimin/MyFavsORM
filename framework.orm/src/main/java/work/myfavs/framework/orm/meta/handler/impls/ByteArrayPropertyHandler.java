package work.myfavs.framework.orm.meta.handler.impls;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ByteUtil;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.common.IOUtil;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class ByteArrayPropertyHandler extends PropertyHandler<byte[]> {

  @Override
  public byte[] convert(ResultSet rs, String columnName, Class<byte[]> clazz) throws SQLException {
    Object val = rs.getObject(columnName);
    if (val == null) return null;

    if (val instanceof Blob) {
      Blob b = (Blob) val;
      try {
        try (InputStream stream = b.getBinaryStream()) {

          return IOUtil.toByteArray(stream);
        } finally {
          // ignore stream.close errors
          try {
            b.free();
          } catch (Throwable ignore) {
            // ignore blob.free errors
          }
        }
      } catch (SQLException | IOException e) {
        throw new DBException(e, "Error converting Blob to byte[]");
      }
    }

    if (val instanceof byte[]) {
      return (byte[]) val;
    }

    throw new DBException("could not convert {} to byte[]", val.getClass().getName());
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, byte[] param) throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }
    ps.setBytes(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.VARBINARY;
  }
}
