package work.myfavs.framework.orm.repository.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理接口
 * Created by tanqimin on 2016/1/29.
 */
public interface PropertyHandler {


  /**
   * 把ResultSet的值转换为指定类型对象
   *
   * @param rs         ResultSet
   * @param columnName 字段名
   * @param clazz      类型
   *
   * @return 对象
   *
   * @throws SQLException SQLException
   */
  abstract public Object convert(ResultSet rs, String columnName, Class<?> clazz)
      throws SQLException;

  /**
   * 把对象作为参数添加到Statement
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数ID
   * @param param      参数对象
   *
   * @throws SQLException SQLException
   */
  abstract public void addParameter(PreparedStatement ps, int paramIndex, Object param)
      throws SQLException;

}
