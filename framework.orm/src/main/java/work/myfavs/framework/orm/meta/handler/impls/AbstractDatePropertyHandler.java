package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.exception.DBException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public abstract class AbstractDatePropertyHandler<T extends Date> extends PropertyHandler<T> {

  protected abstract T fromMilliseconds(long millisecond);

  @SuppressWarnings("unchecked")
  @Override
  public T convert(ResultSet rs, String columnName, Class<T> clazz) throws SQLException {
    Object val = rs.getObject(columnName);
    if (Objects.isNull(val)) return null;
    if (clazz.isInstance(val)) return (T) val;

    if (val instanceof Date)
      return fromMilliseconds(((Date) val).getTime());

    if (val instanceof Number)
      return fromMilliseconds(((Number) val).longValue());

    throw new DBException("Cannot convert type {} to java.util.Date", val.getClass());
  }
}
