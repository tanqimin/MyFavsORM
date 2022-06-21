package work.myfavs.framework.orm.util.func;

public interface ThrowingSupplier<R, E extends Throwable> {
  R get() throws E;
}
