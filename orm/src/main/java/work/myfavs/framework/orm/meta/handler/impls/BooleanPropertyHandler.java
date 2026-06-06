package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * {@code Boolean} 类型的属性处理器.
 * <p>用于处理 Java {@link Boolean} 类型与数据库 {@code BOOLEAN} 类型之间的相互转换.</p>
 */
public class BooleanPropertyHandler extends PropertyHandler<Boolean> {

  private boolean isPrimitive;

  /**
   * 构造 BooleanPropertyHandler.
   */
  public BooleanPropertyHandler() {}

  /**
   * 构造 BooleanPropertyHandler, 指定是否为原始类型.
   *
   * @param isPrimitive 是否为原始类型 {@code boolean}
   */
  public BooleanPropertyHandler(boolean isPrimitive) {

    this.isPrimitive = isPrimitive;
  }

  /**
   * 从 ResultSet 中获取 {@code Boolean} 类型的值.
   *
   * @param rs          ResultSet
   * @param columnIndex 列索引
   * @param clazz       目标类型
   * @return {@code Boolean} 值
   * @throws SQLException SQL 异常
   */
  @Override
  public Boolean convert(ResultSet rs, int columnIndex, Class<Boolean> clazz)
      throws SQLException {
    return ConvertUtil.toBool(rs.getObject(columnIndex), this.isPrimitive);
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
  public void addParameter(PreparedStatement ps, int paramIndex, Boolean param)
      throws SQLException {

    ps.setBoolean(paramIndex, param);
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#BOOLEAN}
   */
  @Override
  public int getSqlType() {
    return Types.BOOLEAN;
  }
}
