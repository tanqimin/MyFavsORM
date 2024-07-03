package work.myfavs.framework.orm.meta.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理接口 此处为了兼容枚举类型
 *
 * <p>Created by tanqimin on 2016/1/29.
 */
public abstract class PropertyHandler<T> {

  /**
   * 把ResultSet的值转换为指定类型对象
   *
   * @param rs          ResultSet
   * @param columnIndex 字段Index
   * @param clazz       类型
   * @return 对象
   * @throws SQLException SQLException
   */
  public abstract T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException;

  /**
   * 把对象作为参数添加到Statement
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数ID
   * @param param      参数对象
   * @throws SQLException SQLException
   */
  public abstract void addParameter(PreparedStatement ps, int paramIndex, T param)
      throws SQLException;

  public abstract int getSqlType();
}
