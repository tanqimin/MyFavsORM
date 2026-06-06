package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.common.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 枚举类型的属性处理器.
 * <p>用于处理 Java 枚举类型与数据库 {@code VARCHAR} 类型之间的相互转换, 以枚举名称进行存储.</p>
 */
public class EnumPropertyHandler extends PropertyHandler<Object> {

  /**
   * 从 ResultSet 中获取枚举类型的值, 以字符串形式匹配枚举名称.
   *
   * @param rs          ResultSet
   * @param columnIndex 列索引
   * @param clazz       枚举类型
   * @return 枚举值, 若无法匹配则返回 {@code null}
   * @throws SQLException SQL 异常
   */
  @Override
  @SuppressWarnings("unchecked")
  public Object convert(ResultSet rs, int columnIndex, @SuppressWarnings("rawtypes") Class clazz) throws SQLException {

    Object val = rs.getObject(columnIndex);
    if(null == val) return null;

    try {
      return Enum.valueOf(clazz, StringUtil.toStr(val));
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * 设置 PreparedStatement 参数, 将枚举值转换为字符串存储.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      枚举值
   * @throws SQLException SQL 异常
   */
  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {

    ps.setString(paramIndex, StringUtil.toStr(param));
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#VARCHAR}
   */
  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }
}
