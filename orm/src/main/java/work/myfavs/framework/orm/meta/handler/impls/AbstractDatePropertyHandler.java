package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public abstract class AbstractDatePropertyHandler<T extends Date> extends PropertyHandler<T> {

  protected abstract T fromMilliseconds(long millisecond);

  protected final boolean usingEpochMilli;

  public AbstractDatePropertyHandler() {
    this.usingEpochMilli = false;
  }

  public AbstractDatePropertyHandler(boolean usingEpochMilli) {
    this.usingEpochMilli = usingEpochMilli;
  }

  @Override
  public T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException {
    Object val = rs.getObject(columnIndex);
    return ConvertUtil.toDate(clazz, val, this::fromMilliseconds);
  }
}
