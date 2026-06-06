package work.myfavs.framework.orm.util.lang;

/**
 * 对应数据库类型为 {@code NText}，相关方法参考 {@link String} 同名类型
 */
public final class NText extends Unicode {

  public static final String NTEXT = "NTEXT";

  public NText() {
    this.sqlType = NTEXT;
  }

  public NText(String content) {
    super(content, NTEXT);
  }
}
