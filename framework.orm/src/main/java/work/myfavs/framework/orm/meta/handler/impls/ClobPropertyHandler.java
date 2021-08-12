package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/** Created by tanqimin on 2016/1/29. */
public class ClobPropertyHandler extends PropertyHandler<Clob> {

  @Override
  public Clob convert(ResultSet rs, String columnName, Class<Clob> clazz) throws SQLException {

    Clob i = rs.getClob(columnName);
    return rs.wasNull() ? null : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Clob param) throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, Types.CLOB);
      return;
    }
    ps.setClob(paramIndex, param);
  }
}
