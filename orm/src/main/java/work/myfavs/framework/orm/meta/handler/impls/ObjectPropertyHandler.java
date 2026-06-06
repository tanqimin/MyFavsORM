package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * {@code Object} 类型的属性处理器.
 * <p>处理任意 Java 类型与 JDBC 类型之间的通用转换.</p>
 */
public class ObjectPropertyHandler extends PropertyHandler<Object> {

  /**
   * 从 ResultSet 中读取值并转换为 {@code Object}.
   *
   * @param rs          ResultSet
   * @param columnIndex 字段索引
   * @param clazz       目标类型
   * @return Object 值
   * @throws SQLException SQLException
   */
  @Override
  public Object convert(ResultSet rs, int columnIndex, Class<Object> clazz) throws SQLException {

    return rs.getObject(columnIndex);
  }

  /**
   * 将参数添加到 PreparedStatement.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQLException
   */
  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {

    ps.setObject(paramIndex, param);
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
