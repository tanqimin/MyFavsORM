package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 字段集合封装
 *
 * @author tanqimin
 */
public class Attributes {

  private final Map<String, Attribute> map = new TreeMap<>();

  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock          writeLock     = readWriteLock.writeLock();

  /**
   * 根据实体属性名获取Attribute
   *
   * @param fieldName 实体属性名
   * @return Attribute
   */
  public Attribute getAttributeByFieldName(String fieldName) {

    Assert.notBlank(fieldName);

    for (Attribute value : map.values()) {
      if (StrUtil.equalsIgnoreCase(value.getFieldName(), fieldName)) {
        return value;
      }
    }
    return null;
  }

  /**
   * 根据数据库字段名获取Attribute
   *
   * @param columnName 数据库字段名
   * @return Attribute
   */
  public Attribute getAttribute(String columnName) {

    Assert.notBlank(columnName);

    return map.get(columnName.toUpperCase());
  }

  public List<Attribute> getAttributes(String[] columnNames) {

    if (ArrayUtil.isEmpty(columnNames)) {
      return CollUtil.list(true, map.values());
    }

    List<Attribute> res = new LinkedList<>();
    for (String columnName : columnNames) {
      final String col = StrUtil.trim(columnName);
      if (containsColumn(col)) {
        res.add(getAttribute(col));
      }
    }
    return res;
  }

  public String[] columns() {

    return map.keySet().toArray(new String[]{});
  }

  public Attribute put(String columnName, Attribute value) {

    Assert.notBlank(columnName);
    Assert.notNull(value);

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

  public Attribute computeIfAbsent(String columnName, Function<String, Attribute> mappingFunction) {

    Assert.notBlank(columnName);

    final String key = columnName.toUpperCase();
    writeLock.lock();
    try {
      return map.computeIfAbsent(key, mappingFunction);
    } finally {
      writeLock.unlock();
    }
  }

  public boolean containsColumn(String columnName) {

    Assert.notBlank(columnName);

    return map.containsKey(columnName.toUpperCase());
  }

  public Collection<Attribute> values() {

    return map.values();
  }

  public int size() {

    return map.size();
  }

  public boolean isEmpty() {

    return map.isEmpty();
  }
}
