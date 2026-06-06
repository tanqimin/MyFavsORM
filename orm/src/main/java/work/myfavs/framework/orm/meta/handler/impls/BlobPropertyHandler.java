package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;

import java.sql.*;

/**
 * {@link Blob} 类型的属性处理器.
 * <p>用于处理 Java {@link Blob} 类型与数据库 {@code BLOB} 类型之间的相互转换.</p>
 */
public class BlobPropertyHandler extends PropertyHandler<Blob> {

  /**
   * 从 ResultSet 中获取 {@link Blob} 类型的值.
   *
   * @param rs          ResultSet
   * @param columnIndex 列索引
   * @param clazz       目标类型
   * @return {@link Blob} 值
   * @throws SQLException SQL 异常
   */
  @Override
  public Blob convert(ResultSet rs, int columnIndex, Class<Blob> clazz) throws SQLException {

    return rs.getBlob(columnIndex);
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
  public void addParameter(PreparedStatement ps, int paramIndex, Blob param) throws SQLException {
    ps.setBlob(paramIndex, param);
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#BLOB}
   */
  @Override
  public int getSqlType() {
    return Types.BLOB;
  }
}
