package work.myfavs.framework.orm.generator.util;

import cn.hutool.core.util.StrUtil;

public class GeneratorUtil {

  /**
   * 把数据表名称转换为类名称
   *
   * @param tableName 数据表名称
   * @return 类名称
   */
  public static String toClass(String tableName) {

    String className = StrUtil.toCamelCase(tableName);
    return StrUtil.upperFirst(className);
  }

  /**
   * 把数据表名称转换为类名称，忽略前缀
   *
   * @param tableName 数据表名称
   * @param prefix 前缀
   * @return 类名称
   */
  public static String toClass(String tableName, String prefix) {

    final String tableWithoutPrefix = StrUtil.removePrefix(tableName, prefix);
    final String className = StrUtil.toCamelCase(tableWithoutPrefix);
    return StrUtil.upperFirst(className);
  }
}
