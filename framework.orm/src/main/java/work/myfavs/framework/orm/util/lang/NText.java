package work.myfavs.framework.orm.util.lang;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * 对应数据库类型为 {@code NText}，相关方法参考 {@link String} 同名类型
 */
public final class NText extends Unicode<NText> {


  public NText() {
  }

  public NText(String original) {
    super(original);
  }

  public NText(NText original) {
    super(original);
  }

  public NText(char[] value) {
    super(value);
  }

  public NText(char[] value, int offset, int count) {
    super(value, offset, count);
  }

  public NText(StringBuffer buffer) {
    super(buffer);
  }

  public NText(StringBuilder builder) {
    super(builder);
  }

  @Override
  public NText substring(int beginIndex) {
    return new NText(internalString.substring(beginIndex));
  }

  @Override
  public NText substring(int beginIndex, int endIndex) {
    return new NText(internalString.substring(beginIndex, endIndex));
  }

  @Override
  public NText concat(String str) {
    return new NText(internalString.concat(str));
  }

  @Override
  public NText concat(NText str) {
    return new NText(internalString.concat(str.internalString));
  }

  @Override
  public NText replace(char oldChar, char newChar) {
    internalString = internalString.replace(oldChar, newChar);
    return this;
  }

  @Override
  public NText replaceFirst(String regex, String replacement) {
    internalString = internalString.replaceFirst(regex, replacement);
    return this;
  }

  @Override
  public NText replaceFirst(String regex, NText replacement) {
    internalString = internalString.replaceFirst(regex, replacement.internalString);
    return this;
  }

  @Override
  public NText replaceAll(String regex, String replacement) {
    internalString = internalString.replaceAll(regex, replacement);
    return this;
  }

  @Override
  public NText replaceAll(String regex, NText replacement) {
    internalString = internalString.replaceAll(regex, replacement.internalString);
    return this;
  }

  @Override
  public NText replace(CharSequence target, CharSequence replacement) {
    internalString = internalString.replace(target, replacement);
    return this;
  }

  @Override
  public NText[] split(String regex, int limit) {
    String[] split  = internalString.split(regex, limit);
    NText[]  result = new NText[split.length];
    for (int i = 0; i < split.length; i++) {
      result[i] = new NText(split[i]);
    }
    return result;
  }

  @Override
  public NText[] split(String regex) {
    return split(regex, 0);
  }

  @Override
  public NText toLowerCase(Locale locale) {
    return new NText(internalString.toLowerCase(locale));
  }

  @Override
  public NText toLowerCase() {
    return toLowerCase(Locale.getDefault());
  }

  @Override
  public NText toUpperCase(Locale locale) {
    return new NText(internalString.toUpperCase(locale));
  }

  @Override
  public NText toUpperCase() {
    return toUpperCase(Locale.getDefault());
  }

  @Override
  public NText trim() {
    return new NText(internalString.trim());
  }

  @Override
  public NText strip() {
    return new NText(internalString.strip());
  }

  @Override
  public NText stripLeading() {
    return new NText(internalString.stripLeading());
  }

  @Override
  public NText stripTrailing() {
    return new NText(internalString.stripTrailing());
  }

  @Override
  public Stream<NText> lines() {
    return internalString.lines().map(NText::new);
  }

  @Override
  public NText repeat(int count) {
    return new NText(internalString.repeat(count));
  }

  public static NText join(CharSequence delimiter, CharSequence... elements) {

    return new NText(String.join(delimiter, elements));
  }

  public static NText join(CharSequence delimiter,
                           Iterable<? extends CharSequence> elements) {
    return new NText(String.join(delimiter, elements));
  }

  public static NText valueOf(Object obj) {
    return (obj == null) ? new NText("null") : new NText(obj.toString());
  }

  public static NText valueOf(char[] data) {
    return new NText(data);
  }

  public static NText valueOf(char[] data, int offset, int count) {
    return new NText(data, offset, count);
  }

  public static NText valueOf(boolean b) {
    return b ? new NText("true") : new NText("false");
  }

  public static NText valueOf(char c) {
    return new NText(String.valueOf(c));
  }

  public static NText valueOf(int i) {
    return new NText(String.valueOf(i));
  }

  public static NText valueOf(long i) {
    return new NText(String.valueOf(i));
  }

  public static NText valueOf(float i) {
    return new NText(String.valueOf(i));
  }

  public static NText valueOf(double i) {
    return new NText(String.valueOf(i));
  }
}
