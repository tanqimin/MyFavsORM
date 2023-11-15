package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.HashMap;
import java.util.Map;

/**
 * 元数据构建
 *
 * @author tanqimin
 */
public class Metadata {

  private static final Logger log = LoggerFactory.getLogger(Metadata.class);

  private static final Map<Class<?>, ClassMeta> CLASS_META_CACHE = new HashMap<>();

  private Metadata() {}

  /**
   * 解析指定类为类元数据，并放入缓存中
   *
   * @param clazz 目标类
   * @return 类元数据
   */
  public static ClassMeta get(Class<?> clazz) {
    return CLASS_META_CACHE.computeIfAbsent(clazz, key -> {
      log.debug("ClassMeta : {} keys not exists.", clazz.getName());
      return ClassMeta.createInstance(clazz);
    });
  }

  /**
   * 获取实体类的元数据（必须使用@Table）
   *
   * @param clazz 实体类
   * @return 类元数据
   */
  public static ClassMeta entityMeta(Class<?> clazz) {
    ClassMeta classMeta = get(clazz);
    if (StrUtil.isEmpty(classMeta.getTableName()))
      throw new DBException("Class {} is not an entity class", clazz.getName());
    return classMeta;
  }
}
