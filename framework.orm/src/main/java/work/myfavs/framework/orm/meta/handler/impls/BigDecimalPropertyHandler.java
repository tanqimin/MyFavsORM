package work.myfavs.framework.orm.meta.handler.impls;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BigDecimalPropertyHandler
    extends PropertyHandler<BigDecimal> {

  @Override
  public BigDecimal convert(ResultSet rs, String columnName, Class<BigDecimal> clazz)
      throws SQLException {

    BigDecimal i = rs.getBigDecimal(columnName);
    return rs.wasNull()
        ? null
        : i;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, BigDecimal param)
      throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, Types.DECIMAL);
      return;
    }
    ps.setBigDecimal(paramIndex, param);
  }

}
