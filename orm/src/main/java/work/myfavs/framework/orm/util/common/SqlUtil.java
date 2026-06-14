package work.myfavs.framework.orm.util.common;

import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

/**
 * SQL 工具类，提供 SQL 注入检查和空白字符修剪功能
 */
public class SqlUtil {
  @SuppressWarnings("RegExpRedundantEscape")
  private static final String SQL_PATTERN = "[\\w\\p{IsIdeographic}\\ \\,\\.\\$`]+";

  /**
   * 检查传入语句是否存在注入风险
   *
   * @param sql SQL语句
   * @return SQL语句
   */
  public static String checkInjection(String sql) {
    if (StringUtil.isNotEmpty(sql) && !sql.matches(SQL_PATTERN)) {
      throw new InvalidDataAccessException("参数 %s 中的内容存在注入风险, 请检查!", sql);
    }

    return sql;
  }

  /**
   * 去除字符串左侧的空白字符
   *
   * @param word 待处理的字符串序列
   * @return 去除左侧空白后的字符串，若输入为 {@code null} 则返回 {@code null}
   */
  public static String lTrim(CharSequence word) {
    if (null == word)
      return null;
    StringBuilder res = new StringBuilder(word);
    while (res.length() > 0 && Character.isWhitespace(res.charAt(0))) {
      res.deleteCharAt(0);
    }
    return res.toString();
  }

  /**
   * 去除字符串右侧的空白字符
   *
   * @param word 待处理的字符串序列
   * @return 去除右侧空白后的字符串，若输入为 {@code null} 则返回 {@code null}
   */
  public static String rTrim(CharSequence word) {
    if (null == word)
      return null;
    StringBuilder res = new StringBuilder(word);
    while (res.length() > 0 && Character.isWhitespace(res.charAt(res.length() - 1))) {
      res.deleteCharAt(res.length() - 1);
    }
    return res.toString();
  }
}
