package work.myfavs.framework.orm.repository.handler.impls;

import cn.hutool.core.util.StrUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.repository.handler.PropertyHandler;
import work.myfavs.framework.orm.util.ReflectUtil;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class EnumPropertyHandler
    extends PropertyHandler<Object> {

  @Override
  public Object convert(ResultSet rs, String columnName, Class clazz)
      throws SQLException {

    return ReflectUtil.asEnum(clazz, rs.getString(columnName));
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param)
      throws SQLException {

    ps.setString(paramIndex, StrUtil.toString(param));
  }

}
