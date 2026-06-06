package work.myfavs.framework.orm.meta.handler.impls;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author tanqimin
 */
public class BigDecimalPropertyHandler extends NumberPropertyHandler<BigDecimal> {
  public BigDecimalPropertyHandler() {
  }

  @Override
  protected BigDecimal convertNumber(Number val) {
    if (val instanceof BigDecimal) {
      return (BigDecimal) val;
    } else {
      return BigDecimal.valueOf(val.doubleValue());
    }
  }

  @Override
  protected BigDecimal convertString(String val) {
    return BigDecimal.valueOf(Double.parseDouble(val));
  }

  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, BigDecimal param) throws SQLException {
    ps.setBigDecimal(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.DECIMAL;
  }
}
