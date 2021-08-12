package work.myfavs.framework.orm.util.common;

import io.vavr.Tuple3;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 包含两个Key(Key1,Key2)的类似Map的数据结构封装 Key1和Key2在各自的范围内不允许重复，且不为null
 *
 * @param <TKey1> Key1
 * @param <TKey2> Key2
 * @param <TValue> Value
 */
public class MultiKeyMap<TKey1, TKey2, TValue> implements Serializable {

  private final Set<Tuple3<TKey1, TKey2, TValue>> data = new HashSet<>();

  /**
   * 获取集合大小
   *
   * @return 集合大小
   */
  public int size() {
    return data.size();
  }

  /**
   * 获取集合是否为空？
   *
   * @return 如果集合为空，返回true，否则返回false
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * 判断集合Key1是否包含指定键值
   *
   * @param key Key1
   * @return 如果包含指定键值，返回true，否则返回false
   */
  public boolean containsKey1(TKey1 key) {
    Objects.requireNonNull(key);
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    while (i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key.equals(e._1)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断集合Key2是否包含指定键值
   *
   * @param key Key2
   * @return 如果包含指定键值，返回true，否则返回false
   */
  public boolean containsKey2(TKey2 key) {
    Objects.requireNonNull(key);
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    while (i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key.equals(e._2)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断集合是否包含指定值
   *
   * @param value value
   * @return 如果包含指定值，返回true，否则返回false
   */
  public boolean containsValue(TValue value) {
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    if (value == null) {
      while (i.hasNext()) {
        final Tuple3<TKey1, TKey2, TValue> e = i.next();
        if (e._3 == null) {
          return true;
        }
      }
    } else {
      while (i.hasNext()) {
        final Tuple3<TKey1, TKey2, TValue> e = i.next();
        if (value.equals(e._3)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 根据Key1获取值
   *
   * @param key Key1
   * @return 值
   */
  public TValue getByKey1(TKey1 key) {
    Objects.requireNonNull(key);
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    while (i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key.equals(e._1)) {
        return e._3;
      }
    }
    return null;
  }

  /**
   * 根据Key2获取值
   *
   * @param key Key2
   * @return 值
   */
  public TValue getByKey2(TKey2 key) {
    Objects.requireNonNull(key);
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    while (i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key.equals(e._2)) {
        return e._3;
      }
    }
    return null;
  }

  public Set<TKey1> key1Set() {
    return data.parallelStream().map(t -> t._1).collect(Collectors.toSet());
  }

  public Set<TKey2> key2Set() {
    return data.parallelStream().map(t -> t._2).collect(Collectors.toSet());
  }

  public Collection<TValue> values() {
    return data.parallelStream().map(t -> t._3).collect(Collectors.toList());
  }

  /**
   * 如果存在Key1键，则替换原来的Value，并返回旧Value， 否则把Value添加到新集合中，并返回null
   *
   * @param key1 Key1
   * @param key2 Key2
   * @param value 值
   * @return 返回添加前的值
   */
  public TValue putByKey1(TKey1 key1, TKey2 key2, TValue value) {
    Objects.requireNonNull(key1);

    Tuple3<TKey1, TKey2, TValue> correct = null;
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    while (correct != null && i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key1.equals(e._1)) {
        correct = e;
      }
    }

    if (correct != null) {
      TValue oldValue = correct._3;
      correct.update2(key2);
      correct.update3(value);
      return oldValue;
    }

    correct = new Tuple3<>(key1, key2, value);
    data.add(correct);
    return null;
  }

  /**
   * 如果存在Key2键，则替换原来的Value，并返回旧Value， 否则把Value添加到新集合中，并返回null
   *
   * @param key1 Key1
   * @param key2 Key2
   * @param value 值
   * @return 返回添加前的值
   */
  public TValue putByKey2(TKey1 key1, TKey2 key2, TValue value) {
    Objects.requireNonNull(key2);

    Tuple3<TKey1, TKey2, TValue> correct = null;
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    while (correct != null && i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key2.equals(e._2)) {
        correct = e;
      }
    }

    if (correct != null) {
      TValue oldValue = correct._3;
      correct.update1(key1);
      correct.update3(value);
      return oldValue;
    }

    correct = new Tuple3<>(key1, key2, value);
    data.add(correct);
    return null;
  }

  /**
   * 根据Key1删除成员，并返回删除的Value
   *
   * @param key Key1
   * @return 值
   */
  public TValue removeByKey1(TKey1 key) {
    Objects.requireNonNull(key);
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    Tuple3<TKey1, TKey2, TValue> correct = null;
    while (correct == null && i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key.equals(e._1)) {
        correct = e;
      }
    }

    TValue oldValue = null;
    if (correct != null) {
      oldValue = correct._3;
      i.remove();
    }
    return oldValue;
  }

  /**
   * 根据Key2删除成员，并返回删除的Value
   *
   * @param key Key1
   * @return 值
   */
  public TValue removeByKey2(TKey2 key) {
    Objects.requireNonNull(key);
    final Iterator<Tuple3<TKey1, TKey2, TValue>> i = data.iterator();
    Tuple3<TKey1, TKey2, TValue> correct = null;
    while (correct == null && i.hasNext()) {
      final Tuple3<TKey1, TKey2, TValue> e = i.next();
      if (key.equals(e._2)) {
        correct = e;
      }
    }

    TValue oldValue = null;
    if (correct != null) {
      oldValue = correct._3;
      i.remove();
    }
    return oldValue;
  }

  public void putAllByKey1(MultiKeyMap<TKey1, TKey2, TValue> map) {
    for (Tuple3<TKey1, TKey2, TValue> t : map.data) {
      putByKey1(t._1, t._2, t._3);
    }
  }

  public void putAllByKey2(MultiKeyMap<TKey1, TKey2, TValue> map) {
    for (Tuple3<TKey1, TKey2, TValue> t : map.data) {
      putByKey2(t._1, t._2, t._3);
    }
  }

  public void clear() {
    data.clear();
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof MultiKeyMap)) {
      return false;
    }
    MultiKeyMap<TKey1, TKey2, TValue> m = (MultiKeyMap<TKey1, TKey2, TValue>) o;
    if (m.size() != size()) {
      return false;
    }

    try {
      for (Tuple3<TKey1, TKey2, TValue> e : data) {
        TKey1 key1 = e._1;
        TKey2 key2 = e._2;
        TValue value = e._3;
        if (value == null) {
          if (!(m.getByKey1(key1) == null && m.containsKey1(key1))) {
            return false;
          }
          if (!(m.getByKey2(key2) == null && m.containsKey2(key2))) {
            return false;
          }
        } else {
          if (!value.equals(m.getByKey1(key1))) {
            return false;
          }
          if (!value.equals(m.getByKey2(key2))) {
            return false;
          }
        }
      }
    } catch (ClassCastException unused) {
      return false;
    } catch (NullPointerException unused) {
      return false;
    }

    return true;
  }
}
