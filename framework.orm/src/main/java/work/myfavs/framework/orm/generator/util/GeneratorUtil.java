package work.myfavs.framework.orm.generator.util;

import work.myfavs.framework.orm.util.StringUtil;

public class GeneratorUtil {

  /**
   * 把数据表名称转换为类名称
   *
   * @param tableName 数据表名称
   *
   * @return 类名称
   */
  public static String toClass(String tableName) {

    if (tableName == null) {
      return null;
    }
    String className = StringUtil.camel(tableName);
    return StringUtil.upperCaseFirst(className);
  }

}
