package work.myfavs.framework.orm.util.lang;

import org.junit.Test;
import work.myfavs.framework.orm.util.common.Constant;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class NTextTest {

  private final static String TEST_STRING = "AB_CD_EFG_0123_456_789";

  @Test
  public void substring() {
    NText NText  = getNText();
    NText NText1 = NText.substring(3);
    assertEquals(NText1.toString(), "CD_EFG_0123_456_789");
  }

  private static NText getNText() {
    return new NText(TEST_STRING);
  }

  @Test
  public void testSubstring() {
    NText NText  = getNText();
    NText NText1 = NText.substring(3, 5);
    assertEquals(NText1.toString(), "CD");
  }

  @Test
  public void concat() {
    NText NText  = new NText();
    NText NText1 = NText.concat("AB").concat("_C");
    assertEquals(NText1.toString(), "AB_C");
  }

  @Test
  public void testConcat() {
    NText NText1 = new NText("A");
    NText NText2 = new NText("B");
    assertEquals(NText1.concat(NText2).toString(), "AB");
  }

  @Test
  public void replace() {
    NText nText = getNText();
    nText.replace('_', ',');
    assertEquals(nText.toString(), "AB,CD,EFG,0123,456,789");
  }

  @Test
  public void replaceFirst() {
    NText NText  = new NText("Hello, World");
    NText NText1 = NText.replaceFirst("World", "TAM");
    assertEquals(NText1.toString(), "Hello, TAM");
  }

  @Test
  public void testReplaceFirst() {
    NText NText  = new NText("Hello, World");
    NText NText1 = NText.replaceFirst("World", new NText("TAM"));
    assertEquals(NText1.toString(), "Hello, TAM");
  }

  @Test
  public void replaceAll() {
    NText NText  = new NText("鹅,鹅,鹅");
    NText NText1 = NText.replaceAll("鹅", "鸡");
    assertEquals(NText1.toString(), "鸡,鸡,鸡");
  }

  @Test
  public void testReplaceAll() {
    NText NText  = new NText("鹅,鹅,鹅");
    NText NText1 = NText.replaceAll("鹅", new NText("鸡"));
    assertEquals(NText1.toString(), "鸡,鸡,鸡");
  }

  @Test
  public void testReplace() {
    NText NText  = new NText("AA_BB_AA_CC");
    NText NText1 = NText.replace("AA", "DD");
    assertEquals(NText1.toString(), "DD_BB_DD_CC");
  }

  @Test
  public void split() {
    NText[] varchars = getNText().split("_");
    assertEquals(varchars.length, 6);
  }

  @Test
  public void testSplit() {
    NText[] varchars = getNText().split("_", 3);
    assertEquals(varchars.length, 3);
  }

  @Test
  public void toLowerCase() {
    NText nText = new NText("AA");
    assertEquals(nText.toLowerCase().toString(), "aa");
  }

  @Test
  public void testToLowerCase() {
    NText nText = new NText("AA");
    assertEquals(nText.toLowerCase(Locale.US).toString(), "aa");
  }

  @Test
  public void toUpperCase() {
    NText nText = new NText("aa");
    assertEquals(nText.toUpperCase().toString(), "AA");
  }

  @Test
  public void testToUpperCase() {
    NText nText = new NText("aa");
    assertEquals(nText.toUpperCase(Locale.US).toString(), "AA");
  }

  @Test
  public void trim() {
    NText nText = new NText("  HELLO  ").trim();
    assertEquals(nText.toString(), "HELLO");
  }

  @Test
  public void strip() {
    NText nText = new NText("  HELLO  ").strip();
    assertEquals(nText.toString(), "HELLO");
  }

  @Test
  public void stripLeading() {
    NText nText = new NText("  HELLO  ").stripLeading();
    assertEquals(nText.toString(), "HELLO  ");
  }

  @Test
  public void stripTrailing() {
    NText nText = new NText("  HELLO  ").stripTrailing();
    assertEquals(nText.toString(), "  HELLO");
  }

  @Test
  public void lines() {
    Stream<NText> lines = new NText("AA")
        .concat(Constant.LINE_SEPARATOR)
        .concat("BB")
        .lines();

    assertEquals(lines.count(), 2);
  }

  @Test
  public void repeat() {
    NText nText = new NText("A").repeat(3);
    assertEquals(nText.toString(), "AAA");
  }

  @Test
  public void join() {
    NText nText = NText.join(",", "A", "B");
    assertEquals("A,B", nText.toString());
  }

  @Test
  public void testJoin() {
    NText nText = NText.join(",", List.of("A", "B"));
    assertEquals("A,B", nText.toString());
  }

  @Test
  public void valueOf() {
    Object value = null;
    NText  nText = NText.valueOf(value);
    assertEquals(nText.toString(), "null");
  }

  @Test
  public void testValueOf() {
    NText nText = NText.valueOf(new char[]{'A', 'B'});
    assertEquals(nText.toString(), "AB");
  }

  @Test
  public void testValueOf1() {
    NText nText = NText.valueOf(new char[]{'A', 'B'}, 1, 1);
    assertEquals(nText.toString(), "B");
  }

  @Test
  public void testValueOf2() {
    NText NText1 = NText.valueOf(true);
    NText NText2 = NText.valueOf(false);
    assertEquals(NText1.toString(), "true");
    assertEquals(NText2.toString(), "false");
  }

  @Test
  public void testValueOf3() {
    NText nText = NText.valueOf('A');
    assertEquals(nText.toString(), "A");
  }

  @Test
  public void testValueOf4() {
    NText nText = NText.valueOf(1);
    assertEquals(nText.toString(), "1");
  }

  @Test
  public void testValueOf5() {
    NText nText = NText.valueOf(1L);
    assertEquals(nText.toString(), "1");
  }

  @Test
  public void testValueOf6() {
    NText nText = NText.valueOf(1F);
    assertEquals(nText.toString(), "1.0");
  }

  @Test
  public void testValueOf7() {
    NText nText = NText.valueOf(1D);
    assertEquals(nText.toString(), "1.0");
  }
}