package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * 元数据构建
 *
 * @author tanqimin
 */
public class Metadata {

  private static final Logger log = LoggerFactory.getLogger(Metadata.class);

  private static final Map<String, ClassMeta> CLASS_META_CACHE = new WeakHashMap<>();
  private static final Object SYNC_LOCK = new Object();

  private Metadata() {}

  /**
   * 解析指定类为类元数据，并放入缓存中
   *
   * @param clazz 目标类
   * @return 类元数据
   */
  public static ClassMeta get(Class<?> clazz) {
    final String className = clazz.getName();
    return CLASS_META_CACHE.computeIfAbsent(className, key -> {
      log.debug("ClassMeta : {} keys not exists.", className);
      return ClassMeta.createInstance(clazz);
    });
  }
}
