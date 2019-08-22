package work.myfavs.framework.orm.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

  @Test
  public void format() {

    String str1 = StringUtil.format("HELLO, {}", "WORLD");
    Assert.assertEquals("HELLO, WORLD", str1);
    String str2 = StringUtil.format("HELLO, \\{}{}", "WORLD");
    Assert.assertEquals("HELLO, {}WORLD", str2);
    String nullStr = null;
    String str3 = StringUtil.format("HELLO, {}", nullStr);
    Assert.assertEquals("HELLO, null", str3);
  }


  @Test
  public void camel() {
    String str = "order_line";
    Assert.assertEquals("orderLine", StringUtil.camel(str));
  }

  @Test
  public void underline() {
    String str = "orderLine";
    Assert.assertEquals("order_line", StringUtil.underline(str));
  }

}