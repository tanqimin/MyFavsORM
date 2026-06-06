package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.convert.ConvertUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数字类型的抽象属性处理器.
 * <p>提供数字类型的通用转换逻辑, 子类需实现具体的数字类型转换.</p>
 *
 * @param <T> 数字类型
 */
public abstract class NumberPropertyHandler<T extends Number> extends PropertyHandler<T> {

  protected boolean isPrimitive;

  /**
   * 构造 {@code NumberPropertyHandler} 实例.
   */
  public NumberPropertyHandler() {}

  /**
   * 构造 {@code NumberPropertyHandler} 实例.
   *
   * @param isPrimitive 是否基本类型
   */
  public NumberPropertyHandler(boolean isPrimitive) {
    this.isPrimitive = isPrimitive;
  }

  /**
   * 从 ResultSet 中读取值并转换为数字类型.
   *
   * @param rs          ResultSet
   * @param columnIndex 字段索引
   * @param clazz       目标类型
   * @return 转换后的数字对象
   * @throws SQLException SQLException
   */
  @Override
  public T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException {
    return ConvertUtil.toNumber(clazz, rs.getObject(columnIndex), this::convertNumber, this::convertString);
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
  public void addParameter(PreparedStatement ps, int paramIndex, T param) throws SQLException {
    setParameter(ps, paramIndex, param);
  }

  /**
   * 将 {@code Number} 转换为目标数字类型.
   *
   * @param val Number 值
   * @return 目标数字类型值
   */
  protected abstract T convertNumber(Number val);

  /**
   * 将 {@code String} 转换为目标数字类型.
   *
   * @param val 字符串值
   * @return 目标数字类型值
   */
  protected abstract T convertString(String val);

  /**
   * 设置 JDBC 参数.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQLException
   */
  protected abstract void setParameter(PreparedStatement ps, int paramIndex, T param) throws SQLException;

}
