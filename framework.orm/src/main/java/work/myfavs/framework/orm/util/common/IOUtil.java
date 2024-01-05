package work.myfavs.framework.orm.util.common;

import work.myfavs.framework.orm.util.exception.DBException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * IO工具类
 */
public class IOUtil {

  private static final int EOF                 = -1;
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  /**
   * 把InputStream转换为Byte数组
   *
   * @param input {@link InputStream}
   * @return Byte数组
   * @throws IOException 异常 {@link IOException}
   */
  public static byte[] toByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[]                buffer = new byte[DEFAULT_BUFFER_SIZE];
    int                   n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
    }
    return output.toByteArray();
  }

  /**
   * 把Reader输出字符串
   *
   * @param input Reader输入
   * @return 字符串
   * @throws IOException 异常 {@link IOException}
   */
  public static String toString(Reader input) throws IOException {
    StringBuilder output = new StringBuilder();
    char[]        buffer = new char[DEFAULT_BUFFER_SIZE];
    int           n;
    while (EOF != (n = input.read(buffer))) {
      output.append(buffer, 0, n);
    }
    return output.toString();
  }


  /**
   * 读取指定路径的文件内容到String中
   *
   * @param filePath 文件路径
   * @return 字符串
   */
  public static String read(String filePath) {
    try (InputStream is = IOUtil.class.getClassLoader().getResourceAsStream(filePath)) {
      StringBuilder content = new StringBuilder();
      if (null == is)
        return content.toString();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line).append(Constant.LINE_SEPARATOR);
        }
      }

      return content.toString();
    } catch (IOException e) {
      throw new DBException(e, "读取文件(%s)时发生异常: %s", filePath, e.getMessage());
    }
  }
}
