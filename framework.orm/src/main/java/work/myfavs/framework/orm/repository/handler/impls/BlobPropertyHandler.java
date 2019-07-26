package work.myfavs.framework.orm.repository.handler.impls;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BlobPropertyHandler
    extends PropertyHandler<Blob> {


  @Override
  public Blob convert(ResultSet rs, String columnName, Class<Blob> clazz)
      throws SQLException {

    Blob i = rs.getBlob(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Blob param)
      throws SQLException {

    ps.setBlob(paramIndex, param);
  }

}
