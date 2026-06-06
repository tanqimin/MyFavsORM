package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * {@code Byte} 类型的属性处理器.
 * <p>用于处理 Java {@link Byte} 类型与数据库 {@code VARBINARY} 类型之间的相互转换.</p>
 */
public class BytePropertyHandler extends NumberPropertyHandler<Byte> {

  /**
   * 构造 BytePropertyHandler.
   */
  public BytePropertyHandler() {
  }

  /**
   * 构造 BytePropertyHandler, 指定是否为原始类型.
   *
   * @param isPrimitive 是否为原始类型 {@code byte}
   */
  public BytePropertyHandler(boolean isPrimitive) {
    super(isPrimitive);
  }

  @Override
  protected Byte convertNumber(Number val) {
    return val.byteValue();
  }

  @Override
  protected Byte convertString(String val) {
    return Byte.parseByte(val);
  }

  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, Byte param) throws SQLException {
    ps.setByte(paramIndex, param);
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#VARBINARY}
   */
  @Override
  public int getSqlType() {
    return Types.VARBINARY;
  }
}
