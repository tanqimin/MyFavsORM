package work.myfavs.framework.orm.meta.schema;

import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 元数据构建
 *
 * @author tanqimin
 */
public class Metadata {

  private Metadata() {}

  /**
   * 解析指定类为类元数据，并放入缓存中
   *
   * @param clazz 目标类
   * @return 类元数据
   */
  public static ClassMeta classMeta(Class<?> clazz) {
    return ClassMeta.createInstance(clazz);
  }

  /**
   * 获取实体类的元数据（必须使用@Table）
   *
   * @param clazz 实体类
   * @return 类元数据
   */
  public static ClassMeta entityMeta(Class<?> clazz) {
    ClassMeta classMeta = classMeta(clazz);
    if (classMeta.isEntity())
      return classMeta;
    throw new DBException("类型 %s 不是实体类, 实体类必须使用 @Table 注释. ", clazz.getName());
  }
}
