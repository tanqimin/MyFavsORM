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
}