package work.myfavs.framework.orm.util.func;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

  R apply(T t)
      throws E;

}
