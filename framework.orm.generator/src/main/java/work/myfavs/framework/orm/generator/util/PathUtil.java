package work.myfavs.framework.orm.generator.util;

public class PathUtil {

  public static String toPath(String packageName) {

    return packageName.replace(".", "/");
  }

  public static String append(String path1, String path2) {

    StringBuilder ret = new StringBuilder();
    if (path1.endsWith("/")) {
      ret.append(path1, 0, path1.length() - 1);
    } else {
      ret.append(path1);
    }

    if (path2.startsWith("/")) {
      ret.append(path2);
    } else {
      ret.append("/").append(path2);
    }
    return ret.toString();
  }

}
