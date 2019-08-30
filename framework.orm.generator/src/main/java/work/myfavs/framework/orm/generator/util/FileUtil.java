package work.myfavs.framework.orm.generator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtil {

  public final static int IGNORE    = 0;
  public final static int OVERWRITE = 1;

  /**
   * 传入文件名以及字符串, 将字符串信息保存到文件中
   *
   * @param fileName 文件名
   * @param content  文件内容
   * @param oper     操作类型，0：如果文件存在，则跳过，1：如果文件存在，则覆盖；
   */
  public static void TextToFile(final String fileName, final String content, int oper)
      throws IOException {

    Path filePath = Paths.get(fileName);
    Path dictPath = Paths.get(fileName.substring(0, fileName.lastIndexOf("/")));

    if (!Files.exists(dictPath)) {
      Files.createDirectories(dictPath);
    }

    if (Files.exists(filePath)) {
      if (oper == IGNORE) {
        return;
      } else {
        Files.delete(filePath);
      }
    }

    Files.createFile(filePath);
    Files.write(filePath, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
  }

}
