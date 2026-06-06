package work.myfavs.framework.orm.util.func;

/**
 * 接受一个没有参数和返回值的方法
 * 操作调用 {@link #run()}  方法
 *
 * @param <E> 抛出异常的类型
 */
public interface ThrowingRunnable<E extends Throwable> {
  void run() throws E;
}
