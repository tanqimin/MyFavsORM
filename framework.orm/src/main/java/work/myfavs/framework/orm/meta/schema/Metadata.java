package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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

  private static final Map<String, ClassMeta> CLASS_META_CACHE = new HashMap<>();
  private static final Object SYNC_LOCK = new Object();

  private Metadata() {}

  /**
   * 解析指定类为类元数据，并放入缓存中
   *
   * @param clazz 目标类
   * @return 类元数据
   */
  public static ClassMeta get(Class<?> clazz) {


    ClassMeta classMeta = CLASS_META_CACHE.get(clazz.getName());
    if (Objects.isNull(classMeta)) {
      synchronized (SYNC_LOCK) {
        classMeta = CLASS_META_CACHE.get(clazz.getName());
        if (Objects.isNull(classMeta)) {
          if (log.isDebugEnabled())
            log.debug(StrUtil.format("ClassMeta : {} keys not exists.", clazz.getName()));
          classMeta = ClassMeta.createInstance(clazz);
          CLASS_META_CACHE.put(clazz.getName(), classMeta);
        }
      }
    }
    return classMeta;
  }
}
