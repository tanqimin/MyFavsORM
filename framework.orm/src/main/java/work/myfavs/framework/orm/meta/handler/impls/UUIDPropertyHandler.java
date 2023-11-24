package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

public class UUIDPropertyHandler extends PropertyHandler<UUID> {

  @Override
  public UUID convert(ResultSet rs, String columnName, Class<UUID> clazz) throws SQLException {

    String val = rs.getString(columnName);
    if (Objects.isNull(val))
      return null;
    return UUID.fromString(val);
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
