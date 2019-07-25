package work.myfavs.framework.example.util.exts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;
import work.myfavs.framework.orm.util.JSONUtil;

public class ConfigPropertyHandler
    extends PropertyHandler<Config> {

  @Override
  public Config convert(ResultSet rs, String columnName, Class<Config> clazz)
      throws SQLException {

    String val = rs.getString(columnName);

    return rs.wasNull()
        ? null
        : JSONUtil.toObject(Config.class, val);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Config param)
      throws SQLException {

    ps.setString(paramIndex, JSONUtil.toStr(param));
  }

}
