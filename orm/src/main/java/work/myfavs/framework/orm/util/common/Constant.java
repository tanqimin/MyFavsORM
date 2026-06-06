package work.myfavs.framework.orm.util.common;

import work.myfavs.framework.orm.util.lang.NText;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.List;

/**
 * 常量接口，定义框架中使用的各类常量和默认配置
 */
public interface Constant {

  /**
   * SQL Server 中参数最大长度
   */
  int MAX_PARAM_SIZE_FOR_MSSQL = 1000;

  /**
   * 默认日期格式
   */
  String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * 默认日期格式器
   */
  SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_STR);

  /**
   * 原始类型列表
   */
  List<Class<?>> PRIMITIVE_TYPES = List.of(
      Integer.class,
      Long.class,
      Double.class,
      String.class,
      Boolean.class,
      NVarchar.class,
      NText.class,
      Float.class,
      Number.class,
      Short.class);

  /**
   * 下划线字符
   */
  char UNDERLINE = '_';

  /**
   * 模糊查询多字符通配符
   */
  char FUZZY_MULTIPLE = '%';

  /**
   * 模糊查询单字符通配符
   */
  char FUZZY_SINGLE = '_';

  /**
   * 模糊查询转义字符
   */
  char FUZZY_ESCAPE = '¦';

  /**
   * 空格字符
   */
  char SPACE_CHAR = ' ';

  /**
   * 空格字符串
   */
  String SPACE = " ";

  /**
   * 逗号符号
   */
  String SYMBOL_COMMA = ",";

  /**
   * 系统行分隔符
   */
  String LINE_SEPARATOR = System.lineSeparator();

  /**
   * 系统默认时区
   */
  ZoneId ZONE_ID = ZoneId.systemDefault();
}
