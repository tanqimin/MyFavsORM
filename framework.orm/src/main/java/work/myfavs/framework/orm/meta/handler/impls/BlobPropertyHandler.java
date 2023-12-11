package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.*;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BlobPropertyHandler extends PropertyHandler<Blob> {

  @Override
  public Blob convert(ResultSet rs, int columnIndex, Class<Blob> clazz) throws SQLException {

    return rs.getBlob(columnIndex);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Blob param) throws SQLException {
    ps.setBlob(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.BLOB;
  }
}
