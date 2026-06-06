package work.myfavs.framework.orm.util.lang;

/**
 * 对应数据库类型为 {@code NVarchar}，相关方法参考 {@link String} 同名类型
 */
public final class NVarchar extends Unicode {

  public static final String NVARCHAR = "NVARCHAR";

  /**
   * 构造 NVarchar 实例.
   */
  public NVarchar() {
    this.sqlType = NVARCHAR;
  }

  /**
   * 构造 NVarchar 实例.
   *
   * @param content 字符串内容
   */
  public NVarchar(String content) {
    super(content, NVARCHAR);
  }
}
