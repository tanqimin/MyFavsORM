package work.myfavs.framework.orm.generator.util;

import lombok.NonNull;
import work.myfavs.framework.orm.util.StringUtil;

public class GeneratorUtil {

  /**
   * 把数据表名称转换为类名称
   *
   * @param tableName 数据表名称
   *
   * @return 类名称
   */
  public static String toClass(@NonNull String tableName) {

    if (tableName == null) {
      return null;
    }
    String className = StringUtil.camel(tableName);
    return StringUtil.upperCaseFirst(className);
  }

  /**
   * 把数据表名称转换为类名称，忽略前缀
   *
   * @param tableName 数据表名称
   * @param prefix    前缀
   *
   * @return 类名称
   */
  public static String toClass(@NonNull String tableName, @NonNull String prefix) {

    if (StringUtil.startWith(tableName, prefix)) {
      return toClass(tableName.substring(prefix.length() - 1));
    }

    return toClass(tableName);
  }

}
