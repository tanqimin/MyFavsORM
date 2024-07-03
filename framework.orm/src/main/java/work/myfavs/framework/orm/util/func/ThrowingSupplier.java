package work.myfavs.framework.orm.util.func;

/**
 * 接受没有参数并生成结果的函数
 * 函数调用 {@link #get()} 方法
 *
 * @param <R> 返回结果类型
 * @param <E> 抛出异常的类型
 */
public interface ThrowingSupplier<R, E extends Throwable> {
  R get() throws E;
}
