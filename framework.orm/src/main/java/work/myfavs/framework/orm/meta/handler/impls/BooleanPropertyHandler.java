package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BooleanPropertyHandler extends PropertyHandler<Boolean> {

  private boolean isPrimitive;

  public BooleanPropertyHandler() {}

  public BooleanPropertyHandler(boolean isPrimitive) {

    this.isPrimitive = isPrimitive;
  }

  @Override
  public Boolean convert(ResultSet rs, int columnIndex, Class<Boolean> clazz)
      throws SQLException {
    return ConvertUtil.toBool(rs.getObject(columnIndex), this.isPrimitive);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Boolean param)
      throws SQLException {

    ps.setBoolean(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.BOOLEAN;
  }
}
