package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

/**
 * {@code UUID} 类型的属性处理器.
 * <p>处理 Java {@code UUID} 类型与 JDBC {@code VARCHAR} 类型之间的相互转换.</p>
 */
public class UUIDPropertyHandler extends PropertyHandler<UUID> {

  /**
   * 从 ResultSet 中读取值并转换为 {@code UUID}.
   *
   * @param rs          ResultSet
   * @param columnIndex 字段索引
   * @param clazz       目标类型
   * @return UUID 对象
   * @throws SQLException SQLException
   */
  @Override
  public UUID convert(ResultSet rs, int columnIndex, Class<UUID> clazz) throws SQLException {

    return ConvertUtil.toUUID(rs.getObject(columnIndex));
  }

  /**
   * 将 {@code UUID} 参数添加到 PreparedStatement.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQLException
   */
  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, UUID param) throws SQLException {

    ps.setString(paramIndex, param.toString());
  }

  /**
   * 获取 SQL 类型代码.
   *
   * @return {@code Types.VARCHAR}
   */
  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }
}
