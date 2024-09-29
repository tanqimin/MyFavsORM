package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;

public class LocalDateTimePropertyHandler extends AbstractTemporalAccessorPropertyHandler<LocalDateTime> {

  public LocalDateTimePropertyHandler() {
    super();
  }

  public LocalDateTimePropertyHandler(boolean usingEpochMilli) {
    super(usingEpochMilli);
  }

  @Override
  protected LocalDateTime fromInstant(Instant instant) {
    return LocalDateTime.ofInstant(instant, super.ZONE_ID);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, LocalDateTime param) throws SQLException {
    if (usingEpochMilli) {
      ps.setLong(paramIndex, param.atZone(super.ZONE_ID).toInstant().toEpochMilli());
      return;
    }
    ps.setTimestamp(paramIndex, Timestamp.valueOf(param));
  }

  @Override
  public int getSqlType() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }
}
