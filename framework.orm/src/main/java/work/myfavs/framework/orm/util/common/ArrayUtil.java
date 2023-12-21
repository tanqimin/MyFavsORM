package work.myfavs.framework.orm.util.common;

public class ArrayUtil {

  public static boolean isArray(Object obj) {
    return null != obj && obj.getClass().isArray();
  }

  public static boolean isEmpty(int[] array) {
    return array == null || array.length == 0;
  }

  public static boolean isNotEmpty(int[] array) {
    return !isEmpty(array);
  }

  public static <T> boolean isEmpty(T[] array) {
    return array == null || array.length == 0;
  }

  public static <T> boolean isNotEmpty(T[] array) {
    return !isEmpty(array);
  }
}
