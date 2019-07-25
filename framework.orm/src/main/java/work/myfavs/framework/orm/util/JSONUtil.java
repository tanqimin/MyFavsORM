package work.myfavs.framework.orm.util;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;

/**
 * JSON工具类
 */
public class JSONUtil {

  /**
   * 对象转JSON字符串
   *
   * @param obj 对象
   *
   * @return JSON
   */
  public static String toStr(Object obj) {

    return JsonStream.serialize(obj);
  }

  /**
   * JSON字符串转对象
   *
   * @param tClass  对象类型
   * @param jsonStr JSON字符串
   * @param <T>     对象类型泛型
   *
   * @return 对象
   */
  public static <T> T toObject(Class<T> tClass, String jsonStr) {

    return JsonIterator.deserialize(jsonStr).as(tClass);
  }

}
