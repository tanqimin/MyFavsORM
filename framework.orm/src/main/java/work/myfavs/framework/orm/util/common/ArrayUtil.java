package work.myfavs.framework.orm.util.common;

/**
 * 数组工具类
 */
public class ArrayUtil {

  /**
   * 是否数组
   *
   * @param obj 判断的对象
   * @return 数组返回 {@code true}
   */
  public static boolean isArray(Object obj) {
    return null != obj && obj.getClass().isArray();
  }

  /**
   * 数组是否为空
   *
   * @param array 数组对象
   * @param <T>   数组类型泛型
   * @return 数组为空返回 {@code true}
   */
  public static <T> boolean isEmpty(T[] array) {
    return array == null || array.length == 0;
  }

  /**
   * 数组是否不为空
   *
   * @param array 数组对象
   * @param <T>   数组类型泛型
   * @return 数组不为空返回 {@code true}
   */
  public static <T> boolean isNotEmpty(T[] array) {
    return !isEmpty(array);
  }
}
