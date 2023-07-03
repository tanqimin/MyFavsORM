package work.myfavs.framework.orm.util;

import cn.hutool.json.JSONConfig;

/** JSON工具类 */
public class JsonUtil {
  private static final JSONConfig JSON_CONFIG =
      JSONConfig.create().setIgnoreNullValue(false).setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  /**
   * 将对象解析为JSON字符串
   *
   * @param obj 对象
   * @return JSON字符串
   */
  public static String toJsonStr(Object obj) {
    return cn.hutool.json.JSONUtil.toJsonStr(obj, JSON_CONFIG);
  }
}
