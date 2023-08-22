package work.myfavs.framework.orm.util.convert;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class ObjectConvert {
  public static Collection<?> toCollection(Object object){
    Collection<Object> collection = new ArrayList<>();
    if(object == null) return collection;

    if(ArrayUtil.isArray(object)){
      Class<?> componentType = object.getClass().getComponentType();
      if(componentType.isPrimitive()){
        int size = Array.getLength(object);
        for(int i = 0; i < size; i++) {
          collection.add(Array.get(object, i));
        }
        return collection;
      } else {
        return CollectionUtil.toList((Object[]) object);
      }
    } else if(object instanceof Collection<?>){
      return (Collection<?>) object;
    }

    throw new IllegalArgumentException(StrUtil.format("The argument (Type: {}) can't convert to Collection", object.getClass().getName()));
  }
}
