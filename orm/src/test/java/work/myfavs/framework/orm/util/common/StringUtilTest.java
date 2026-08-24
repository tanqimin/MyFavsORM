package work.myfavs.framework.orm.util.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilTest {
  private final static String S_NULL  = null;
  private final static String S_EMPTY = "";
  private final static String S_SPACE = " ";
  private final static String S_TAB   = "  ";

  @Test
  public void isEmpty() {
    assertTrue(StringUtil.isEmpty(S_NULL));
    assertTrue(StringUtil.isEmpty(S_EMPTY));
    assertFalse(StringUtil.isEmpty(S_SPACE));
    assertFalse(StringUtil.isEmpty(S_TAB));
  }

  @Test
  public void isNotEmpty() {
    assertFalse(StringUtil.isNotEmpty(S_NULL));
    assertFalse(StringUtil.isNotEmpty(S_EMPTY));
    assertTrue(StringUtil.isNotEmpty(S_SPACE));
    assertTrue(StringUtil.isNotEmpty(S_TAB));
  }

  @Test
  public void isBlank() {
    assertTrue(StringUtil.isBlank(S_NULL));
    assertTrue(StringUtil.isBlank(S_EMPTY));
    assertTrue(StringUtil.isBlank(S_SPACE));
    assertTrue(StringUtil.isBlank(S_TAB));
  }

  @Test
  public void isBlankChar() {
    assertTrue(StringUtil.isBlankChar(' '));
    assertTrue(StringUtil.isBlankChar(' '));
  }

  @Test
  public void testEquals() {
    assertTrue(StringUtil.equals("ABC", "ABC"));
    assertFalse(StringUtil.equals("ABC", "abc"));
  }

  @Test
  public void equalsIgnoreCase() {
    assertTrue(StringUtil.equalsIgnoreCase("ABC", "ABC"));
    assertTrue(StringUtil.equalsIgnoreCase("ABC", "abc"));
  }

  @Test
  public void testEquals1() {
    assertTrue(StringUtil.equals("ABC", "ABC", true));
    assertTrue(StringUtil.equals("ABC", "abc", true));
    assertFalse(StringUtil.equals("ABC", "abc", false));
  }

  @Test
  public void toUnderlineCase() {
    String s1 = "colorName";
    String s2 = "customProperty01";
    assertEquals("color_name", StringUtil.toUnderlineCase(s1));
    assertEquals("custom_property_01", StringUtil.toUnderlineCase(s2));
  }

  @Test
  public void upperFirst() {
    String s1 = "username";
    String s2 = "customProperty";
    assertEquals("Username", StringUtil.capitalize(s1));
    assertEquals("CustomProperty", StringUtil.capitalize(s2));
  }

  @Test
  public void toStr() {
    StringBuilder s2 = new StringBuilder("ABC");
    assertNull(StringUtil.toStr(null));
    assertEquals("ABC", StringUtil.toStr(s2));
  }

  @Test
  public void replace() {
    String s1 = "ABC_ABC";
    assertEquals("a_a", StringUtil.replace(s1, "ABC", "a"));
  }

  @Test
  public void trimStart() {
    assertEquals("ABC", StringUtil.trimStart(S_SPACE.concat("ABC")));
  }

  @Test
  public void trimEnd() {
    assertEquals("ABC", StringUtil.trimEnd("ABC".concat(S_SPACE)));
  }

  @Test
  public void trim() {
    assertEquals(StringUtil.trim(S_SPACE.concat("ABC").concat(S_TAB)), "ABC");
  }

  @Test
  public void testTrim() {
    assertEquals("ABC", StringUtil.trim(S_SPACE.concat("ABC"), -1));
    assertEquals("ABC", StringUtil.trim("ABC".concat(S_SPACE), 1));
    assertEquals(StringUtil.trim(S_SPACE.concat("ABC").concat(S_TAB), 0), "ABC");
  }

  @Test
  public void testTrim1() {
    assertEquals("ABC", StringUtil.trim("$".concat("ABC"), -1, c -> c.equals('$')));
  }

  @Test
  public void removePrefix() {
    String prefix = "TB_";
    assertEquals("USER", StringUtil.removePrefix("TB_USER", prefix));
  }

  @Test
  public void toCamelCase() {
    String s1 = "user_name";
    assertEquals("userName", StringUtil.toCamelCase(s1));
  }

  @Test
  public void contains() {
    String s1 = "$ABC";
    assertTrue(StringUtil.contains(s1, '$'));
  }

  @Test
  public void indexOf() {
    String s1 = "$ABC";
    assertEquals(2, StringUtil.indexOf(s1, 'B'));
  }

  @Test
  public void testIndexOf() {
    String s1 = "$ABC";
    assertEquals(-1, StringUtil.indexOf(s1, 'B', 3));
  }

  @Test
  public void leftPad() {
    String pad = StringUtil.leftPad("001", "0", 10);
    assertEquals(pad, "0000000001");
  }

  @Test(expected = IllegalArgumentException.class)
  public void leftPad_ShouldThrow_WhenStringTooLong() {
    StringUtil.leftPad("too long", "0", 3);
  }

  @Test
  public void equals_WithBothNull_ShouldReturnTrue() {
    assertTrue(StringUtil.equals(null, null));
  }

  @Test
  public void equals_WithFirstNull_ShouldReturnFalse() {
    assertFalse(StringUtil.equals(null, "ABC"));
  }

  @Test
  public void equals_WithSecondNull_ShouldReturnFalse() {
    assertFalse(StringUtil.equals("ABC", null));
  }

  @Test
  public void equalsIgnoreCase_WithBothNull_ShouldReturnTrue() {
    assertTrue(StringUtil.equalsIgnoreCase(null, null));
  }

  @Test
  public void equalsIgnoreCase_WithFirstNull_ShouldReturnFalse() {
    assertFalse(StringUtil.equalsIgnoreCase(null, "ABC"));
  }

  @Test
  public void equalsAny_WithMatchingChar_ShouldReturnTrue() {
    assertTrue(StringUtil.equalsAny('a', 'a', 'b', 'c'));
  }

  @Test
  public void equalsAny_WithNoMatch_ShouldReturnFalse() {
    assertFalse(StringUtil.equalsAny('x', 'a', 'b', 'c'));
  }

  @Test
  public void equalsAny_WithNullArray_ShouldReturnFalse() {
    assertFalse(StringUtil.equalsAny('a', (char[]) null));
  }

  @Test
  public void onlyMatchAny_ShouldReturnTrue_WhenAllCharsMatch() {
    assertTrue(StringUtil.onlyMatchAny("abc", 'a', 'b', 'c'));
  }

  @Test
  public void onlyMatchAny_ShouldReturnFalse_WhenAnyCharDoesNotMatch() {
    assertFalse(StringUtil.onlyMatchAny("abc", 'a', 'b'));
  }

  @Test
  public void capitalize_WithEmptyString_ShouldReturnEmpty() {
    assertEquals("", StringUtil.capitalize(""));
  }

  @Test
  public void capitalize_WithAlreadyUpperCase_ShouldNotChange() {
    assertEquals("ABC", StringUtil.capitalize("ABC"));
  }

  @Test
  public void capitalize_WithSingleCharacter_ShouldUpperCase() {
    assertEquals("A", StringUtil.capitalize("a"));
  }

  @Test
  public void toStr_WithNonNull_ShouldReturnToString() {
    assertEquals("123", StringUtil.toStr(123));
  }

  @Test
  public void split_ShouldReturnTrimmedParts() {
    java.util.List<String> parts = StringUtil.split("a, b ,c", ",");
    assertEquals(3, parts.size());
    assertEquals("a", parts.get(0));
    assertEquals("b", parts.get(1));
    assertEquals("c", parts.get(2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void split_WithNull_ShouldThrow() {
    StringUtil.split(null, ",");
  }

  @Test
  public void length_ShouldReturnStringLength() {
    assertEquals(3, StringUtil.length("ABC"));
  }

  @Test
  public void length_WithNull_ShouldReturnZero() {
    assertEquals(0, StringUtil.length(null));
  }

  @Test
  public void toUnderlineCase_WithSpaces_ShouldConvertToUnderscores() {
    assertEquals("hello_world", StringUtil.toUnderlineCase("hello world"));
  }

  @Test
  public void toUnderlineCase_WithHyphens_ShouldConvertToUnderscores() {
    assertEquals("hello_world", StringUtil.toUnderlineCase("hello-world"));
  }

  @Test
  public void toUnderlineCase_WithDots_ShouldConvertToUnderscores() {
    assertEquals("hello_world", StringUtil.toUnderlineCase("hello.world"));
  }

  @Test
  public void toUnderlineCase_WithConsecutiveSeparators_ShouldCollapse() {
    assertEquals("hello_world", StringUtil.toUnderlineCase("hello___world"));
  }

  @Test
  public void removePrefix_ShouldReturnOriginal_WhenPrefixDoesNotMatch() {
    assertEquals("TB_USER", StringUtil.removePrefix("TB_USER", "TA_"));
  }

  @Test
  public void removePrefix_WithEmptyPrefix_ShouldReturnOriginal() {
    assertEquals("TB_USER", StringUtil.removePrefix("TB_USER", ""));
  }

  @Test
  public void removePrefix_WithEmptyString_ShouldReturnEmpty() {
    assertEquals("", StringUtil.removePrefix("", "TB_"));
  }
}