package work.myfavs.framework.orm.util.lang;

import work.myfavs.framework.orm.util.common.StringUtil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Unicode<T extends Unicode<?>> implements java.io.Serializable, Comparable<T>, CharSequence {
  protected String internalString = "";


  public Unicode() {
  }

  public Unicode(String original) {
    Objects.requireNonNull(original);
    internalString = original;
  }

  public Unicode(T original) {
    Objects.requireNonNull(original);
    internalString = original.internalString;
  }

  public Unicode(char[] value) {
    internalString = new String(value);
  }

  public Unicode(char[] value, int offset, int count) {
    internalString = new String(value, offset, count);
  }

  public Unicode(StringBuffer buffer) {
    internalString = new String(buffer);
  }

  public Unicode(StringBuilder builder) {
    internalString = new String(builder);
  }

  abstract public T substring(int beginIndex);

  abstract public T substring(int beginIndex, int endIndex);

  abstract public T concat(String str);

  abstract public T concat(T str);

  abstract public T replace(char oldChar, char newChar);

  abstract public T replaceFirst(String regex, String replacement);

  abstract public T replaceFirst(String regex, T replacement);

  abstract public T replaceAll(String regex, String replacement);

  abstract public T replaceAll(String regex, T replacement);

  abstract public T replace(CharSequence target, CharSequence replacement);

  abstract public T[] split(String regex, int limit);

  abstract public T[] split(String regex);


  abstract public T toLowerCase(Locale locale);

  abstract public T toLowerCase();

  abstract public T toUpperCase(Locale locale);

  abstract public T toUpperCase();

  abstract public T trim();

  abstract public T strip();

  abstract public T stripLeading();

  abstract public T stripTrailing();

  abstract public Stream<T> lines();

  abstract public T repeat(int count);

  @Override
  public CharSequence subSequence(int start, int end) {
    return this.substring(start, end);
  }

  @Override
  public int length() {
    return internalString.length();
  }

  public boolean isEmpty() {
    return internalString.isEmpty();
  }

  @Override
  public char charAt(int index) {
    return internalString.charAt(index);
  }

  public String toString() {
    return internalString;
  }

  @Override
  public IntStream chars() {
    return internalString.chars();
  }

  @Override
  public IntStream codePoints() {
    return internalString.codePoints();
  }

  public byte[] getBytes(String charsetName)
      throws UnsupportedEncodingException {
    return internalString.getBytes(charsetName);
  }

  public byte[] getBytes(Charset charset) {
    return internalString.getBytes(charset);
  }

  public byte[] getBytes() {
    return internalString.getBytes();
  }

  public boolean equals(T anObject) {
    return StringUtil.equals(internalString, anObject.internalString);
  }

  public boolean contentEquals(StringBuffer sb) {
    return internalString.contentEquals(sb);
  }

  public boolean contentEquals(CharSequence cs) {
    return internalString.contentEquals(cs);
  }

  public boolean equalsIgnoreCase(T anotherNString) {
    String anotherString = null == anotherNString ? null : anotherNString.internalString;
    return internalString.equalsIgnoreCase(anotherString);
  }

  public boolean equalsIgnoreCase(String anotherString) {
    return internalString.equalsIgnoreCase(anotherString);
  }

  public int compareTo(String anotherString) {
    return internalString.compareTo(anotherString);
  }

  public int compareToIgnoreCase(T str) {
    Objects.requireNonNull(str);
    return internalString.compareToIgnoreCase(str.internalString);
  }

  public boolean regionMatches(int toffset, String other, int ooffset, int len) {
    return internalString.regionMatches(toffset, other, ooffset, len);
  }

  public boolean regionMatches(int toffset, T other, int ooffset, int len) {
    Objects.requireNonNull(other);
    return internalString.regionMatches(toffset, other.internalString, ooffset, len);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset,
                               String other, int ooffset, int len) {
    return internalString.regionMatches(ignoreCase, toffset, other, ooffset, len);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset,
                               T other, int ooffset, int len) {
    Objects.requireNonNull(other);
    return internalString.regionMatches(ignoreCase, toffset, other.internalString, ooffset, len);
  }

  public boolean startsWith(String prefix, int toffset) {
    return internalString.startsWith(prefix, toffset);
  }

  public boolean startsWith(T prefix, int toffset) {
    Objects.requireNonNull(prefix);
    return internalString.startsWith(prefix.internalString, toffset);
  }

  public boolean startsWith(String prefix) {
    return internalString.startsWith(prefix);
  }

  public boolean startsWith(T prefix) {
    Objects.requireNonNull(prefix);
    return internalString.startsWith(prefix.internalString);
  }

  public boolean endsWith(String suffix) {
    return internalString.endsWith(suffix);
  }

  public boolean endsWith(T suffix) {
    Objects.requireNonNull(suffix);
    return internalString.endsWith(suffix.internalString);
  }

  public int hashCode() {
    return internalString.hashCode();
  }


  public int indexOf(int ch) {
    return internalString.indexOf(ch);
  }

  public int indexOf(int ch, int fromIndex) {
    return internalString.indexOf(ch, fromIndex);
  }

  public int indexOf(String str) {
    return internalString.indexOf(str);
  }

  public int indexOf(T str) {
    Objects.requireNonNull(str);
    return internalString.indexOf(str.internalString);
  }

  /**
   * 获取字符在字符串中最后一次出现的索引
   *
   * @param ch 字符
   * @return 在字符串中最后一次出现的索引
   */
  public int lastIndexOf(int ch) {
    return internalString.lastIndexOf(ch);
  }

  /**
   * 从指定索引开始向前查找，获取字符在字符串中最后一次出现的索引
   *
   * @param ch        字符
   * @param fromIndex 指定索引
   * @return 在字符串中最后一次出现的索引
   */
  public int lastIndexOf(int ch, int fromIndex) {
    return internalString.lastIndexOf(ch, fromIndex);
  }

  /**
   * 获取字符在字符串中最后一次出现的索引
   *
   * @param str 字符
   * @return 在字符串中最后一次出现的索引
   */
  public int lastIndexOf(String str) {
    return internalString.lastIndexOf(str);
  }

  /**
   * 获取字符在字符串中最后一次出现的索引
   *
   * @param str 字符
   * @return 在字符串中最后一次出现的索引
   */
  public int lastIndexOf(T str) {
    Objects.requireNonNull(str);
    return internalString.lastIndexOf(str.internalString);
  }

  /**
   * 从指定索引开始向前查找，获取字符在字符串中最后一次出现的索引
   *
   * @param str       字符
   * @param fromIndex 指定索引
   * @return 在字符串中最后一次出现的索引
   */
  public int lastIndexOf(String str, int fromIndex) {
    return internalString.lastIndexOf(str, fromIndex);
  }

  /**
   * 从指定索引开始向前查找，获取字符在字符串中最后一次出现的索引
   *
   * @param str       字符
   * @param fromIndex 指定索引
   * @return 在字符串中最后一次出现的索引
   */
  public int lastIndexOf(T str, int fromIndex) {
    return internalString.lastIndexOf(str.internalString, fromIndex);
  }

  public boolean matches(String regex) {
    return internalString.matches(regex);
  }

  public boolean contains(CharSequence s) {
    return internalString.contains(s);
  }

  public boolean isBlank() {
    return internalString.isBlank();
  }

  public char[] toCharArray() {
    return internalString.toCharArray();
  }

  @Override
  public int compareTo(T otherUnicode) {
    Objects.requireNonNull(otherUnicode);
    return internalString.compareTo(otherUnicode.internalString);
  }
}
