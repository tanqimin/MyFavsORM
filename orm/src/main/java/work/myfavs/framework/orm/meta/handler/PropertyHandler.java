package work.myfavs.framework.orm.meta.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理器的抽象基类，负责 Java 类型与 JDBC 类型之间的双向转换。
 * <p>每个 {@link PropertyHandler} 负责一种特定 Java 类型与数据库列的相互转换。
 * 框架内置 23 种默认处理器（参见 {@link work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory#registerDefault()}），
 * 用户也可通过 {@link work.myfavs.framework.orm.DBTemplate.Builder#mapping(java.util.function.Consumer)} 注册自定义处理器。</p>
 *
 * @param <T> 处理器处理的 Java 类型
 * @see PropertyHandlerFactory
 * @see work.myfavs.framework.orm.meta.schema.Attribute
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

  /**
   * 获取当前类型的 JDBC SQL 类型代码.
   *
   * @return SQL 类型代码
   */
  public abstract int getSqlType();
}
