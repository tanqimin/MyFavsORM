package work.myfavs.framework.orm.util.lang;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * 对应数据库类型为 {@code NVarchar}，相关方法参考 {@link String} 同名类型
 */
public final class NVarchar extends Unicode<NVarchar> {


  public NVarchar() {
  }

  public NVarchar(String original) {
    super(original);
  }

  public NVarchar(NVarchar original) {
    super(original);
  }

  public NVarchar(char[] value) {
    super(value);
  }

  public NVarchar(char[] value, int offset, int count) {
    super(value, offset, count);
  }

  public NVarchar(StringBuffer buffer) {
    super(buffer);
  }

  public NVarchar(StringBuilder builder) {
    super(builder);
  }

  @Override
  public NVarchar substring(int beginIndex) {
    return new NVarchar(internalString.substring(beginIndex));
  }

  @Override
  public NVarchar substring(int beginIndex, int endIndex) {
    return new NVarchar(internalString.substring(beginIndex, endIndex));
  }

  @Override
  public NVarchar concat(String str) {
    return new NVarchar(internalString.concat(str));
  }

  @Override
  public NVarchar concat(NVarchar str) {
    return new NVarchar(internalString.concat(str.internalString));
  }

  @Override
  public NVarchar replace(char oldChar, char newChar) {
    internalString = internalString.replace(oldChar, newChar);
    return this;
  }

  @Override
  public NVarchar replaceFirst(String regex, String replacement) {
    internalString = internalString.replaceFirst(regex, replacement);
    return this;
  }

  @Override
  public NVarchar replaceFirst(String regex, NVarchar replacement) {
    internalString = internalString.replaceFirst(regex, replacement.internalString);
    return this;
  }

  @Override
  public NVarchar replaceAll(String regex, String replacement) {
    internalString = internalString.replaceAll(regex, replacement);
    return this;
  }

  @Override
  public NVarchar replaceAll(String regex, NVarchar replacement) {
    internalString = internalString.replaceAll(regex, replacement.internalString);
    return this;
  }

  @Override
  public NVarchar replace(CharSequence target, CharSequence replacement) {
    internalString = internalString.replace(target, replacement);
    return this;
  }

  @Override
  public NVarchar[] split(String regex, int limit) {
    String[]   split  = internalString.split(regex, limit);
    NVarchar[] result = new NVarchar[split.length];
    for (int i = 0; i < split.length; i++) {
      result[i] = new NVarchar(split[i]);
    }
    return result;
  }

  @Override
  public NVarchar[] split(String regex) {
    return split(regex, 0);
  }

  @Override
  public NVarchar toLowerCase(Locale locale) {
    return new NVarchar(internalString.toLowerCase(locale));
  }

  @Override
  public NVarchar toLowerCase() {
    return toLowerCase(Locale.getDefault());
  }

  @Override
  public NVarchar toUpperCase(Locale locale) {
    return new NVarchar(internalString.toUpperCase(locale));
  }

  @Override
  public NVarchar toUpperCase() {
    return toUpperCase(Locale.getDefault());
  }

  @Override
  public NVarchar trim() {
    return new NVarchar(internalString.trim());
  }

  @Override
  public NVarchar strip() {
    return new NVarchar(internalString.strip());
  }

  @Override
  public NVarchar stripLeading() {
    return new NVarchar(internalString.stripLeading());
  }

  @Override
  public NVarchar stripTrailing() {
    return new NVarchar(internalString.stripTrailing());
  }

  @Override
  public Stream<NVarchar> lines() {
    return internalString.lines().map(NVarchar::new);
  }

  @Override
  public NVarchar repeat(int count) {
    return new NVarchar(internalString.repeat(count));
  }

  public static NVarchar join(CharSequence delimiter, CharSequence... elements) {

    return new NVarchar(String.join(delimiter, elements));
  }

  public static NVarchar join(CharSequence delimiter,
                              Iterable<? extends CharSequence> elements) {
    return new NVarchar(String.join(delimiter, elements));
  }

  public static NVarchar valueOf(Object obj) {
    return (obj == null) ? new NVarchar("null") : new NVarchar(obj.toString());
  }

  public static NVarchar valueOf(char[] data) {
    return new NVarchar(data);
  }

  public static NVarchar valueOf(char[] data, int offset, int count) {
    return new NVarchar(data, offset, count);
  }

  public static NVarchar valueOf(boolean b) {
    return b ? new NVarchar("true") : new NVarchar("false");
  }

  public static NVarchar valueOf(char c) {
    return new NVarchar(String.valueOf(c));
  }

  public static NVarchar valueOf(int i) {
    return new NVarchar(String.valueOf(i));
  }

  public static NVarchar valueOf(long i) {
    return new NVarchar(String.valueOf(i));
  }

  public static NVarchar valueOf(float i) {
    return new NVarchar(String.valueOf(i));
  }

  public static NVarchar valueOf(double i) {
    return new NVarchar(String.valueOf(i));
  }
}
