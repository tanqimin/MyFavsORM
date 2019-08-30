package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BooleanPropertyHandler
    extends PropertyHandler<Boolean> {

  @Override
  public Boolean convert(ResultSet rs, String columnName, Class<Boolean> clazz)
      throws SQLException {

    boolean i = rs.getBoolean(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Boolean param)
      throws SQLException {

    ps.setBoolean(paramIndex, param);
  }

}
