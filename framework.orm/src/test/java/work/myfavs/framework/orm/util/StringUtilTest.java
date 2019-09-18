package work.myfavs.framework.orm.util;

import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

  @Test
  public void format() {

    String str1 = StrUtil.format("HELLO, {}", "WORLD");
    Assert.assertEquals("HELLO, WORLD", str1);
    String str2 = StrUtil.format("HELLO, \\{}{}", "WORLD");
    Assert.assertEquals("HELLO, {}WORLD", str2);
    String nullStr = null;
    String str3    = StrUtil.format("HELLO, {}", nullStr);
    Assert.assertEquals("HELLO, null", str3);
  }


  @Test
  public void camel() {

    String str = "order_line";
    Assert.assertEquals("orderLine", StrUtil.toCamelCase(str));
  }

  @Test
  public void underline() {

    String str = "orderLine";
    Assert.assertEquals("order_line", StrUtil.toUnderlineCase(str));
  }

}