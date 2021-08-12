package work.myfavs.framework.orm.util.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 属性文件配置
 *
 * @author tanqimin
 */
public class Prop {

  private static Properties properties = null;

  public static Properties load() {
    if (properties == null) {
      properties = new Properties();
      final File file = FileUtil.file("myfavs.properties");
      if (file.exists() == false) {
        throw new DBException("Can not found file named myfavs.properties in the class path.");
      }
      try {
        properties.load(new FileInputStream(file));
      } catch (IOException e) {
        throw new DBException(e);
      }
    }
    return properties;
  }

  public static String getStr(String key) {
    return load().getProperty(key, null);
  }

  public static Integer getInt(String key) {
    final String str = getStr(key);
    return StrUtil.isEmpty(str) ? null : Integer.parseInt(str);
  }

  public static Boolean getBoolean(String key) {
    final String str = getStr(key);
    return StrUtil.isEmpty(str) ? null : Boolean.parseBoolean(str);
  }
}
