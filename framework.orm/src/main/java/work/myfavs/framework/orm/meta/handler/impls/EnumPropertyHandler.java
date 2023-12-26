package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.common.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class EnumPropertyHandler extends PropertyHandler<Object> {

  @Override
  @SuppressWarnings("unchecked")
  public Object convert(ResultSet rs, int columnIndex, Class clazz) throws SQLException {

    String val = rs.getString(columnIndex);
    if (StringUtil.isEmpty(val)) return null;

    try {
      return Enum.valueOf(clazz, val);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {

    ps.setString(paramIndex, StringUtil.toStr(param));
  }

  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }
}
