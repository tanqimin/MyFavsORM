package work.myfavs.framework.orm.generator.util;

import cn.hutool.core.util.StrUtil;

public class PathUtil {

  public static String toPath(String packageName) {

    return StrUtil.replace(packageName, ".", "/");
  }

}
