package work.myfavs.framework.orm.meta.handler.impls;

import cn.hutool.core.convert.Convert;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.*;
import java.util.Date;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class DatePropertyHandler extends PropertyHandler<Date> {

  @Override
  public Date convert(ResultSet rs, String columnName, Class<Date> clazz) throws SQLException {
    return Convert.toDate(rs.getObject(columnName));
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Date param) throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }

    if (param instanceof Timestamp)
      ps.setTimestamp(paramIndex, (Timestamp) param);
    else
      ps.setTimestamp(paramIndex, new Timestamp(param.getTime()));
  }

  @Override
  public int getSqlType() {
    return Types.TIMESTAMP;
  }
}
