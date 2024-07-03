package work.myfavs.framework.orm.generator.util;

import java.util.Objects;

public class PathUtil {

  public static String toPath(String packageName) {

    Objects.requireNonNull(packageName);
    return packageName.replace('.', '/');
  }
}
