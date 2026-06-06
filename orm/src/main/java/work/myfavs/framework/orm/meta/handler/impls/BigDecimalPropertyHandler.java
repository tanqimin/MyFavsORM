package work.myfavs.framework.orm.meta.handler.impls;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * {@code BigDecimal} 类型的属性处理器.
 * <p>用于处理 Java {@link BigDecimal} 类型与数据库 {@code DECIMAL} 类型之间的相互转换.</p>
 */
public class BigDecimalPropertyHandler extends NumberPropertyHandler<BigDecimal> {

  /**
   * 构造 BigDecimalPropertyHandler.
   */
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

  /**
   * 将字符串转换为 {@code BigDecimal}.
   *
   * @param val 字符串值
   * @return {@code BigDecimal} 值
   */
  @Override
  protected BigDecimal convertString(String val) {
    return BigDecimal.valueOf(Double.parseDouble(val));
  }

  /**
   * 设置 PreparedStatement 参数.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQL 异常
   */
  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, BigDecimal param) throws SQLException {
    ps.setBigDecimal(paramIndex, param);
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#DECIMAL}
   */
  @Override
  public int getSqlType() {
    return Types.DECIMAL;
  }
}
