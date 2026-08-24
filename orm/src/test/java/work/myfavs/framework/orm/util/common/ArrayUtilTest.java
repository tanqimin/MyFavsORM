package work.myfavs.framework.orm.util.common;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilTest {

  public final static String[] obj = {"A", "BC", "D"};

  @Test
  public void isArray() {

    Assert.assertTrue(ArrayUtil.isArray(obj));
  }

  @Test
  public void isEmpty() {
    Assert.assertFalse(ArrayUtil.isEmpty(obj));
  }

  @Test
  public void isNotEmpty() {
    Assert.assertTrue(ArrayUtil.isNotEmpty(obj));
  }

  // ======================== concat ========================

  @Test
  public void concatBothNull() {
    Assert.assertArrayEquals(new int[0], ArrayUtil.concat(null, null));
  }

  @Test
  public void concatFirstNull() {
    int[] b = {4, 5};
    int[] result = ArrayUtil.concat(null, b);
    Assert.assertNotSame("concat(null, b) 应返回 b 的副本", b, result);
    Assert.assertArrayEquals(new int[]{4, 5}, result);
  }

  @Test
  public void concatSecondNull() {
    int[] a = {1, 2, 3};
    int[] result = ArrayUtil.concat(a, null);
    Assert.assertNotSame("concat(a, null) 应返回 a 的副本", a, result);
    Assert.assertArrayEquals(new int[]{1, 2, 3}, result);
  }

  @Test
  public void concatNormal() {
    int[] a = {1, 2, 3};
    int[] b = {4, 5};
    Assert.assertArrayEquals(new int[]{1, 2, 3, 4, 5}, ArrayUtil.concat(a, b));
  }

  @Test
  public void concatEmptyFirst() {
    int[] a = {};
    int[] b = {4, 5};
    int[] result = ArrayUtil.concat(a, b);
    Assert.assertNotSame("concat(empty, b) 应返回 b 的副本", b, result);
    Assert.assertArrayEquals(new int[]{4, 5}, result);
  }

  @Test
  public void concatEmptySecond() {
    int[] a = {1, 2, 3};
    int[] b = {};
    int[] result = ArrayUtil.concat(a, b);
    Assert.assertNotSame("concat(a, empty) 应返回 a 的副本", a, result);
    Assert.assertArrayEquals(new int[]{1, 2, 3}, result);
  }
}