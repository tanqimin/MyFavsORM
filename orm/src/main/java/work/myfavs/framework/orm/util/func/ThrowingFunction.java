package work.myfavs.framework.orm.util.func;

/**
 * 接受一个参数并生成结果的函数。
 * 函数调用 {@link #apply(Object)} 方法
 *
 * @param <T> 输入参数的类型
 * @param <R> 返回结果类型
 * @param <E> 抛出异常的类型
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

  R apply(T t) throws E;
}
