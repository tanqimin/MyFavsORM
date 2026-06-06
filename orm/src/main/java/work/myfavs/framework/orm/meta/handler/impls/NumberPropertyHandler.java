package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class NumberPropertyHandler<T extends Number> extends PropertyHandler<T> {

  protected boolean isPrimitive;

  public NumberPropertyHandler() {}

  public NumberPropertyHandler(boolean isPrimitive) {
    this.isPrimitive = isPrimitive;
  }

  @Override
  public T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException {
    return ConvertUtil.toNumber(clazz, rs.getObject(columnIndex), this::convertNumber, this::convertString);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, T param) throws SQLException {
    setParameter(ps, paramIndex, param);
  }

  protected abstract T convertNumber(Number val);

  protected abstract T convertString(String val);

  protected abstract void setParameter(PreparedStatement ps, int paramIndex, T param) throws SQLException;

}
