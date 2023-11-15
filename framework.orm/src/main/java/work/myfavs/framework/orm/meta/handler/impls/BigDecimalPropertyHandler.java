package work.myfavs.framework.orm.meta.handler.impls;

import cn.hutool.core.convert.Convert;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author tanqimin
 * @date 2016/1/29
 */
public class BigDecimalPropertyHandler extends NumberPropertyHandler<BigDecimal> {
  public BigDecimalPropertyHandler() {
  }

  @Override
  protected BigDecimal nullPrimitiveValue() {
    return null;
  }

  @Override
  protected BigDecimal convert(Object val) {
    return Convert.toBigDecimal(val);
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
