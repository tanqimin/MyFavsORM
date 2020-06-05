package work.myfavs.framework.orm.meta.dialect;

import java.util.Optional;
import java.util.function.Function;

/**
 * 当启用分表的情况下，需要设置分表名称。 在使用Dialect生成SQL的时候，如果设置了分表名称，则使用分表名称进行查询
 *
 * @author tanqimin
 */
public class TableAlias {

  private final static ThreadLocal<String> TABLE_ALIAS_POOL = new ThreadLocal<>();

  public static String get() {
    return TABLE_ALIAS_POOL.get();
  }

  public static Optional<String> getOpt() {
    return Optional.ofNullable(get());
  }

  public static void set(String tableName) {
    TABLE_ALIAS_POOL.set(tableName);
  }

  public static void clear() {
    TABLE_ALIAS_POOL.remove();
  }

  public static <T> T run(String tableName, Function<String, T> function) {

    try {
      set(tableName);
      return function.apply(get());
    } finally {
      clear();
    }
  }
}
