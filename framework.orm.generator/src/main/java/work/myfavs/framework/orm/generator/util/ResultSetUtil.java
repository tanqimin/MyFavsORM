package work.myfavs.framework.orm.generator.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetUtil {

  /**
   * 获取字符串列的值
   *
   * @param rs          ResultSet
   * @param columnLabel 字段名
   *
   * @return 值
   *
   * @throws SQLException SQLException
   */
  public static String getString(ResultSet rs, String columnLabel)
      throws SQLException {

    String res = rs.getString(columnLabel);
    return rs.wasNull()
        ? null
        : res;
  }

  /**
   * 获取数值列的值
   *
   * @param rs          ResultSet
   * @param columnLabel 字段名
   *
   * @return 值
   *
   * @throws SQLException SQLException
   */
  public static Integer getInt(ResultSet rs, String columnLabel)
      throws SQLException {

    int res = rs.getInt(columnLabel);
    return rs.wasNull()
        ? null
        : res;
  }

  /**
   * 获取布尔列的值
   *
   * @param rs          ResultSet
   * @param columnLabel 字段名
   *
   * @return 值
   *
   * @throws SQLException SQLException
   */
  public static Boolean getBoolean(ResultSet rs, String columnLabel)
      throws SQLException {

    boolean res = rs.getBoolean(columnLabel);
    return rs.wasNull()
        ? null
        : res;
  }

}
