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

  /**
   * 按顺序合并两个 int 数组，返回新数组。
   * 任意参数为 null 时当作空数组处理。
   *
   * @param a 第一个数组
   * @param b 第二个数组
   * @return 合并后的新数组
   */
  public static int[] concat(int[] a, int[] b) {
    if (a == null && b == null) return new int[0];
    if (a == null) return b.clone();
    if (b == null) return a.clone();
    int[] result = new int[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }
}
