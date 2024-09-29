package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * TIMESTAMP WITH TIMEZONE
 *
 * @author tanqimin
 */
public class OffsetDateTimePropertyHandler extends AbstractTemporalAccessorPropertyHandler<OffsetDateTime> {

  public OffsetDateTimePropertyHandler() {
    super();
  }

  public OffsetDateTimePropertyHandler(boolean usingEpochMilli) {
    super(usingEpochMilli);
  }

  @Override
  protected OffsetDateTime fromInstant(Instant instant) {
    return OffsetDateTime.ofInstant(instant, super.ZONE_ID);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, OffsetDateTime param)
      throws SQLException {
    if (usingEpochMilli) {
      ps.setLong(paramIndex, param.atZoneSameInstant(super.ZONE_ID).toInstant().toEpochMilli());
      return;
    }
    ps.setTimestamp(paramIndex, Timestamp.valueOf(param.toLocalDateTime()));
  }

  @Override
  public int getSqlType() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }
}
