package work.myfavs.framework.orm.util;

import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.util.common.StringUtil;

public class StringUtilTest {

  @Test
  public void format() {

    String str1 = String.format("HELLO, %s", "WORLD");
    Assert.assertEquals("HELLO, WORLD", str1);
    String str2 = String.format("HELLO, %%s%s", "WORLD");
    Assert.assertEquals("HELLO, %sWORLD", str2);
  }

  @Test
  public void camel() {

    String str = "order_line";
    Assert.assertEquals("orderLine", StringUtil.toCamelCase(str));
  }

  @Test
  public void underline() {

    String str = "orderLine";
    Assert.assertEquals("order_line", StringUtil.toUnderlineCase(str));
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
    Assert.assertEquals("i_18_n_name", StringUtil.toUnderlineCase(s3));
    Assert.assertEquals("i_13338_n_name", StringUtil.toUnderlineCase(s4));
    Assert.assertEquals("i_13338_name", StringUtil.toUnderlineCase(s5));

    Assert.assertEquals(s1, StringUtil.toCamelCase(StringUtil.toUnderlineCase(s1)));
  }
}
