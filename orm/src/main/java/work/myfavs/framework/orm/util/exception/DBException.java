package work.myfavs.framework.orm.util.exception;

public class DBException extends RuntimeException {

  public DBException() {}

  public DBException(String message, Object... params) {

    super(String.format(message, params));
  }

  public DBException(Throwable cause, String message, Object... params) {

    super(String.format(message, params), cause);
  }

  public DBException(Throwable cause) {

    super(cause);
  }
}
