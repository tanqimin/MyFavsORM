package work.myfavs.framework.orm.meta.handler.impls;

import cn.hutool.core.convert.Convert;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class NumberPropertyHandler<T extends Number> extends PropertyHandler<T> {

  protected boolean isPrimitive;

  public NumberPropertyHandler() {}

  public NumberPropertyHandler(boolean isPrimitive) {
    this.isPrimitive = isPrimitive;
  }

  @Override
  public T convert(ResultSet rs, String columnName, Class<T> clazz) throws SQLException {
    Object val = rs.getObject(columnName);
    if (Objects.isNull(val))
      return isPrimitive ? nullPrimitiveValue() : null;
    return convert(val);
  }

  protected abstract T nullPrimitiveValue();


  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, T param) throws SQLException {
    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }
    setParameter(ps, paramIndex, param);
  }


  protected abstract T convert(Object val);

  protected abstract void setParameter(PreparedStatement ps, int paramIndex, T param) throws SQLException;

}
