package work.myfavs.framework.orm.meta.schema;


import work.myfavs.framework.orm.util.common.ArrayUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * 字段集合封装
 *
 * @author tanqimin
 */
public class Attributes {

  private final Map<String /* columnName */, Attribute> map = new LinkedHashMap<>();

  /**
   * 根据数据库字段名获取Attribute
   *
   * @param columnName 数据库字段名
   * @return Attribute
   */
  public Attribute getAttribute(String columnName) {

    if (StringUtil.isEmpty(columnName))
      throw new InvalidDataAccessException("数据库字段名不能为空! ");

    return map.get(columnName.toUpperCase());
  }

  /**
   * 根据数据库字段名数组获取对应的 {@link Attribute} 列表
   *
   * @param columnNames 数据库字段名数组，为空则返回所有字段
   * @return {@link Attribute} 列表
   */
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

  /**
   * 获取所有数据库字段名数组
   *
   * @return 数据库字段名数组
   */
  public String[] columns() {

    return map.keySet().toArray(new String[]{});
  }

  /**
   * 添加或替换指定数据库字段名的 {@link Attribute}
   *
   * @param columnName 数据库字段名
   * @param value      {@link Attribute} 实例
   * @return 之前与该字段名关联的 {@link Attribute}，如果没有则返回 {@code null}
   */
  public Attribute put(String columnName, Attribute value) {

    if (StringUtil.isEmpty(columnName))
      throw new InvalidDataAccessException("数据库字段名不能为空! ");

    Objects.requireNonNull(value);

    return map.put(columnName.toUpperCase(), value);
  }

  /**
   * 遍历所有字段名与 {@link Attribute} 的键值对
   *
   * @param action 对每个键值对执行的操作
   */
  public void forEach(BiConsumer<String, Attribute> action) {
    map.forEach(action);
  }

  /**
   * 判断是否存在指定数据库字段名
   *
   * @param columnName 数据库字段名
   * @return 存在返回 {@code true}
   */
  public boolean containsColumn(String columnName) {

    if (StringUtil.isEmpty(columnName))
      throw new InvalidDataAccessException("数据库字段名不能为空! ");

    return map.containsKey(columnName.toUpperCase());
  }

  /**
   * 获取所有 {@link Attribute} 的集合
   *
   * @return {@link Attribute} 集合
   */
  public Collection<Attribute> values() {

    return map.values();
  }

  /**
   * 获取字段数量
   *
   * @return 字段数量
   */
  public int size() {

    return map.size();
  }
}
