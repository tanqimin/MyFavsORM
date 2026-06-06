package work.myfavs.framework.orm.util.common;

import work.myfavs.framework.orm.util.exception.DBException;

public class SqlUtil {
  @SuppressWarnings("RegExpRedundantEscape")
  private static final String SQL_PATTERN = "[\\w\\p{IsIdeographic}\\ \\,\\.]+";

  /**
   * 检查传入语句是否存在注入风险
   *
   * @param sql SQL语句
   * @return SQL语句
   */
  public static String checkInjection(String sql) {
    if (StringUtil.isNotEmpty(sql) && !sql.matches(SQL_PATTERN)) {
      throw new DBException("参数 %s 中的内容存在注入风险, 请检查!", sql);
    }

    return sql;
  }

  public static String lTrim(CharSequence word) {
    if (null == word)
      return null;
    StringBuilder res = new StringBuilder(word);
    while (res.length() > 0 && Character.isWhitespace(res.charAt(0))) {
      res.deleteCharAt(0);
    }
    return res.toString();
  }

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
