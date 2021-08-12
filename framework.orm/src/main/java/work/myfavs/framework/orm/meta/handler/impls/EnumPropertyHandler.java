package work.myfavs.framework.orm.meta.handler.impls;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/** Created by tanqimin on 2016/1/29. */
public class EnumPropertyHandler extends PropertyHandler<Object> {

  @Override
  @SuppressWarnings("unchecked")
  public Object convert(ResultSet rs, String columnName, Class clazz) throws SQLException {

    return EnumUtil.fromStringQuietly(clazz, rs.getString(columnName));
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, Types.VARCHAR);
      return;
    }
    ps.setString(paramIndex, StrUtil.toString(param));
  }
}
