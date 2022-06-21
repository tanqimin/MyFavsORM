package work.myfavs.framework.orm.util.func;

public interface ThrowingRunnable<E extends Throwable> {
  void run() throws E;
}
