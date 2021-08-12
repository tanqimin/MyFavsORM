package work.myfavs.framework.orm.util.exception;

import cn.hutool.core.util.StrUtil;

public class DBException extends RuntimeException {

  public DBException() {}

  public DBException(CharSequence message, Object... params) {

    super(StrUtil.format(message, params));
  }

  public DBException(Throwable cause, CharSequence message, Object... params) {

    super(StrUtil.format(message, params), cause);
  }

  public DBException(Throwable cause) {

    super(cause);
  }
}
