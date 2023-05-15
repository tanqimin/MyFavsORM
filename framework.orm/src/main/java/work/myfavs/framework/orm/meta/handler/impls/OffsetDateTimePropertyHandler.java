package work.myfavs.framework.orm.meta.handler.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

/**
 * TIMESTAMP WITH TIMEZONE
 *
 * @author tanqimin
 */
public class OffsetDateTimePropertyHandler extends PropertyHandler<OffsetDateTime> {

  @Override
  public OffsetDateTime convert(ResultSet rs, String columnName, Class<OffsetDateTime> clazz)
      throws SQLException {
    final OffsetDateTime offsetDateTime = rs.getObject(columnName, OffsetDateTime.class);
    if (rs.wasNull()) {
      return null;
    }
    return offsetDateTime;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, OffsetDateTime param)
      throws SQLException {
    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }
    ps.setObject(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }
}
