package work.myfavs.framework.orm.meta.dialect;

import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * SQL 语句缓存
 *
 * @author tanqimin
 */
public final class SqlCache {

  private final static Map<String, Sql> CACHE = new ConcurrentHashMap<>();

  private SqlCache() {
  }

  public static Sql put(Class<?> clazz, Opt opt, String[] columns, Sql sql) {
    final String key = getKey(clazz, opt, columns);
    return CACHE.put(key, sql);
  }

  public static Sql put(Class<?> clazz, Opt opt, Sql sql) {
    return put(clazz, opt, null, sql);
  }

  public static Sql get(Class<?> clazz, Opt opt, String[] columns) {
    final String key = getKey(clazz, opt, columns);
    return Optional.ofNullable(CACHE.get(key)).map(Sql::new).orElse(null);
  }

  public static Sql get(Class<?> clazz, Opt opt) {
    return get(clazz, opt, null);
  }

  public static boolean contains(Class<?> clazz, Opt opt, String[] columns) {
    final String key = getKey(clazz, opt, columns);
    return CACHE.containsKey(key);
  }

  public static boolean contains(Class<?> clazz, Opt opt) {
    return contains(clazz, opt, null);
  }

  public static Sql computeIfAbsent(Class<?> clazz, Opt opt, String[] columns,
      Function<String, Sql> func) {
    final String key = getKey(clazz, opt, columns);
    return CACHE.computeIfAbsent(key, func);
  }

  public static Sql computeIfAbsent(Class<?> clazz, Opt opt,
      Function<String, Sql> func) {
    return computeIfAbsent(clazz, opt, null, func);
  }

  public static void clear() {
    CACHE.clear();
  }

  private static String getKey(Class<?> clazz, Opt opt, String[] columns) {
    final String clazzName = clazz.getName();
    final String optType = opt.name();
    final StringBuilder col = new StringBuilder();
    if (columns == null || columns.length == 0) {
      col.append("*");
    } else {
      Arrays.stream(columns).sorted().forEach(c -> col.append(c).append("_"));
      col.deleteCharAt(col.lastIndexOf("_"));
    }

    return StrUtil.format("{}_{}_{}", clazzName, optType, col);
  }

  /**
   * SQL 操作类型
   */
  enum Opt {
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
    DELETE;
  }
}
