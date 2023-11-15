package work.myfavs.framework.orm.meta.handler.impls;

import cn.hutool.core.convert.Convert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class BytePropertyHandler extends NumberPropertyHandler<Byte> {
  public BytePropertyHandler() {
  }

  public BytePropertyHandler(boolean isPrimitive) {
    super(isPrimitive);
  }

  @Override
  protected Byte nullPrimitiveValue() {
    return Convert.toByte(0);
  }

  @Override
  protected Byte convert(Object val) {
    return Convert.toByte(val);
  }

  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, Byte param) throws SQLException {
    ps.setByte(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.VARBINARY;
  }
}
