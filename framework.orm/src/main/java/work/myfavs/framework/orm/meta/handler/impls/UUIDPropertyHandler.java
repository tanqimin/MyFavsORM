package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class UUIDPropertyHandler extends PropertyHandler<UUID> {

  @Override
  public UUID convert(ResultSet rs, int columnIndex, Class<UUID> clazz) throws SQLException {

    return ConvertUtil.toUUID(rs.getObject(columnIndex));
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, UUID param) throws SQLException {

    ps.setString(paramIndex, param.toString());
  }

  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }
}
