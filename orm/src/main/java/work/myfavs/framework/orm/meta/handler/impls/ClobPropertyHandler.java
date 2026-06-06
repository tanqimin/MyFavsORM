package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.*;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class ClobPropertyHandler extends PropertyHandler<Clob> {

  @Override
  public Clob convert(ResultSet rs, int columnIndex, Class<Clob> clazz) throws SQLException {

    return rs.getClob(columnIndex);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Clob param) throws SQLException {

    ps.setClob(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.CLOB;
  }
}
