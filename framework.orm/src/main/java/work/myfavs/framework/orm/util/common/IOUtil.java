package work.myfavs.framework.orm.util.common;

import work.myfavs.framework.orm.util.exception.DBException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IOUtil {

  private static final int EOF                 = -1;
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  public static byte[] toByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[]                buffer = new byte[DEFAULT_BUFFER_SIZE];
    int                   n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
    }
    return output.toByteArray();
  }

  public static String toString(Reader input) throws IOException {
    StringBuilder output = new StringBuilder();
    char[]        buffer = new char[DEFAULT_BUFFER_SIZE];
    int           n;
    while (EOF != (n = input.read(buffer))) {
      output.append(buffer, 0, n);
    }
    return output.toString();
  }


  public static String read(String filePath) {
    try (InputStream is = IOUtil.class.getClassLoader().getResourceAsStream(filePath)) {
      StringBuilder content = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line).append(System.lineSeparator());
        }
      }

      return content.toString();
    } catch (IOException e) {
      throw new DBException(e, "Error read file in path: %s, message: %s", filePath, e.getMessage());
    }
  }
}
