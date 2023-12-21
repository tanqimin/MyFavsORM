package work.myfavs.framework.orm.meta.schema;


import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.common.ArrayUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

/**
 * 字段集合封装
 *
 * @author tanqimin
 */
public class Attributes {

  private final Map<String /* columnName */, Attribute> map = new LinkedHashMap<>();

  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock          writeLock     = readWriteLock.writeLock();

  /**
   * 根据数据库字段名获取Attribute
   *
   * @param columnName 数据库字段名
   * @return Attribute
   */
  public Attribute getAttribute(String columnName) {

    if (StringUtil.isEmpty(columnName))
      throw new DBException("Column name can not be empty");

    return map.get(columnName.toUpperCase());
  }

  public List<Attribute> getAttributes(String[] columnNames) {

    if (ArrayUtil.isEmpty(columnNames)) {
      return new ArrayList<>(map.values());
    }

    List<Attribute> res = new ArrayList<>();
    for (String columnName : columnNames) {
      final String col       = StringUtil.trim(columnName);
      Attribute    attribute = getAttribute(col);

      if (containsColumn(col)) {
        res.add(attribute);
      }
    }
    return res;
  }

  public String[] columns() {

    return map.keySet().toArray(new String[]{});
  }

  public Attribute put(String columnName, Attribute value) {

    if (StringUtil.isEmpty(columnName))
      throw new DBException("Column name can not be empty");

    Objects.requireNonNull(value);

    final String key = columnName.toUpperCase();
    writeLock.lock();
    try {
      return map.put(key, value);
    } finally {
      writeLock.unlock();
    }
  }

  public void forEach(BiConsumer<String, Attribute> action) {
    map.forEach(action);
  }

  public boolean containsColumn(String columnName) {

    if (StringUtil.isEmpty(columnName))
      throw new DBException("Column name can not be empty");

    return map.containsKey(columnName.toUpperCase());
  }

  public Collection<Attribute> values() {

    return map.values();
  }

  public int size() {

    return map.size();
  }
}
