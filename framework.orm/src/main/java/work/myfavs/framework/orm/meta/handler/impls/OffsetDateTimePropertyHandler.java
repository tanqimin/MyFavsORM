package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;

/**
 * TIMESTAMP WITH TIMEZONE
 *
 * @author tanqimin
 */
public class OffsetDateTimePropertyHandler extends PropertyHandler<OffsetDateTime> {

  @Override
  public OffsetDateTime convert(ResultSet rs, int columnIndex, Class<OffsetDateTime> clazz)
      throws SQLException {

    return rs.getObject(columnIndex, OffsetDateTime.class);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, OffsetDateTime param)
      throws SQLException {

    ps.setObject(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.TIMESTAMP_WITH_TIMEZONE;
  }
}
