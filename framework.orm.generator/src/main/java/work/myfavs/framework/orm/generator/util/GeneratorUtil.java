package work.myfavs.framework.orm.generator.util;

import work.myfavs.framework.orm.util.common.StringUtil;

public class GeneratorUtil {

  /**
   * 把数据表名称转换为类名称
   *
   * @param tableName 数据表名称
   * @return 类名称
   */
  public static String toClass(String tableName) {

    String className = StringUtil.toCamelCase(tableName);
    return StringUtil.upperFirst(className);
  }

  /**
   * 把数据表名称转换为类名称，忽略前缀
   *
   * @param tableName 数据表名称
   * @param prefix 前缀
   * @return 类名称
   */
  public static String toClass(String tableName, String prefix) {

    final String tableWithoutPrefix = StringUtil.removePrefix(tableName, prefix);
    final String className = StringUtil.toCamelCase(tableWithoutPrefix);
    return StringUtil.upperFirst(className);
  }
}
