package work.myfavs.framework.orm.meta.clause;

/**
 * 用于IN、NOT IN查询条件
 */
public enum Mode {
  /**
   * 如果IN、NOT IN的参数集合为空，忽略该条件
   */
  IGNORE,

  /**
   * 如果IN、NOT IN的参数集合为空，创造条件1 > 2令查询不成立
   */
  NONE;
}
