package work.myfavs.framework.orm.util.common;

import java.util.Iterator;

public class Enumerator<E> {
  private final Iterator<E> iterator;
  private       E           current = null;

  public Enumerator(Iterator<E> iterator) {
    this.iterator = iterator;
  }

  public boolean next() {
    if (iterator.hasNext()) {
      this.current = iterator.next();
      return true;
    } else {
      return false;
    }
  }

  public E getCurrent() {
    return current;
  }
}
