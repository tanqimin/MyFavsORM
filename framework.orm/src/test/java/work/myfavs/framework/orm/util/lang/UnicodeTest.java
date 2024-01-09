package work.myfavs.framework.orm.util.lang;

import com.alibaba.druid.sql.visitor.functions.Char;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class UnicodeTest {

  @Test
  public void subSequence() {
    CharSequence charSequence = new NVarchar("ABCDEFG").subSequence(1, 3);
    assertEquals(charSequence.toString(), "BC");
  }

  @Test
  public void length() {
    NVarchar nvarchar = new NVarchar("ABC");
    assertEquals(nvarchar.length(), 3);
  }

  @Test
  public void isEmpty() {
    NVarchar nvarchar = new NVarchar();
    assertTrue(nvarchar.isEmpty());
    assertFalse(nvarchar.concat("ABC").isEmpty());
  }

  @Test
  public void charAt() {
    NVarchar nvarchar = new NVarchar("ABC");
    assertEquals(nvarchar.charAt(1), 'B');
  }

  @Test
  public void testToString() {
    NVarchar nvarchar = new NVarchar("ABC");
    assertEquals(nvarchar.toString(), "ABC");
  }

  @Test
  public void chars() {
    NVarchar nvarchar = new NVarchar("ABC");
    assertEquals(nvarchar.chars().count(), 3);
  }

  @Test
  public void codePoints() {
    NVarchar nvarchar = new NVarchar("AB🤣");
    assertEquals(nvarchar.codePoints().count(), 3);
  }

  @Test
  public void getBytes() throws UnsupportedEncodingException {
    NVarchar str   = new NVarchar("ABC");
    byte[]   bytes = str.getBytes("UTF-8");
    assertEquals('A', (char) bytes[0]);
    assertEquals('B', (char) bytes[1]);
    assertEquals('C', (char) bytes[2]);
  }

  @Test
  public void testGetBytes() {
    NVarchar str   = new NVarchar("ABC");
    byte[]   bytes = str.getBytes(StandardCharsets.UTF_8);
    assertEquals('A', (char) bytes[0]);
    assertEquals('B', (char) bytes[1]);
    assertEquals('C', (char) bytes[2]);
  }

  @Test
  public void testGetBytes1() {
    NVarchar str   = new NVarchar("ABC");
    byte[]   bytes = str.getBytes();
    assertEquals('A', (char) bytes[0]);
    assertEquals('B', (char) bytes[1]);
    assertEquals('C', (char) bytes[2]);
  }

  @Test
  public void testEquals() {
    NVarchar nvarchar1 = new NVarchar("A");
    NVarchar nvarchar2 = new NVarchar("A");
    NVarchar nvarchar3 = new NVarchar("B");
    assertTrue(nvarchar1.equals(nvarchar2));
    assertFalse(nvarchar1.equals(nvarchar3));
  }

  @Test
  public void contentEquals() {
    NVarchar nvarchar1 = new NVarchar("A");
    NVarchar nvarchar2 = new NVarchar("A");
    NVarchar nvarchar3 = new NVarchar("B");
    assertTrue(nvarchar1.contentEquals(nvarchar2));
    assertFalse(nvarchar1.contentEquals(nvarchar3));
  }

  @Test
  public void testContentEquals() {
    NVarchar     nvarchar1 = new NVarchar("A");
    StringBuffer nvarchar2 = new StringBuffer("A");
    StringBuffer nvarchar3 = new StringBuffer("B");
    assertTrue(nvarchar1.contentEquals(nvarchar2));
    assertFalse(nvarchar1.contentEquals(nvarchar3));
  }

  @Test
  public void equalsIgnoreCase() {
    NVarchar nvarchar1 = new NVarchar("A");
    NVarchar nvarchar2 = new NVarchar("a");
    NVarchar nvarchar3 = new NVarchar("B");
    assertTrue(nvarchar1.equalsIgnoreCase(nvarchar2));
    assertFalse(nvarchar1.equalsIgnoreCase(nvarchar3));
  }

  @Test
  public void testEqualsIgnoreCase() {
    NVarchar nvarchar1 = new NVarchar("A");
    assertTrue(nvarchar1.equalsIgnoreCase("a"));
    assertFalse(nvarchar1.equalsIgnoreCase("b"));
  }

  @Test
  public void compareTo() {
    NVarchar nvarchar1 = new NVarchar("A");
    assertEquals(0, nvarchar1.compareTo("A"));
    assertTrue(nvarchar1.compareTo("B") < 0);
  }

  @Test
  public void compareToIgnoreCase() {
    NVarchar nvarchar1 = new NVarchar("A");
    NVarchar nvarchar2 = new NVarchar("a");
    NVarchar nvarchar3 = new NVarchar("b");
    assertEquals(0, nvarchar1.compareToIgnoreCase(nvarchar2));
    assertTrue(nvarchar1.compareToIgnoreCase(nvarchar3) < 0);
  }

  @Test
  public void regionMatches() {
    NVarchar nvarchar1 = new NVarchar("ABBBBC");
    NVarchar nvarchar2 = new NVarchar("CCBBBBA");
    assertTrue(nvarchar1.regionMatches(1, nvarchar2, 2, 4));
  }

  @Test
  public void testRegionMatches() {
    NVarchar nvarchar1 = new NVarchar("ABBBBC");
    assertTrue(nvarchar1.regionMatches(1, "CCBBBBA", 2, 4));
  }

  @Test
  public void testRegionMatches1() {
    NVarchar nvarchar1 = new NVarchar("ABBBBC");
    assertTrue(nvarchar1.regionMatches(true, 1, "CCbbbbA", 2, 4));
  }

  @Test
  public void testRegionMatches2() {
    NVarchar nvarchar1 = new NVarchar("ABBBBC");
    NVarchar nvarchar2 = new NVarchar("CCbbbbA");
    assertTrue(nvarchar1.regionMatches(true, 1, nvarchar2, 2, 4));
  }

  @Test
  public void startsWith() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertTrue(nVarchar.startsWith("OB", 3));
  }

  @Test
  public void testStartsWith() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertTrue(nVarchar.startsWith("TB"));
  }

  @Test
  public void testStartsWith1() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertTrue(nVarchar.startsWith(new NVarchar("OB"), 3));
  }

  @Test
  public void testStartsWith2() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertTrue(nVarchar.startsWith(new NVarchar("TB")));
  }

  @Test
  public void endsWith() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertTrue(nVarchar.endsWith("ND"));
  }

  @Test
  public void testEndsWith() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertTrue(nVarchar.endsWith(new NVarchar("ND")));
  }

  @Test
  public void testHashCode() {
    NVarchar nVarchar = new NVarchar("ABC");
    assertEquals(nVarchar.hashCode(), "ABC".hashCode());
  }

  @Test
  public void indexOf() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.indexOf('_'), 2);
  }

  @Test
  public void testIndexOf() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.indexOf('_', 1), 2);
  }

  @Test
  public void testIndexOf1() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.indexOf("_"), 2);
  }

  @Test
  public void testIndexOf2() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.indexOf(new NVarchar("_")), 2);
  }

  @Test
  public void lastIndexOf() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.lastIndexOf('_'), 5);
  }

  @Test
  public void testLastIndexOf() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.lastIndexOf('_', 10), 5);
  }


  @Test
  public void testLastIndexOf1() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.lastIndexOf("_"), 5);
  }

  @Test
  public void testLastIndexOf2() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.lastIndexOf(new NVarchar("_")), 5);
  }

  @Test
  public void testLastIndexOf3() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.lastIndexOf("_", 10), 5);
  }

  @Test
  public void testLastIndexOf4() {
    NVarchar nVarchar = new NVarchar("TB_OB_OUTBOUND");
    assertEquals(nVarchar.lastIndexOf(new NVarchar("_"), 10), 5);
  }

  @Test
  public void matches() {
    NVarchar nVarchar = new NVarchar("Hello World!");
    assertTrue(nVarchar.matches("[A-Za-z0-9 !]+"));
  }

  @Test
  public void contains() {
    NVarchar nVarchar = new NVarchar("Hello World!");
    assertTrue(nVarchar.contains("o W"));
  }

  @Test
  public void isBlank() {
    NVarchar nVarchar = new NVarchar();
    assertTrue(nVarchar.isBlank());
  }

  @Test
  public void toCharArray() {
    NVarchar nVarchar = new NVarchar("Hello World!");
    char[]   charArray = nVarchar.toCharArray();
    assertEquals('e', charArray[1]);
  }

  @Test
  public void testCompareTo() {
    NVarchar nvarchar1 = new NVarchar("A");
    NVarchar nvarchar2 = new NVarchar("A");
    NVarchar nvarchar3 = new NVarchar("B");
    assertEquals(0, nvarchar1.compareTo(nvarchar2));
    assertTrue(nvarchar1.compareTo(nvarchar3) < 0);
  }
}