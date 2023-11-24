package work.myfavs.framework.orm.meta.handler.impls;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

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
  public Object convert(ResultSet rs, String columnName, Class clazz) throws SQLException {

    return EnumUtil.fromStringQuietly(clazz, rs.getString(columnName));
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {

    ps.setString(paramIndex, StrUtil.toString(param));
  }

  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }
}
