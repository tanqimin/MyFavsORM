package work.myfavs.framework.orm.meta.schema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 元数据构建
 *
 * @author tanqimin
 */
public class Metadata {

  private static final Logger log = LoggerFactory.getLogger(Metadata.class);

  private static final Map<String, ClassMeta> CLASS_META_CACHE = new ConcurrentHashMap<>();

  private Metadata() {}

  /**
   * 解析指定类为类元数据，并放入缓存中
   *
   * @param clazz 目标类
   * @return 类元数据
   */
  public static ClassMeta get(Class<?> clazz) {

    return CLASS_META_CACHE.computeIfAbsent(
        clazz.getName(), className -> ClassMeta.createInstance(clazz));
  }
}
