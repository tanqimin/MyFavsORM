package work.myfavs.framework.orm;

import work.myfavs.framework.orm.util.exception.DBException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** @Author Karl TAM @Date 2022/01/05 10:54 @Version 1.0 */
public class DBTemplateContext {
  private static final Map<String, DBTemplate> POOL = new ConcurrentHashMap<>();
  private static final ThreadLocal<DBTemplate> CURRENT = new ThreadLocal<>();

  public static DBTemplate get(String dsName) {
    if (POOL.containsKey(dsName)) {
      return POOL.get(dsName);
    }
    throw new DBException("The DataSource named {} not exists.", dsName);
  }

  public static DBTemplate add(String dsName, DBTemplate dbTemplate) {
    if (POOL.isEmpty()) setCurrent(dbTemplate);
    return POOL.computeIfAbsent(dsName, key -> dbTemplate);
  }

  public static void setCurrent(DBTemplate dbTemplate) {
    CURRENT.set(dbTemplate);
  }

  public static DBTemplate getCurrent() {
    return CURRENT.get();
  }
}
