package work.myfavs.framework.orm.util.lang;

/**
 * 对应数据库类型为 {@code NVarchar}，相关方法参考 {@link String} 同名类型
 */
public final class NVarchar extends Unicode {

  public static final String NVARCHAR = "NVARCHAR";

  public NVarchar() {
    this.sqlType = NVARCHAR;
  }

  public NVarchar(String content) {
    super(content, NVARCHAR);
  }
}
