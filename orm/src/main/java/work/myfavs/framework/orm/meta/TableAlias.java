package work.myfavs.framework.orm.meta;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 当启用分表的情况下，需要设置分表名称。 在使用Dialect生成SQL的时候，如果设置了分表名称，则使用分表名称进行查询
 *
 * @author tanqimin
 */
public class TableAlias {

  private static final ThreadLocal<String> TABLE_ALIAS_POOL = new ThreadLocal<>();

  /**
   * 获取当前线程的分表名称
   *
   * @return 分表名称，可能为 {@code null}
   */
  public static String get() {
    return TABLE_ALIAS_POOL.get();
  }

  /**
   * 获取当前线程的分表名称（Optional 包装）
   *
   * @return 分表名称的 {@link Optional}
   */
  public static Optional<String> getOpt() {
    return Optional.ofNullable(get());
  }

  /**
   * 设置当前线程的分表名称
   *
   * @param tableName 分表名称
   */
  public static void set(String tableName) {
    TABLE_ALIAS_POOL.set(tableName);
  }

  /**
   * 清除当前线程的分表名称
   */
  public static void clear() {
    TABLE_ALIAS_POOL.remove();
  }

  /**
   * 在指定分表范围内执行 {@link Runnable}
   *
   * @param tableName 分表名称
   * @param runnable  待执行的任务
   */
  public static void runnable(String tableName, Runnable runnable) {
    try {
      set(tableName);
      runnable.run();
    } finally {
      clear();
    }
  }

  /**
   * 在指定分表范围内执行 {@link Consumer}
   *
   * @param tableName 分表名称
   * @param consumer  待执行的消费操作
   */
  public static void consumer(String tableName, Consumer<String> consumer) {
    try {
      set(tableName);
      consumer.accept(tableName);
    } finally {
      clear();
    }
  }

  /**
   * 在指定分表范围内执行 {@link Supplier} 并返回结果
   *
   * @param tableName 分表名称
   * @param supplier  待执行的提供操作
   * @param <T>       返回值类型
   * @return 提供操作的结果
   */
  public static <T> T supplier(String tableName, Supplier<T> supplier) {
    try {
      set(tableName);
      return supplier.get();
    } finally {
      clear();
    }
  }

  /**
   * 在指定分表范围内执行 {@link Function} 并返回结果
   *
   * @param tableName 分表名称
   * @param function  待执行的函数
   * @param <T>       返回值类型
   * @return 函数执行结果
   */
  public static <T> T function(String tableName, Function<String, T> function) {

    try {
      set(tableName);
      return function.apply(get());
    } finally {
      clear();
    }
  }
}
