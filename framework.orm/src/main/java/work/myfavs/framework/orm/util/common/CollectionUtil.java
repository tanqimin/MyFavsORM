package work.myfavs.framework.orm.util.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class CollectionUtil {
  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T> List<List<T>> split(Collection<T> collection, int size) {
    final List<List<T>> result = new ArrayList<>();
    if (isEmpty(collection)) {
      return result;
    }

    final int initSize = Math.min(collection.size(), size);
    List<T>   subList  = new ArrayList<>(initSize);
    for (T t : collection) {
      if (subList.size() >= size) {
        result.add(subList);
        subList = new ArrayList<>(initSize);
      }
      subList.add(t);
    }
    result.add(subList);
    return result;
  }

  public static boolean isNotEmpty(Collection<?> collection) {
    return !isEmpty(collection);
  }

  public static <T> String join(Iterable<T> iterable, CharSequence conjunction, Function<T, ? extends CharSequence> func) {
    if (null == iterable) {
      return null;
    }
    final StringBuilder result   = new StringBuilder();
    Iterator<T>         iterator = iterable.iterator();
    if (iterator.hasNext()) {
      result.append(func.apply(iterator.next()));
      while (iterator.hasNext()) {
        result.append(conjunction).append(func.apply(iterator.next()));
      }
    }
    return result.toString();
  }
}
