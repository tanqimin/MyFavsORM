package work.myfavs.framework.orm.util;

public class StringUtil {

  public final static char   ESCAPE_CHAR       = '\\';
  public final static String PLACEHOLDER       = "{}";
  public final static char   PLACEHOLDER_START = '{';

  /**
   * 格式化文本, {} 表示占位符<br>
   * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
   * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
   * 例：<br>
   * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
   * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
   * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
   *
   * @param template 文本模板，被替换的部分用 {} 表示
   * @param args     参数值
   *
   * @return 格式化后的文本
   */
  public static String format(final String template, final Object... args) {

    if (template == null || template.isEmpty() || args == null || args.length == 0) {
      return template;
    }

    final int templateLength = template.length();

    StringBuilder sb         = new StringBuilder(templateLength + 50);
    int           currentPos = 0;
    int           placeholderIdx;

    for (int argIdx = 0;
         argIdx < args.length;
         argIdx++) {
      placeholderIdx = template.indexOf(PLACEHOLDER, currentPos);
      if (placeholderIdx == -1) {
        if (currentPos == 0) {
          return template;
        }

        sb.append(template, currentPos, templateLength);
        return sb.toString();
      }

      if (placeholderIdx > 0 && template.charAt(placeholderIdx - 1) == ESCAPE_CHAR) {
        if (placeholderIdx > 1 && template.charAt(placeholderIdx - 2) == ESCAPE_CHAR) {
          sb.append(template, currentPos, placeholderIdx - 1);
          sb.append(toStr(args[argIdx]));
          currentPos = placeholderIdx + 2;
        } else {
          argIdx--;
          sb.append(template, currentPos, placeholderIdx - 1);
          sb.append(PLACEHOLDER_START);
          currentPos = placeholderIdx + 1;
        }
      } else {
        sb.append(template, currentPos, placeholderIdx);
        sb.append(toStr(args[argIdx]));
        currentPos = placeholderIdx + 2;
      }
    }

    sb.append(template, currentPos, templateLength);

    return sb.toString();
  }


  public static String toStr(Object obj) {

    if (obj == null) {
      return null;
    }

    if (obj instanceof String) {
      return (String) obj;
    }

    return obj.toString();
  }

}
