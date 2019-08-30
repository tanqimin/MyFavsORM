package work.myfavs.framework.orm.util.exception;

public class UnexpectedNewInstanceException
    extends RuntimeException {

  public UnexpectedNewInstanceException() {

  }

  public UnexpectedNewInstanceException(String message) {

    super(message);
  }

  public UnexpectedNewInstanceException(String message, Throwable cause) {

    super(message, cause);
  }

  public UnexpectedNewInstanceException(Throwable cause) {

    super(cause);
  }

  public UnexpectedNewInstanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

    super(message, cause, enableSuppression, writableStackTrace);
  }

}
