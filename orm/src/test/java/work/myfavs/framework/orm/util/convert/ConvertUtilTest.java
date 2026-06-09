package work.myfavs.framework.orm.util.convert;

import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class ConvertUtilTest {

  private enum TestEnum {
    VALUE_A, VALUE_B
  }

  @Test
  public void toCollectionWithIntArray() {
    Collection<?> result = ConvertUtil.toCollection(new int[]{1, 2, 3});
    Assert.assertEquals(3, result.size());
    Assert.assertTrue(result.contains(1));
    Assert.assertTrue(result.contains(2));
    Assert.assertTrue(result.contains(3));
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithLongArray() {
    Collection<?> result = ConvertUtil.toCollection(new long[]{1L, 2L});
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(1L));
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithDoubleArray() {
    Collection<?> result = ConvertUtil.toCollection(new double[]{1.1, 2.2});
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(1.1));
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithFloatArray() {
    Collection<?> result = ConvertUtil.toCollection(new float[]{1.0f, 2.0f});
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithShortArray() {
    Collection<?> result = ConvertUtil.toCollection(new short[]{(short) 1, (short) 2});
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithByteArray() {
    Collection<?> result = ConvertUtil.toCollection(new byte[]{1, 2});
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithBooleanArray() {
    Collection<?> result = ConvertUtil.toCollection(new boolean[]{true, false});
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(true));
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithCharArray() {
    Collection<?> result = ConvertUtil.toCollection(new char[]{'a', 'b'});
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains('a'));
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithObjectArray() {
    Collection<?> result = ConvertUtil.toCollection(new String[]{"x", "y", "z"});
    Assert.assertEquals(3, result.size());
    Assert.assertTrue(result.contains("x"));
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test
  public void toCollectionWithCollectionInput() {
    List<String> input = new ArrayList<>(Arrays.asList("a", "b"));
    Collection<?> result = ConvertUtil.toCollection(input);
    Assert.assertEquals(2, result.size());
    Assert.assertSame(input, result);
  }

  @Test
  public void toCollectionWithNull() {
    Collection<?> result = ConvertUtil.toCollection(null);
    Assert.assertNotNull(result);
    Assert.assertTrue(result.isEmpty());
    Assert.assertTrue(result instanceof ArrayList);
  }

  @Test(expected = InvalidDataAccessException.class)
  public void toCollectionWithUnsupportedType() {
    ConvertUtil.toCollection("not a collection");
  }

  @Test
  public void toIntWithNull() {
    Assert.assertNull(ConvertUtil.toInt(null));
  }

  @Test
  public void toIntWithNumber() {
    Assert.assertEquals(Integer.valueOf(42), ConvertUtil.toInt(42));
  }

  @Test
  public void toIntWithString() {
    Assert.assertEquals(Integer.valueOf(42), ConvertUtil.toInt("42"));
  }

  @Test
  public void toIntWithEmptyStringReturnsNull() {
    Assert.assertNull(ConvertUtil.toInt(""));
  }

  @Test(expected = NumberFormatException.class)
  public void toIntWithInvalidString() {
    ConvertUtil.toInt("not-a-number");
  }

  @Test
  public void toShortWithNull() {
    Assert.assertNull(ConvertUtil.toShort(null));
  }

  @Test
  public void toShortWithNumber() {
    Assert.assertEquals(Short.valueOf((short) 42), ConvertUtil.toShort((short) 42));
  }

  @Test
  public void toShortWithString() {
    Assert.assertEquals(Short.valueOf((short) 42), ConvertUtil.toShort("42"));
  }

  @Test
  public void toShortWithEmptyStringReturnsNull() {
    Assert.assertNull(ConvertUtil.toShort(""));
  }

  @Test(expected = NumberFormatException.class)
  public void toShortWithInvalidString() {
    ConvertUtil.toShort("not-a-number");
  }

  @Test
  public void toLongWithNull() {
    Assert.assertNull(ConvertUtil.toLong(null));
  }

  @Test
  public void toLongWithNumber() {
    Assert.assertEquals(Long.valueOf(42L), ConvertUtil.toLong(42L));
  }

  @Test
  public void toLongWithString() {
    Assert.assertEquals(Long.valueOf(42L), ConvertUtil.toLong("42"));
  }

  @Test
  public void toLongWithEmptyStringReturnsNull() {
    Assert.assertNull(ConvertUtil.toLong(""));
  }

  @Test(expected = NumberFormatException.class)
  public void toLongWithInvalidString() {
    ConvertUtil.toLong("not-a-number");
  }

  @Test
  public void toFloatWithNull() {
    Assert.assertNull(ConvertUtil.toFloat(null));
  }

  @Test
  public void toFloatWithNumber() {
    Assert.assertEquals(Float.valueOf(42.5f), ConvertUtil.toFloat(42.5f));
  }

  @Test
  public void toFloatWithString() {
    Assert.assertEquals(Float.valueOf(42.5f), ConvertUtil.toFloat("42.5"));
  }

  @Test
  public void toFloatWithEmptyStringReturnsNull() {
    Assert.assertNull(ConvertUtil.toFloat(""));
  }

  @Test(expected = NumberFormatException.class)
  public void toFloatWithInvalidString() {
    ConvertUtil.toFloat("not-a-number");
  }

  @Test
  public void toDoubleWithNull() {
    Assert.assertNull(ConvertUtil.toDouble(null));
  }

  @Test
  public void toDoubleWithNumber() {
    Assert.assertEquals(Double.valueOf(42.5), ConvertUtil.toDouble(42.5));
  }

  @Test
  public void toDoubleWithString() {
    Assert.assertEquals(Double.valueOf(42.5), ConvertUtil.toDouble("42.5"));
  }

  @Test
  public void toDoubleWithEmptyStringReturnsNull() {
    Assert.assertNull(ConvertUtil.toDouble(""));
  }

  @Test(expected = NumberFormatException.class)
  public void toDoubleWithInvalidString() {
    ConvertUtil.toDouble("not-a-number");
  }

  @Test
  public void toByteWithNull() {
    Assert.assertNull(ConvertUtil.toByte(null));
  }

  @Test
  public void toByteWithNumber() {
    Assert.assertEquals(Byte.valueOf((byte) 42), ConvertUtil.toByte((byte) 42));
  }

  @Test
  public void toByteWithString() {
    Assert.assertEquals(Byte.valueOf((byte) 42), ConvertUtil.toByte("42"));
  }

  @Test
  public void toByteWithEmptyStringReturnsNull() {
    Assert.assertNull(ConvertUtil.toByte(""));
  }

  @Test(expected = NumberFormatException.class)
  public void toByteWithInvalidString() {
    ConvertUtil.toByte("not-a-number");
  }

  @Test
  public void toBigDecimalWithBigDecimalInput() {
    BigDecimal input = new BigDecimal("42.5");
    Assert.assertSame(input, ConvertUtil.toBigDecimal(input));
  }

  @Test
  public void toBigDecimalWithDouble() {
    BigDecimal result = ConvertUtil.toBigDecimal(42.5);
    Assert.assertEquals(0, new BigDecimal("42.5").compareTo(result));
  }

  @Test
  public void toBigDecimalWithString() {
    BigDecimal result = ConvertUtil.toBigDecimal("42.5");
    Assert.assertEquals(0, new BigDecimal("42.5").compareTo(result));
  }

  @Test
  public void toBoolWithNullPrimitiveReturnsFalse() {
    Assert.assertFalse(ConvertUtil.toBool(null, true));
  }

  @Test
  public void toBoolWithNullWrapperReturnsNull() {
    Assert.assertNull(ConvertUtil.toBool(null, false));
  }

  @Test
  public void toBoolWithBooleanTrue() {
    Assert.assertTrue(ConvertUtil.toBool(true, false));
  }

  @Test
  public void toBoolWithBooleanFalse() {
    Assert.assertFalse(ConvertUtil.toBool(false, false));
  }

  @Test
  public void toBoolWithNumberZeroReturnsFalse() {
    Assert.assertFalse(ConvertUtil.toBool(0, false));
  }

  @Test
  public void toBoolWithNumberNonZeroReturnsTrue() {
    Assert.assertTrue(ConvertUtil.toBool(1, false));
    Assert.assertTrue(ConvertUtil.toBool(-1, false));
  }

  @Test
  public void toBoolWithStringY() {
    Assert.assertTrue(ConvertUtil.toBool("Y", false));
  }

  @Test
  public void toBoolWithStringYES() {
    Assert.assertTrue(ConvertUtil.toBool("YES", false));
  }

  @Test
  public void toBoolWithStringTRUE() {
    Assert.assertTrue(ConvertUtil.toBool("TRUE", false));
  }

  @Test
  public void toBoolWithStringT() {
    Assert.assertTrue(ConvertUtil.toBool("T", false));
  }

  @Test
  public void toBoolWithStringJ() {
    Assert.assertTrue(ConvertUtil.toBool("J", false));
  }

  @Test
  public void toBoolWithString1() {
    Assert.assertTrue(ConvertUtil.toBool("1", false));
  }

  @Test
  public void toBoolWithString0() {
    Assert.assertFalse(ConvertUtil.toBool("0", false));
  }

  @Test
  public void toBoolWithStringYLowerCase() {
    Assert.assertTrue(ConvertUtil.toBool("y", false));
  }

  @Test
  public void toBoolWithCharacterY() {
    Assert.assertTrue(ConvertUtil.toBool('Y', false));
  }

  @Test
  public void toBoolWithCharacterT() {
    Assert.assertTrue(ConvertUtil.toBool('T', false));
  }

  @Test
  public void toBoolWithCharacterJ() {
    Assert.assertTrue(ConvertUtil.toBool('J', false));
  }

  @Test
  public void toBoolWithInvalidStringReturnsFalse() {
    Assert.assertFalse(ConvertUtil.toBool("invalid", false));
    Assert.assertFalse(ConvertUtil.toBool("N", false));
    Assert.assertFalse(ConvertUtil.toBool("FALSE", false));
    Assert.assertFalse(ConvertUtil.toBool("OFF", false));
  }

  @Test
  public void toEnumWithValidString() {
    Assert.assertEquals(TestEnum.VALUE_A, ConvertUtil.toEnum(TestEnum.class, "VALUE_A"));
  }

  @Test
  public void toEnumWithNull() {
    Assert.assertNull(ConvertUtil.toEnum(TestEnum.class, null));
  }

  @Test(expected = InvalidDataAccessException.class)
  public void toEnumWithInvalidString() {
    ConvertUtil.toEnum(TestEnum.class, "INVALID_VALUE");
  }

  @Test
  public void toDateWithDateInput() {
    Date now = new Date();
    Assert.assertEquals(now, ConvertUtil.toDate(now));
  }

  @Test
  public void toDateWithNumberMillis() {
    long millis = System.currentTimeMillis();
    Date result = ConvertUtil.toDate(millis);
    Assert.assertEquals(new Date(millis), result);
  }

  @Test
  public void toDateWithLocalDateTime() {
    LocalDateTime ldt = LocalDateTime.now();
    Date result = ConvertUtil.toDate(ldt);
    long expectedMillis = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    Assert.assertEquals(new Date(expectedMillis), result);
  }

  @Test
  public void toDateWithNull() {
    Assert.assertNull(ConvertUtil.toDate(null));
  }

  @Test(expected = InvalidDataAccessException.class)
  public void toDateWithInvalidType() {
    ConvertUtil.toDate("not a date");
  }

  @Test
  public void toUuidWithUuidInput() {
    UUID input = UUID.randomUUID();
    Assert.assertSame(input, ConvertUtil.toUUID(input));
  }

  @Test
  public void toUuidWithValidString() {
    UUID expected = UUID.randomUUID();
    Assert.assertEquals(expected, ConvertUtil.toUUID(expected.toString()));
  }

  @Test
  public void toUuidWithNull() {
    Assert.assertNull(ConvertUtil.toUUID(null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void toUuidWithInvalidString() {
    ConvertUtil.toUUID("not-a-uuid");
  }
}
