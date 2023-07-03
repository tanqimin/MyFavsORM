package work.myfavs.framework.orm.util;

import cn.hutool.core.collection.CollectionUtil;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {
  public static Object[] toObjectArray(Object array) {
    if (array == null) return new Object[] {};
    Class<?> ofArray = array.getClass().getComponentType();
    if (ofArray.isPrimitive()) {
      List<Object> ar = new ArrayList<>();
      int length = Array.getLength(array);
      for (int i = 0; i < length; i++) {
        ar.add(Array.get(array, i));
      }
      return ar.toArray();
    } else {
      return (Object[]) array;
    }
  }

  public static List<Object> toObjectList(Object array) {
    if (array == null) return new ArrayList<>();
    Class<?> ofArray = array.getClass().getComponentType();
    if (ofArray.isPrimitive()) {
      List<Object> ar = new ArrayList<>();
      int length = Array.getLength(array);
      for (int i = 0; i < length; i++) {
        ar.add(Array.get(array, i));
      }
      return ar;
    } else {
      return CollectionUtil.toList((Object[]) array);
    }
  }
}
