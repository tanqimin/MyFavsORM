package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * Created by tanqimin on 2016/1/29.
 */
public class DatePropertyHandler extends AbstractDatePropertyHandler<Date> {

  private final boolean useTimestamp;

  public DatePropertyHandler() {
    this(false);
  }

  public DatePropertyHandler(boolean useTimestamp) {
    this.useTimestamp = useTimestamp;
  }

  @Override
  protected Date fromMilliseconds(long millisecond) {
    return new Date(millisecond);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Date param) throws SQLException {

    if (useTimestamp) {
      ps.setLong(paramIndex, param.getTime());
      return;
    }

    if (param instanceof Timestamp) {
      ps.setTimestamp(paramIndex, (Timestamp) param);
      return;
    }

    ps.setTimestamp(paramIndex, new Timestamp(param.getTime()));

  }

  @Override
  public int getSqlType() {

    return useTimestamp ? Types.BIGINT : Types.TIMESTAMP;
  }
}
