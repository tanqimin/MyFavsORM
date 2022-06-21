package work.myfavs.framework.orm.util.func;

public interface ThrowingConsumer<T, E extends Throwable> {
  void accept(T t) throws E;
}
