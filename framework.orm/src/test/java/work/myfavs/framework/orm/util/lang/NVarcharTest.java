package work.myfavs.framework.orm.util.lang;

import org.junit.Test;
import work.myfavs.framework.orm.util.common.Constant;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class NVarcharTest {

  private final static String TEST_STRING = "AB_CD_EFG_0123_456_789";

  @Test
  public void substring() {
    NVarchar nVarchar  = getnVarchar();
    NVarchar nVarchar1 = nVarchar.substring(3);
    assertEquals(nVarchar1.toString(), "CD_EFG_0123_456_789");
  }

  private static NVarchar getnVarchar() {
    return new NVarchar(TEST_STRING);
  }

  @Test
  public void testSubstring() {
    NVarchar nVarchar  = getnVarchar();
    NVarchar nVarchar1 = nVarchar.substring(3, 5);
    assertEquals(nVarchar1.toString(), "CD");
  }

  @Test
  public void concat() {
    NVarchar nVarchar  = new NVarchar();
    NVarchar nVarchar1 = nVarchar.concat("AB").concat("_C");
    assertEquals(nVarchar1.toString(), "AB_C");
  }

  @Test
  public void testConcat() {
    NVarchar nVarchar1 = new NVarchar("A");
    NVarchar nVarchar2 = new NVarchar("B");
    assertEquals(nVarchar1.concat(nVarchar2).toString(), "AB");
  }

  @Test
  public void replace() {
    NVarchar nVarchar = getnVarchar();
    nVarchar.replace('_', ',');
    assertEquals(nVarchar.toString(), "AB,CD,EFG,0123,456,789");
  }

  @Test
  public void replaceFirst() {
    NVarchar nVarchar  = new NVarchar("Hello, World");
    NVarchar nVarchar1 = nVarchar.replaceFirst("World", "TAM");
    assertEquals(nVarchar1.toString(), "Hello, TAM");
  }

  @Test
  public void testReplaceFirst() {
    NVarchar nVarchar  = new NVarchar("Hello, World");
    NVarchar nVarchar1 = nVarchar.replaceFirst("World", new NVarchar("TAM"));
    assertEquals(nVarchar1.toString(), "Hello, TAM");
  }

  @Test
  public void replaceAll() {
    NVarchar nVarchar  = new NVarchar("鹅,鹅,鹅");
    NVarchar nVarchar1 = nVarchar.replaceAll("鹅", "鸡");
    assertEquals(nVarchar1.toString(), "鸡,鸡,鸡");
  }

  @Test
  public void testReplaceAll() {
    NVarchar nVarchar  = new NVarchar("鹅,鹅,鹅");
    NVarchar nVarchar1 = nVarchar.replaceAll("鹅", new NVarchar("鸡"));
    assertEquals(nVarchar1.toString(), "鸡,鸡,鸡");
  }

  @Test
  public void testReplace() {
    NVarchar nVarchar  = new NVarchar("AA_BB_AA_CC");
    NVarchar nVarchar1 = nVarchar.replace("AA", "DD");
    assertEquals(nVarchar1.toString(), "DD_BB_DD_CC");
  }

  @Test
  public void split() {
    NVarchar[] varchars = getnVarchar().split("_");
    assertEquals(varchars.length, 6);
  }

  @Test
  public void testSplit() {
    NVarchar[] varchars = getnVarchar().split("_", 3);
    assertEquals(varchars.length, 3);
  }

  @Test
  public void toLowerCase() {
    NVarchar nVarchar = new NVarchar("AA");
    assertEquals(nVarchar.toLowerCase().toString(), "aa");
  }

  @Test
  public void testToLowerCase() {
    NVarchar nVarchar = new NVarchar("AA");
    assertEquals(nVarchar.toLowerCase(Locale.US).toString(), "aa");
  }

  @Test
  public void toUpperCase() {
    NVarchar nVarchar = new NVarchar("aa");
    assertEquals(nVarchar.toUpperCase().toString(), "AA");
  }

  @Test
  public void testToUpperCase() {
    NVarchar nVarchar = new NVarchar("aa");
    assertEquals(nVarchar.toUpperCase(Locale.US).toString(), "AA");
  }

  @Test
  public void trim() {
    NVarchar nVarchar = new NVarchar("  HELLO  ").trim();
    assertEquals(nVarchar.toString(), "HELLO");
  }

  @Test
  public void strip() {
    NVarchar nVarchar = new NVarchar("  HELLO  ").strip();
    assertEquals(nVarchar.toString(), "HELLO");
  }

  @Test
  public void stripLeading() {
    NVarchar nVarchar = new NVarchar("  HELLO  ").stripLeading();
    assertEquals(nVarchar.toString(), "HELLO  ");
  }

  @Test
  public void stripTrailing() {
    NVarchar nVarchar = new NVarchar("  HELLO  ").stripTrailing();
    assertEquals(nVarchar.toString(), "  HELLO");
  }

  @Test
  public void lines() {
    Stream<NVarchar> lines = new NVarchar("AA")
        .concat(Constant.LINE_SEPARATOR)
        .concat("BB")
        .lines();

    assertEquals(lines.count(), 2);
  }

  @Test
  public void repeat() {
    NVarchar nVarchar = new NVarchar("A").repeat(3);
    assertEquals(nVarchar.toString(), "AAA");
  }

  @Test
  public void join() {
    NVarchar nVarchar = NVarchar.join(",", "A", "B");
    assertEquals("A,B", nVarchar.toString());
  }

  @Test
  public void testJoin() {
    NVarchar nVarchar = NVarchar.join(",", List.of("A", "B"));
    assertEquals("A,B", nVarchar.toString());
  }

  @Test
  public void valueOf() {
    Object   value    = null;
    NVarchar nVarchar = NVarchar.valueOf(value);
    assertEquals(nVarchar.toString(), "null");
  }

  @Test
  public void testValueOf() {
    NVarchar nVarchar = NVarchar.valueOf(new char[]{'A', 'B'});
    assertEquals(nVarchar.toString(), "AB");
  }

  @Test
  public void testValueOf1() {
    NVarchar nVarchar = NVarchar.valueOf(new char[]{'A', 'B'}, 1, 1);
    assertEquals(nVarchar.toString(), "B");
  }

  @Test
  public void testValueOf2() {
    NVarchar nVarchar1 = NVarchar.valueOf(true);
    NVarchar nVarchar2 = NVarchar.valueOf(false);
    assertEquals(nVarchar1.toString(), "true");
    assertEquals(nVarchar2.toString(), "false");
  }

  @Test
  public void testValueOf3() {
    NVarchar nVarchar = NVarchar.valueOf('A');
    assertEquals(nVarchar.toString(), "A");
  }

  @Test
  public void testValueOf4() {
    NVarchar nVarchar = NVarchar.valueOf(1);
    assertEquals(nVarchar.toString(), "1");
  }

  @Test
  public void testValueOf5() {
    NVarchar nVarchar = NVarchar.valueOf(1L);
    assertEquals(nVarchar.toString(), "1");
  }

  @Test
  public void testValueOf6() {
    NVarchar nVarchar = NVarchar.valueOf(1F);
    assertEquals(nVarchar.toString(), "1.0");
  }

  @Test
  public void testValueOf7() {
    NVarchar nVarchar = NVarchar.valueOf(1D);
    assertEquals(nVarchar.toString(), "1.0");
  }
}