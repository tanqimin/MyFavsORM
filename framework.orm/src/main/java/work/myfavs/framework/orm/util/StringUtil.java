package work.myfavs.framework.orm.util;

public class StringUtil {

  public static String toStr(Object obj) {

    if (obj == null) {
      return null;
    }
    return obj.toString();
  }

}
