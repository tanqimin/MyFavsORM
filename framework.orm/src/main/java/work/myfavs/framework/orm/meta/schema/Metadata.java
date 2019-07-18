package work.myfavs.framework.orm.meta.schema;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Metadata {

  private final static ConcurrentMap<String, ClassMeta> CLASS_META_CACHE = new ConcurrentHashMap<>();

  private Metadata() {

  }

  /**
   * 解析指定类为类元数据，并放入缓存中
   *
   * @param clazz 目标类
   *
   * @return 类元数据
   */
  public static ClassMeta get(Class<?> clazz) {

    String clazzName = clazz.getName();

    if (CLASS_META_CACHE.containsKey(clazzName)) {
      return CLASS_META_CACHE.get(clazzName);
    }

    ClassMeta classMeta = ClassMeta.createInstance(clazz);
    CLASS_META_CACHE.put(clazzName, classMeta);

    return classMeta;
  }

}
