package work.myfavs.framework.orm.meta.clause;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.common.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SQL 语句基类
 */
public abstract class Clause implements Serializable {

  @Getter
  @Setter
  protected StringBuilder sql = new StringBuilder();

  @Getter
  protected final List<Object> params = new ArrayList<>();

  public Clause() {
  }

  public Clause(@NonNull CharSequence sql) {
    this.sql.append(sql);
  }

  public Clause(@NonNull CharSequence sql, Object param) {
    this(sql);
    this.param(param);
  }

  public Clause(@NonNull CharSequence sql, Collection<?> params) {
    this(sql);
    this.params(params);
  }
  // endregion

  @Override
  public String toString() {
    return StringUtil.toStr(sql);
  }

  protected final Clause param(Object param) {
    this.params.add(param);
    return this;
  }

  protected final Clause params(Collection<?> params) {
    if (CollectionUtil.isNotEmpty(params))
      this.params.addAll(params);
    return this;
  }

  /**
   * 删除最后一位字符串
   *
   * @param str 删除的字符串
   * @return 删除后的字符串
   */
  public Clause deleteLast(String str) {
    if (isBlankSql() || StringUtil.isEmpty(str))
      return this;

    this.sql.deleteCharAt(this.sql.lastIndexOf(str));
    while (Character.isWhitespace(this.sql.charAt(this.sql.length() - 1)))
      this.sql.deleteCharAt(this.sql.length() - 1);
    return this;
  }

  /**
   * 当前语句和参数列表是否不为空
   *
   * @return 如果语句和参数列表都不为空，返回true
   */
  protected boolean notBlank() {
    return !isBlank();
  }

  /**
   * 当前语句和参数列表是否为空
   *
   * @return 如果语句和参数列表都为空，返回true
   */
  protected boolean isBlank() {
    return isBlankSql() && isEmptyParams();
  }

  protected boolean isEmptyParams() {
    return CollectionUtil.isEmpty(this.params);
  }

  protected boolean isBlankSql() {
    return StringUtil.isBlank(this.sql);
  }

  /**
   * 删除 sql 左边空格
   */
  protected Clause ltrim() {
    if (isBlankSql()) return this;
    while (Character.isWhitespace(this.sql.charAt(0))) {
      this.sql.deleteCharAt(0);
    }
    return this;
  }

  /**
   * 删除 sql 右边空格
   */
  protected Clause rtrim() {
    if (isBlankSql()) return this;
    while (Character.isWhitespace(this.sql.charAt(this.sql.length() - 1))) {
      this.sql.deleteCharAt(this.sql.length() - 1);
    }
    return this;
  }

  protected final Clause concat(CharSequence sql) {
    if (StringUtil.isEmpty(sql)) return this;

    this.sql.append(sql);
    return this;
  }

  /**
   * 拼接字符串，字符串之间使用空格分隔
   * <pre>
   *   如果 clause 为空 或 clause 为 {@link Constant#LINE_SEPARATOR} 则不处理。
   *   如果 {@link Clause#getSql()} 最后一个字符为空格，则删除最后一个字符
   *   如果 clause 第一个字符为空格，则删除第一个字符
   * </pre>
   *
   * @param clause 字符串单词
   */
  protected Clause concatWithSpace(CharSequence clause) {
    if (StringUtil.isEmpty(clause))
      return this;

    if (isBlankSql())
      return this.concat(clause);

    StringBuilder sqlSb = new StringBuilder(clause);
    if (Character.isWhitespace(sqlSb.charAt(0)))
      return this.rtrim().concat(sqlSb);

    return this.concat(Constant.SPACE).concat(clause);
  }
}
