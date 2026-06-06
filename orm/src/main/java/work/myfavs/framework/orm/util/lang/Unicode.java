package work.myfavs.framework.orm.util.lang;

import java.util.Objects;

/**
 * Unicode 字符串类型，对应数据库中的 Unicode 字段（如 NChar、NVarchar、NText）.
 */
public class Unicode implements java.io.Serializable {

  protected String content;

  protected String sqlType;

  /**
   * 构造 Unicode 实例.
   */
  public Unicode() {
  }

  /**
   * 构造 Unicode 实例.
   *
   * @param content 字符串内容
   */
  public Unicode(String content) {
    this.content = content;
  }

  /**
   * 构造 Unicode 实例.
   *
   * @param content 字符串内容
   * @param sqlType 数据库类型
   */
  public Unicode(String content, String sqlType) {
    this.content = content;
    this.sqlType = sqlType;
  }

  /**
   * 获取字符串内容.
   *
   * @return 字符串内容
   */
  public String getContent() {
    return content;
  }

  /**
   * 设置字符串内容.
   *
   * @param content 字符串内容
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * 获取数据库类型.
   *
   * @return 数据库类型
   */
  public String getSqlType() {
    return sqlType;
  }

  /**
   * 设置数据库类型.
   *
   * @param sqlType 数据库类型
   */
  public void setSqlType(String sqlType) {
    this.sqlType = sqlType;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Unicode unicode = (Unicode) o;
    return Objects.equals(content, unicode.content) && Objects.equals(sqlType, unicode.sqlType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, sqlType);
  }

  @Override
  public String toString() {
    return content;
  }
}
