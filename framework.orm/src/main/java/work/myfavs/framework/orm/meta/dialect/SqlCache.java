package work.myfavs.framework.orm.meta.dialect;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * SQL 语句缓存
 *
 * @author tanqimin
 */
public final class SqlCache {

  private static final Map<String/*class_opt_columns*/, String/*sql*/> CACHE = new ConcurrentHashMap<>();

  private SqlCache() {}

  public static String put(Class<?> clazz, Opt opt, String[] columns, String sql) {
    final String key = getKey(clazz, opt, columns);
    return CACHE.put(key, sql);
  }

  public static String put(Class<?> clazz, Opt opt, String sql) {
    return put(clazz, opt, null, sql);
  }

  public static String get(Class<?> clazz, Opt opt, String[] columns) {
    final String key = getKey(clazz, opt, columns);
    return CACHE.get(key);
  }

  public static String get(Class<?> clazz, Opt opt) {
    return get(clazz, opt, null);
  }

  public static boolean contains(Class<?> clazz, Opt opt, String[] columns) {
    final String key = getKey(clazz, opt, columns);
    return CACHE.containsKey(key);
  }

  public static boolean contains(Class<?> clazz, Opt opt) {
    return contains(clazz, opt, null);
  }

  public static String computeIfAbsent(
      Class<?> clazz, Opt opt, String[] columns, Supplier<String> sqlSupplier) {
    final String key = getKey(clazz, opt, columns);
    String       sql = CACHE.get(key);
    if (Objects.isNull(sql)) {
      sql = sqlSupplier.get();
      CACHE.put(key, sql);
    }
    return sql;
  }

  public static String computeIfAbsent(Class<?> clazz, Opt opt, Supplier<String> sqlSupplier) {
    return computeIfAbsent(clazz, opt, null, sqlSupplier);
  }

  public static void clear() {
    CACHE.clear();
  }

  private static String getKey(Class<?> clazz, Opt opt, String[] columns) {
    String col = "*";
    if (ArrayUtil.isNotEmpty(columns)) {
      col = StrUtil.join("_", Arrays.stream(columns).sorted());
    }

    return StrUtil.format("{}_{}_{}", clazz.getName(), opt.name(), col);
  }

  /**
   * SQL 操作类型
   */
  public enum Opt {
    /**
     * 查询
     */
    SELECT,
    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE
  }
}
