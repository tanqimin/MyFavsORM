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
    String str3 = StrUtil.format("HELLO, {}", nullStr);
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

  @Test
  public void toUnderlineCase() {
    String s1 = "customFieldValue01";
    String s2 = "customFIEldValue01";
    String s3 = "i18nName";
    String s4 = "i13338nName";
    String s5 = "i13338Name";
    Assert.assertEquals("custom_field_value_01", StringUtil.toUnderlineCase(s1));
    Assert.assertEquals("custom_fi_eld_value_01", StringUtil.toUnderlineCase(s2));
    Assert.assertEquals("custom_field_value01", StrUtil.toUnderlineCase(s1));
    Assert.assertEquals("i_18_n_name", StringUtil.toUnderlineCase(s3));
    Assert.assertEquals("i_13338_n_name", StringUtil.toUnderlineCase(s4));
    Assert.assertEquals("i_13338_name", StringUtil.toUnderlineCase(s5));

    Assert.assertEquals(s1, StrUtil.toCamelCase(StringUtil.toUnderlineCase(s1)));
  }
}
