package work.myfavs.framework.orm.util;

import java.sql.ResultSet;
import java.util.List;
import work.myfavs.framework.orm.util.exception.NotImplementedException;

/**
 * 数据库类型转换
 */
public class DBConvert {

  /**
   * 把ResultSet转换为指定类型的List
   *
   * @param clazz Class
   * @param rs    ResultSet
   * @param <T>   Class T
   *
   * @return List
   */
  public static <T> List<T> toList(Class<T> clazz, ResultSet rs) {

    throw new NotImplementedException();
  }

}
