package work.myfavs.framework.orm.util.func;

/**
 * 接受单个输入参数且不返回任何结果的操作
 * 操作调用 {@link #accept(Object)} 方法
 *
 * @param <T> 输入参数的类型
 * @param <E> 抛出异常的类型
 */
public interface ThrowingConsumer<T, E extends Throwable> {
  void accept(T t) throws E;
}
