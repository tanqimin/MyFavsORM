package work.myfavs.framework.orm.util.common;

import java.text.SimpleDateFormat;
import java.util.List;

public interface Constant {
  /**
   * SQL Server 中参数最大长度
   */
  int MAX_PARAM_SIZE_FOR_MSSQL = 1000;

  String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss.SSS";

  SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_STR);

  List<Class<?>> PRIMITIVE_TYPES = List.of(Integer.class, Long.class, Double.class, String.class, Float.class, Boolean.class, Number.class,
                                           Short.class);
}
