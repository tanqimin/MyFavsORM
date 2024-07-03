package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class ObjectPropertyHandler extends PropertyHandler<Object> {

  @Override
  public Object convert(ResultSet rs, int columnIndex, Class<Object> clazz) throws SQLException {

    return rs.getObject(columnIndex);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {

    ps.setObject(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }
}
