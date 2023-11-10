package work.myfavs.framework.orm.util;

import cn.hutool.core.util.StrUtil;

public class StringUtil extends StrUtil {

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
}
