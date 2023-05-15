package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/** Created by tanqimin on 2016/1/29. */
public class BooleanPropertyHandler extends PropertyHandler<Boolean> {

  private boolean isPrimitive;

  public BooleanPropertyHandler() {}

  public BooleanPropertyHandler(boolean isPrimitive) {

    this.isPrimitive = isPrimitive;
  }

  @Override
  public Boolean convert(ResultSet rs, String columnName, Class<Boolean> clazz)
      throws SQLException {

    boolean i = rs.getBoolean(columnName);
    if (rs.wasNull()) {
      if (isPrimitive) {
        return false;
      } else {
        return null;
      }
    }

    return i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Boolean param)
      throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }
    ps.setBoolean(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.BOOLEAN;
  }
}
