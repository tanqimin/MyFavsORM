package work.myfavs.framework.orm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

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

  public static boolean nullOrEmpty(Object obj) {

    return obj == null || toStr(obj).isEmpty();
  }

  public static boolean eq(String str1, String str2) {

    return eq(str1, str2, false);
  }

  public static boolean eq(String str1, String str2, boolean ignoreCase) {

    if (str1 == null && str2 == null) {
      return true;
    }

    if (str1 != null && str2 != null) {
      return ignoreCase
          ? str1.equalsIgnoreCase(str2)
          : str1.equals(str2);
    }

    return false;
  }

  /**
   * 下划线转驼峰
   *
   * @param str 下划线字符串
   *
   * @return 驼峰字符串
   */
  public static String camel(String str) {
    //利用正则删除下划线，把下划线后一位改成大写
    Pattern      pattern = Pattern.compile("_(\\w)");
    Matcher      matcher = pattern.matcher(str);
    StringBuffer sb      = new StringBuffer(str);
    if (matcher.find()) {
      sb = new StringBuffer();
      //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
      //正则之前的字符和被替换的字符
      matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
      //把之后的也添加到StringBuffer对象里
      matcher.appendTail(sb);
    } else {
      return sb.toString();
    }
    return camel(sb.toString());
  }


  /**
   * 驼峰转下划线
   *
   * @param str 驼峰字符串
   *
   * @return 下划线字符串
   */
  public static String underline(String str) {

    Pattern      pattern = Pattern.compile("[A-Z]");
    Matcher      matcher = pattern.matcher(str);
    StringBuffer sb      = new StringBuffer(str);
    if (matcher.find()) {
      sb = new StringBuffer();
      //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
      //正则之前的字符和被替换的字符
      matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
      //把之后的也添加到StringBuffer对象里
      matcher.appendTail(sb);
    } else {
      return sb.toString();
    }
    return underline(sb.toString());
  }


  /**
   * 首字母大写
   *
   * @param str 字符串
   *
   * @return 首字母大写字符串
   */
  public static String upperCaseFirst(String str) {

    char[] chars = str.toCharArray();
    if (chars[0] >= 'a' && chars[0] <= 'z') {
      chars[0] = (char) (chars[0] - 32);
    }
    return new String(chars);
  }

  public static boolean startWith(@NonNull String str, @NonNull String prefix) {

    return str.toLowerCase().startsWith(prefix.toLowerCase());
  }

}
