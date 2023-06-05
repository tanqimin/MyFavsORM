package work.myfavs.framework.orm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import work.myfavs.framework.orm.util.exception.DBException;

/** */
public class DBTemplateContext {
  private static final Map<String, DBTemplate> POOL = new ConcurrentHashMap<>();

  public static DBTemplate get(String dsName) {
    if (POOL.containsKey(dsName)) {
      return POOL.get(dsName);
    }
    throw new DBException("The DataSource named {} not exists.", dsName);
  }

  public static DBTemplate add(String dsName, DBTemplate dbTemplate) {
    POOL.put(dsName, dbTemplate);
    return dbTemplate;
  }
}
