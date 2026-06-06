package work.myfavs.framework.orm.util.common;

import java.util.Iterator;

/**
 * 迭代器包装类，提供对 {@link java.util.Iterator} 的简化访问，支持按需获取当前元素
 *
 * @param <E> 元素类型泛型
 */
public class Enumerator<E> {
  private final Iterator<E> iterator;
  private       E           current = null;

  /**
   * 构造 {@link Enumerator} 实例
   *
   * @param iterator 被包装的迭代器
   */
  public Enumerator(Iterator<E> iterator) {
    this.iterator = iterator;
  }

  /**
   * 移动到下一个元素
   *
   * @return 存在下一个元素返回 {@code true}，否则返回 {@code false}
   */
  public boolean next() {
    if (iterator.hasNext()) {
      this.current = iterator.next();
      return true;
    } else {
      return false;
    }
  }

  /**
   * 获取当前元素
   *
   * @return 当前元素，若未调用 {@link #next()} 或已迭代完毕可能返回 {@code null}
   */
  public E getCurrent() {
    return current;
  }
}
