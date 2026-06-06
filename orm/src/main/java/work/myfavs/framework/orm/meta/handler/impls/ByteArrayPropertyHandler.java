package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.common.IOUtil;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

/**
 * {@code byte[]} 类型的属性处理器.
 * <p>用于处理 Java {@code byte[]} 类型与数据库 {@code VARBINARY} 类型之间的相互转换.</p>
 */
public class ByteArrayPropertyHandler extends PropertyHandler<byte[]> {

  /**
   * 从 ResultSet 中获取 {@code byte[]} 类型的值, 支持 {@link Blob} 和直接的 {@code byte[]} 类型.
   *
   * @param rs          ResultSet
   * @param columnIndex 列索引
   * @param clazz       目标类型
   * @return {@code byte[]} 值
   * @throws SQLException SQL 异常
   */
  @Override
  public byte[] convert(ResultSet rs, int columnIndex, Class<byte[]> clazz) throws SQLException {
    Object val = rs.getObject(columnIndex);
    if (null == val) return null;

    if (val instanceof Blob) {
      Blob b = (Blob) val;
      try {
        try (InputStream stream = b.getBinaryStream()) {

          return IOUtil.toByteArray(stream);
        } finally {
          // ignore stream.close errors
          try {
            b.free();
          } catch (Throwable ignore) {
            // ignore blob.free errors
          }
        }
      } catch (SQLException | IOException e) {
        throw new InvalidDataAccessException(e, "转换 Blob 到 byte[] 时发生异常: %s", e.getMessage());
      }
    }

    if (val instanceof byte[]) {
      return (byte[]) val;
    }

    throw new InvalidDataAccessException("不能把 %s 类型转换成 byte[] 类型", val.getClass().getName());
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
  public void addParameter(PreparedStatement ps, int paramIndex, byte[] param) throws SQLException {

    ps.setBytes(paramIndex, param);
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#VARBINARY}
   */
  @Override
  public int getSqlType() {
    return Types.VARBINARY;
  }
}
