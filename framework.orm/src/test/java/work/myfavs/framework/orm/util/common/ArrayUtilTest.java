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
}