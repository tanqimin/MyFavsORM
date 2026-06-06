package work.myfavs.framework.sb2.demo.domain.entity;

import work.myfavs.framework.orm.util.lang.NVarchar;

/**
 * 用于测试 {@link work.myfavs.framework.orm.util.lang.NVarchar} 类型序列化与反序列化的实体类.
 */
public class TestNvarchar {

  private String str;

  private NVarchar nstr;

  public String getStr() {
    return str;
  }

  public void setStr(String str) {
    this.str = str;
  }

  public NVarchar getNstr() {
    return nstr;
  }

  public void setNstr(NVarchar nstr) {
    this.nstr = nstr;
  }
}