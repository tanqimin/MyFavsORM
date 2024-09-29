package work.myfavs.framework.orm.util.common;

import java.util.*;
import java.util.function.Function;

/**
 * 集合工具类
 */
public class CollectionUtil {

  /**
   * 判断集合是否为空
   *
   * @param collection 集合
   * @param <T>        集合类型泛型
   * @return 如果为空返回 {@code true}
   */
  public static <T> boolean isEmpty(Collection<T> collection) {
    return collection == null || collection.isEmpty();
  }

  /**
   * 根据给定的大小分割集合
   *
   * @param collection 需要分割的集合
   * @param size       分割后的集合大小
   * @param <T>        集合类型泛型
   * @return 返回分割后的集合 {@code List<List<T>>}
   */
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

  /**
   * 判断集合是否不为空
   *
   * @param collection 集合
   * @param <T>        集合类型泛型
   * @return 如果集合不为空返回 {@code true}
   */
  public static <T> boolean isNotEmpty(Collection<T> collection) {
    return !isEmpty(collection);
  }

  /**
   * 把集合转换为指定分隔符分隔的字符串
   *
   * @param iterable    集合
   * @param conjunction 分隔符
   * @param <T>         集合类型泛型
   * @return 指定分隔符分隔的字符串
   */
  public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
    return join(iterable, conjunction, Object::toString);
  }

  /**
   * 把集合转换为指定分隔符分隔的字符串
   *
   * @param iterable    集合
   * @param conjunction 分隔符
   * @param func        集合处理函数
   * @param <T>         集合类型泛型
   * @return 指定分隔符分隔的字符串
   */
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

  /**
   * 转换成 {@link Collection} 集合
   *
   * @param obj1 成员1
   * @param objs 成员数组
   * @param <T>  集合成员类型泛型
   * @return {@link Collection} 集合
   */
  @SafeVarargs
  public static <T> Collection<T> toCollection(T obj1, T... objs) {
    Collection<T> result = new ArrayList<>();
    result.add(obj1);
    if (ArrayUtil.isNotEmpty(objs)) {
      Collections.addAll(result, objs);
    }
    return result;
  }
}
