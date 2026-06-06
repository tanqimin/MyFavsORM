package work.myfavs.framework.example.domain.entity;

import work.myfavs.framework.orm.util.lang.NVarchar;

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