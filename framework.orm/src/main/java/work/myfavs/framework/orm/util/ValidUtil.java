package work.myfavs.framework.orm.util;

import java.util.Collection;

public class ValidUtil {

  public static void notNull(Object obj) {

    if (obj == null) {
      throw new IllegalArgumentException();
    }
  }

  public static void notEmpty(Object[] objects) {

    if (objects == null || objects.length == 0) {
      throw new IllegalArgumentException();
    }
  }

  public static <T> void notEmpty(Collection<T> collection) {

    if (collection == null || collection.isEmpty()) {
      throw new IllegalArgumentException();
    }
  }

  public static void notEmpty(String str) {

    if (str == null || str.isEmpty()) {
      throw new IllegalArgumentException();
    }
  }

}
