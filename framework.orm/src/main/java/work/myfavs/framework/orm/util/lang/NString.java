package work.myfavs.framework.orm.util.lang;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 等同于String，对应数据库类型为NVarchar，相关方法参考String同名类型
 */
public final class NString implements java.io.Serializable, Comparable<NString>, CharSequence {

  private String internalString = "";

  public NString() {
  }

  public NString(String original) {
    Objects.requireNonNull(original);
    internalString = original;
  }

  public NString(NString original) {
    Objects.requireNonNull(original);
    internalString = original.internalString;
  }

  public NString(char[] value) {
    internalString = new String(value);
  }

  public NString(char[] value, int offset, int count) {
    internalString = new String(value, offset, count);
  }

  public NString(StringBuffer buffer) {
    internalString = new String(buffer);
  }

  public NString(StringBuilder builder) {
    internalString = new String(builder);
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


  public boolean equals(Object anObject) {
    return internalString.equals(anObject);
  }

  public boolean contentEquals(StringBuffer sb) {
    return internalString.contentEquals(sb);
  }

  public boolean contentEquals(CharSequence cs) {
    return internalString.contentEquals(cs);
  }

  public boolean equalsIgnoreCase(NString anotherNString) {
    String anotherString = Objects.isNull(anotherNString) ? null : anotherNString.internalString;
    return internalString.equalsIgnoreCase(anotherString);
  }

  public boolean equalsIgnoreCase(String anotherString) {
    return internalString.equalsIgnoreCase(anotherString);
  }

  public int compareTo(String anotherString) {
    return internalString.compareTo(anotherString);
  }

  public int compareToIgnoreCase(NString str) {
    Objects.requireNonNull(str);
    return internalString.compareToIgnoreCase(str.internalString);
  }

  public boolean regionMatches(int toffset, String other, int ooffset, int len) {
    return internalString.regionMatches(toffset, other, ooffset, len);
  }

  public boolean regionMatches(int toffset, NString other, int ooffset, int len) {
    Objects.requireNonNull(other);
    return internalString.regionMatches(toffset, other.internalString, ooffset, len);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset,
                               String other, int ooffset, int len) {
    return internalString.regionMatches(ignoreCase, toffset, other, ooffset, len);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset,
                               NString other, int ooffset, int len) {
    Objects.requireNonNull(other);
    return internalString.regionMatches(ignoreCase, toffset, other.internalString, ooffset, len);
  }

  public boolean startsWith(String prefix, int toffset) {
    return internalString.startsWith(prefix, toffset);
  }

  public boolean startsWith(NString prefix, int toffset) {
    Objects.requireNonNull(prefix);
    return internalString.startsWith(prefix.internalString, toffset);
  }

  public boolean startsWith(String prefix) {
    return internalString.startsWith(prefix);
  }

  public boolean startsWith(NString prefix) {
    Objects.requireNonNull(prefix);
    return internalString.startsWith(prefix.internalString);
  }

  public boolean endsWith(String suffix) {
    return internalString.endsWith(suffix);
  }

  public boolean endsWith(NString suffix) {
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

  public int lastIndexOf(int ch) {
    return internalString.lastIndexOf(ch);
  }

  public int lastIndexOf(int ch, int fromIndex) {
    return internalString.lastIndexOf(ch, fromIndex);
  }

  public int indexOf(String str) {
    return internalString.indexOf(str);
  }

  public int indexOf(NString str) {
    Objects.requireNonNull(str);
    return internalString.indexOf(str.internalString);
  }

  public int lastIndexOf(String str) {
    return internalString.lastIndexOf(str);
  }

  public int lastIndexOf(NString str) {
    Objects.requireNonNull(str);
    return internalString.lastIndexOf(str.internalString);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return internalString.lastIndexOf(str, fromIndex);
  }

  public int lastIndexOf(NString str, int fromIndex) {
    return internalString.lastIndexOf(str.internalString, fromIndex);
  }

  public NString substring(int beginIndex) {
    return new NString(internalString.substring(beginIndex));
  }

  public NString substring(int beginIndex, int endIndex) {
    return new NString(internalString.substring(beginIndex, endIndex));
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return this.substring(start, end);
  }

  public NString concat(String str) {
    return new NString(internalString.concat(str));
  }

  public NString concat(NString str) {
    return new NString(this.concat(str));
  }

  public NString replace(char oldChar, char newChar) {
    internalString = internalString.replace(oldChar, newChar);
    return this;
  }

  public boolean matches(String regex) {
    return internalString.matches(regex);
  }

  public boolean contains(CharSequence s) {
    return internalString.contains(s);
  }

  public NString replaceFirst(String regex, String replacement) {
    internalString = internalString.replaceFirst(regex, replacement);
    return this;
  }

  public NString replaceFirst(String regex, NString replacement) {
    internalString = internalString.replaceFirst(regex, replacement.internalString);
    return this;
  }

  public NString replaceAll(String regex, String replacement) {
    internalString = internalString.replaceAll(regex, replacement);
    return this;
  }

  public NString replaceAll(String regex, NString replacement) {
    internalString = internalString.replaceAll(regex, replacement.internalString);
    return this;
  }

  public NString replace(CharSequence target, CharSequence replacement) {
    internalString = internalString.replace(target, replacement);
    return this;
  }

  public NString[] split(String regex, int limit) {
    String[]  split  = internalString.split(regex, limit);
    NString[] result = new NString[split.length];
    for (int i = 0; i < split.length; i++) {
      result[i] = new NString(split[i]);
    }
    return result;
  }

  public NString[] split(String regex) {
    return split(regex, 0);
  }

  public static NString join(CharSequence delimiter, CharSequence... elements) {

    return new NString(String.join(delimiter, elements));
  }

  public static NString join(CharSequence delimiter,
                             Iterable<? extends CharSequence> elements) {
    return new NString(String.join(delimiter, elements));
  }

  public NString toLowerCase(Locale locale) {
    return new NString(internalString.toLowerCase(locale));
  }

  public NString toLowerCase() {
    return toLowerCase(Locale.getDefault());
  }

  public NString toUpperCase(Locale locale) {
    return new NString(internalString.toUpperCase(locale));
  }

  public NString toUpperCase() {
    return toUpperCase(Locale.getDefault());
  }

  public NString trim() {
    return new NString(internalString.trim());
  }

  public NString strip() {
    return new NString(internalString.strip());
  }

  public NString stripLeading() {
    return new NString(internalString.stripLeading());
  }

  public NString stripTrailing() {
    return new NString(internalString.stripTrailing());
  }

  public Stream<NString> lines() {
    return internalString.lines().map(NString::new);
  }

  public boolean isBlank() {
    return internalString.isBlank();
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

  public char[] toCharArray() {
    return internalString.toCharArray();
  }

  public static NString valueOf(Object obj) {
    return (obj == null) ? new NString("null") : new NString(obj.toString());
  }

  public static NString valueOf(char[] data) {
    return new NString(data);
  }

  public static NString valueOf(char[] data, int offset, int count) {
    return new NString(data, offset, count);
  }

  public static NString valueOf(boolean b) {
    return b ? new NString("true") : new NString("false");
  }

  public static NString valueOf(char c) {
    return new NString(String.valueOf(c));
  }

  public static NString valueOf(int i) {
    return new NString(String.valueOf(i));
  }

  public static NString valueOf(long i) {
    return new NString(String.valueOf(i));
  }

  public static NString valueOf(float i) {
    return new NString(String.valueOf(i));
  }

  public static NString valueOf(double i) {
    return new NString(String.valueOf(i));
  }

  public native NString intern();

  public NString repeat(int count) {
    return new NString(internalString.repeat(count));
  }


  @Override
  public int compareTo(NString otherNString) {
    Objects.requireNonNull(otherNString);
    return internalString.compareTo(otherNString.internalString);
  }
}
