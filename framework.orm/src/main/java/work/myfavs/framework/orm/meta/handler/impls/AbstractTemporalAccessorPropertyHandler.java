package work.myfavs.framework.orm.meta.handler.impls;

import lombok.RequiredArgsConstructor;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.exception.DBException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

@RequiredArgsConstructor
public abstract class AbstractTemporalAccessorPropertyHandler<T extends TemporalAccessor> extends PropertyHandler<T> {

  protected final ZoneId ZONE_ID = ZoneId.systemDefault();

  protected final boolean usingEpochMilli;

  public AbstractTemporalAccessorPropertyHandler() {
    this.usingEpochMilli = false;
  }

  @Override
  public T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException {
    Object val = rs.getObject(columnIndex);
    if (null == val) return null;

    if (usingEpochMilli) {
      long    timestamp = ((Number) val).longValue();
      Instant instant   = Instant.ofEpochMilli(timestamp);
      return fromInstant(instant);
    }

    if (val instanceof LocalDateTime) {
      Instant instant = ((LocalDateTime) val).atZone(ZONE_ID).toInstant();
      return fromInstant(instant);
    } else if (val instanceof Date) {
      Instant instant = ((Date) val).toInstant();
      return fromInstant(instant);
    } else if (val instanceof OffsetDateTime) {
      Instant instant = ((OffsetDateTime) val).atZoneSameInstant(ZONE_ID).toInstant();
      return fromInstant(instant);
    } else {
      throw new DBException("不能把类型 %s 转换为 %s. ", val.getClass(), clazz.getName());
    }

  }

  protected abstract T fromInstant(Instant instant);
}
