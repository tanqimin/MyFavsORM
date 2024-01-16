package work.myfavs.framework.orm.util.common;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 字符串工具类
 */
public class StringUtil {

  /**
   * 判断字符串是否为空
   *
   * @param str 字符串
   * @return 如果为空返回 {@code true}
   */
  public static boolean isEmpty(CharSequence str) {
    return str == null || str.length() == 0;
  }

  /**
   * 判断字符串是否不为空
   *
   * @param str 字符串
   * @return 如果不为空返回 {@code true}
   */
  public static boolean isNotEmpty(CharSequence str) {
    return !isEmpty(str);
  }

  /**
   * 判断字符串是否为空(包括空格、制表符等)
   *
   * @param str 字符串
   * @return 如果字符串为空或包含空格、制表符等返回 {@code true}
   */
  public static boolean isBlank(CharSequence str) {
    if (isEmpty(str)) {
      return true;
    }

    for (int i = 0; i < str.length(); i++) {
      if (!isBlankChar(str.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * 判断传入 char 是否为空字符串
   *
   * @param c Char
   * @return 如果为空或空格、制表符等返回 {@code true}
   */
  @SuppressWarnings("UnnecessaryUnicodeEscape")
  public static boolean isBlankChar(int c) {
    return Character.isWhitespace(c)
        || Character.isSpaceChar(c)
        || c == '\ufeff'
        || c == '\u202a'
        || c == '\u0000'
        // issue#I5UGSQ，Hangul Filler
        || c == '\u3164'
        // Braille Pattern Blank
        || c == '\u2800'
        // MONGOLIAN VOWEL SEPARATOR
        || c == '\u180e';
  }

  public static boolean equals(CharSequence str1, CharSequence str2) {
    return equals(str1, str2, false);
  }

  public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
    return equals(str1, str2, true);
  }

  public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
    if (null == str1) {
      // 只有两个都为null才判断相等
      return str2 == null;
    }
    if (null == str2) {
      // 字符串2空，字符串1非空，直接false
      return false;
    }

    if (ignoreCase) {
      return str1.toString().equalsIgnoreCase(str2.toString());
    } else {
      return str1.toString().contentEquals(str2);
    }
  }

  public static boolean equalsAny(CharSequence str1, CharSequence... strAny) {
    return Arrays.stream(strAny).anyMatch(str2 -> equals(str1, str2));
  }

  /**
   * 转换成下划线分割 StrUtil中不会单独对数字分割，如customFieldValue01 会处理为 custom_field_value01 当前方法对数字进行分割，会处理为
   * custom_field_value_01
   *
   * @param str 待处理的字符串
   * @return 下划线分割的字符串
   */
  public static String toUnderlineCase(CharSequence str) {
    final StringBuilder result        = new StringBuilder();
    boolean             lastUppercase = false;
    for (int i = 0; i < str.length(); i++) {
      char ch        = str.charAt(i);
      char lastEntry = i == 0 ? 'X' : result.charAt(result.length() - 1);
      if (ch == ' ' || ch == '_' || ch == '-' || ch == '.') {
        lastUppercase = false;
        if (lastEntry == '_') {
          continue;
        } else {
          ch = '_';
        }
      } else if (Character.isUpperCase(ch)) {
        ch = Character.toLowerCase(ch);
        // is start?
        if (i > 0) {
          if (lastUppercase) {
            // test if end of acronym
            if (i + 1 < str.length()) {
              char next = str.charAt(i + 1);
              if (!Character.isUpperCase(next) && Character.isAlphabetic(next)) {
                // end of acronym
                if (lastEntry != '_') {
                  result.append('_');
                }
              }
            }
          } else {
            // last was lowercase, insert _
            if (lastEntry != '_') {
              result.append('_');
            }
          }
        }
        lastUppercase = true;
      } else if (Character.isDigit(ch)) {
        if (i > 0) {
          if (!lastUppercase) {
            if (lastEntry != '_') {
              result.append('_');
            }
          }
        }
        lastUppercase = true;
      } else {
        if (Character.isDigit(lastEntry)) {
          result.append('_');
        }
        lastUppercase = false;
      }

      result.append(ch);
    }
    return result.toString();
  }

  /**
   * 把首字母转换为大写
   *
   * @param str 字符串
   * @return 首字母大写的字符串
   */
  public static String capitalize(String str) {
    if (!str.isEmpty()) {
      char firstChar = str.charAt(0);
      if (Character.isLowerCase(firstChar)) {
        return Character.toUpperCase(firstChar) + str.substring(1);
      }
    }
    return str;
  }

  /**
   * 把对象换为字符串
   *
   * @param obj 对象
   * @return 字符串
   */
  public static String toStr(Object obj) {
    if (null == obj) return null;
    return obj.toString();
  }

  /**
   * 替换字符串
   *
   * @param str         字符串
   * @param target      需被替换的字符串
   * @param replacement 替换的字符串
   * @return 替换后的字符串
   */
  public static String replace(String str, CharSequence target, CharSequence replacement) {
    if (isEmpty(str)) return str;

    return str.replace(target, replacement);
  }

  /**
   * 删除字符串前面的空格
   *
   * @param str 字符串
   * @return 删除前面空格后的字符串
   */
  public static String trimStart(CharSequence str) {
    return trim(str, -1);
  }

  /**
   * 删除字符串后面的空格
   *
   * @param str 字符串
   * @return 删除后面空格后的字符串
   */
  public static String trimEnd(CharSequence str) {
    return trim(str, 1);
  }

  /**
   * 删除字符串前后方的空格
   *
   * @param str 字符串
   * @return 删除前后方空格后的字符串
   */
  public static String trim(CharSequence str) {
    return trim(str, 0);
  }

  /**
   * 除去字符串头尾部的空白符，如果字符串是{@code null}，依然返回{@code null}。
   *
   * @param str  要处理的字符串
   * @param mode {@code -1}表示trimStart，{@code 0}表示trim全部， {@code 1}表示trimEnd
   * @return 除去指定字符后的的字符串，如果原字串为{@code null}，则返回{@code null}
   */
  public static String trim(CharSequence str, int mode) {
    return trim(str, mode, StringUtil::isBlankChar);
  }

  /**
   * 除去字符串头尾部的符合条件的字符串，如果字符串是{@code null}，依然返回{@code null}。
   *
   * @param str       要处理的字符串
   * @param mode      {@code -1}表示trimStart，{@code 0}表示trim全部， {@code 1}表示trimEnd
   * @param predicate 条件
   * @return 除去字符串头尾部的符合条件的字符串，如果字符串是{@code null}，依然返回{@code null}。
   */
  public static String trim(CharSequence str, int mode, Predicate<Character> predicate) {
    String result;
    if (str == null) {
      result = null;
    } else {
      int length = str.length();
      int start  = 0;
      int end    = length;// 扫描字符串头部
      if (mode <= 0) {
        while ((start < end) && (predicate.test(str.charAt(start)))) {
          start++;
        }
      }// 扫描字符串尾部
      if (mode >= 0) {
        while ((start < end) && (predicate.test(str.charAt(end - 1)))) {
          end--;
        }
      }
      if ((start > 0) || (end < length)) {
        result = str.toString().substring(start, end);
      } else {
        result = str.toString();
      }
    }

    return result;
  }

  /**
   * 删除字符串前缀
   *
   * @param str    字符串
   * @param prefix 前缀
   * @return 删除前缀后的字符串
   */
  public static String removePrefix(String str, String prefix) {
    if (isEmpty(str) || isEmpty(prefix)) {
      return str;
    }

    if (str.startsWith(prefix)) {
      return str.substring(prefix.length());
    }
    return str;
  }

  final static char UNDERLINE = '_';

  /**
   * 把_分割的字符串转为驼峰格式
   *
   * @param str _分割的字符串
   * @return 驼峰格式的字符串
   */
  public static String toCamelCase(String str) {
    if (isEmpty(str))
      return str;

    if (contains(str, UNDERLINE)) {
      final int           length    = str.length();
      final StringBuilder sb        = new StringBuilder(length);
      boolean             upperCase = false;
      for (int i = 0; i < length; i++) {
        char c = str.charAt(i);

        if (c == UNDERLINE) {
          upperCase = true;
        } else if (upperCase) {
          sb.append(Character.toUpperCase(c));
          upperCase = false;
        } else {
          sb.append(Character.toLowerCase(c));
        }
      }
      return sb.toString();
    } else {
      return str;
    }
  }

  /**
   * 判断字符串是否包含目标字符
   *
   * @param str        字符串
   * @param searchChar 目标字符
   * @return 如果包含返回 {@code true}
   */
  public static boolean contains(String str, char searchChar) {
    return indexOf(str, searchChar) > -1;
  }

  /**
   * 返回目标字符的位置
   *
   * @param str        字符串
   * @param searchChar 目标字符
   * @return 目标字符的位置
   */
  public static int indexOf(String str, char searchChar) {
    return indexOf(str, searchChar, 0);
  }

  /**
   * 返回目标字符的位置
   *
   * @param str        字符串
   * @param searchChar 目标字符
   * @param start      开始位置
   * @return 目标字符的位置
   */
  public static int indexOf(String str, char searchChar, int start) {
    return str.indexOf(searchChar, start);
  }

  public static int length(Object obj) {
    if (null == obj) return 0;
    return StringUtil.toStr(obj).length();
  }

  public static String leftPad(String str, String placeholder, int length) {
    if (str.length() > length)
      throw new IllegalArgumentException(String.format("参数 [%s] 的长度必须大于 %d ", str, length));
    String prefix = placeholder.repeat(length).concat(str);
    return prefix.concat(str).substring(prefix.length() - length, prefix.length());
  }
}
