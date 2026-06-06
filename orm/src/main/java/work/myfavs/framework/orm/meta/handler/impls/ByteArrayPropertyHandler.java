package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.common.IOUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class ByteArrayPropertyHandler extends PropertyHandler<byte[]> {

  @Override
  public byte[] convert(ResultSet rs, int columnIndex, Class<byte[]> clazz) throws SQLException {
    Object val = rs.getObject(columnIndex);
    if (null == val) return null;

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
        throw new DBException(e, "转换 Blob 到 byte[] 时发生异常: %s", e.getMessage());
      }
    }

    if (val instanceof byte[]) {
      return (byte[]) val;
    }

    throw new DBException("不能把 %s 类型转换成 byte[] 类型", val.getClass().getName());
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, byte[] param) throws SQLException {

    ps.setBytes(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.VARBINARY;
  }
}
