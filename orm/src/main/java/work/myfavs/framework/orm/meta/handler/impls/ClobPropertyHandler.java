package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.*;

/**
 * {@link Clob} 类型的属性处理器.
 * <p>用于处理 Java {@link Clob} 类型与数据库 {@code CLOB} 类型之间的相互转换.</p>
 */
public class ClobPropertyHandler extends PropertyHandler<Clob> {

  /**
   * 从 ResultSet 中获取 {@link Clob} 类型的值.
   *
   * @param rs          ResultSet
   * @param columnIndex 列索引
   * @param clazz       目标类型
   * @return {@link Clob} 值
   * @throws SQLException SQL 异常
   */
  @Override
  public Clob convert(ResultSet rs, int columnIndex, Class<Clob> clazz) throws SQLException {

    return rs.getClob(columnIndex);
  }

  /**
   * 设置 PreparedStatement 参数.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQL 异常
   */
  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Clob param) throws SQLException {

    ps.setClob(paramIndex, param);
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#CLOB}
   */
  @Override
  public int getSqlType() {
    return Types.CLOB;
  }
}
