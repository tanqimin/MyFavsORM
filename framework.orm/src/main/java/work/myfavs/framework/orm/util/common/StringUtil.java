package work.myfavs.framework.orm.util.common;

import java.util.Objects;
import java.util.function.Predicate;

public class StringUtil {

  public static boolean isEmpty(CharSequence str) {
    return str == null || str.length() == 0;
  }

  public static boolean isNotEmpty(CharSequence str) {
    return !isEmpty(str);
  }

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

  public static String trim(String str) {
    if (isEmpty(str)) return str;

    return str.trim();
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

  public static String upperFirst(String str) {
    if (!str.isEmpty()) {
      char firstChar = str.charAt(0);
      if (Character.isLowerCase(firstChar)) {
        return Character.toUpperCase(firstChar) + str.substring(1);
      }
    }
    return str;
  }

  public static String toString(Object obj) {
    if (Objects.isNull(obj)) return null;
    return obj.toString();
  }

  public static boolean isBlankIfStr(Object obj) {
    if (null == obj) {
      return true;
    } else if (obj instanceof CharSequence) {
      return isBlank((CharSequence) obj);
    }
    return false;
  }

  public static String replace(String str, CharSequence target, CharSequence replacement) {
    if (isEmpty(str)) return str;

    return str.replace(target, replacement);
  }

  public static String trimStart(CharSequence str) {
    return trim(str, -1);
  }

  public static String trimEnd(CharSequence str) {
    return trim(str, 1);
  }

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

  public static boolean contains(String str, char searchChar) {
    return indexOf(str, searchChar) > -1;
  }

  public static int indexOf(String str, char searchChar) {
    return indexOf(str, searchChar, 0);
  }

  public static int indexOf(String str, char searchChar, int start) {
    return str.indexOf(searchChar, start);
  }
}
