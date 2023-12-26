package work.myfavs.framework.orm.util.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class CollectionUtilTest {
  private static final Collection<String> TEST_NAME = List.of("A", "B", "C", "D", "E");

  @Test
  public void isEmpty() {
    Assert.assertFalse(CollectionUtil.isEmpty(TEST_NAME));
  }

  @Test
  public void split() {
    List<List<String>> split = CollectionUtil.split(TEST_NAME, 2);
    assertEquals(3, split.size());
  }

  @Test
  public void isNotEmpty() {
    assertTrue(CollectionUtil.isNotEmpty(TEST_NAME));
  }

  @Test
  public void join() {
    assertEquals("A,B,C,D,E", CollectionUtil.join(TEST_NAME, ","));
  }

  @Test
  public void testJoin() {
    assertEquals("a,b,c,d,e", CollectionUtil.join(TEST_NAME, ",", String::toLowerCase));
  }
}