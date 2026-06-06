package work.myfavs.framework.orm.meta.enumeration;

/**
 * 用于描述@Cond中的Operator
 */
public enum Operator {
  /**
   * 等于，如果条件值为null时，会忽略条件
   */
  EQUALS,
  /**
   * 等于，如果条件值为null时，会生成条件{@code field IS NULL}
   */
  EQUALS_INC_NULL,
  /**
   * 不等于，如果条件值为null时，会忽略条件
   */
  NOT_EQUALS,
  /**
   * 不等于，如果条件值为null时，会生成条件{@code field IS NOT NULL}
   */
  NOT_EQUALS_INC_NULL,
  /**
   * 模糊查询，需手动补充通配符
   */
  LIKE,
  /**
   * 为空
   */
  IS_NULL,
  /**
   * 不为空
   */
  IS_NOT_NULL,
  /**
   * 大于
   */
  GREATER_THAN,
  /**
   * 大于等于
   */
  GREATER_THAN_OR_EQUALS,
  /**
   * 小于
   */
  LESS_THAN,
  /**
   * 小于等于
   */
  LESS_THAN_OR_EQUALS,
  /**
   * 大于等于
   */
  BETWEEN_START,
  /**
   * 小于等于
   */
  BETWEEN_END,
  /**
   * IN
   */
  IN,
  /**
   * IN，如果集合为空，会产生 {@code 1 > 2} 语句
   */
  IN_INC_EMPTY,
  /**
   * NOT IN
   */
  NOT_IN,
  /**
   * NOT IN，如果集合为空，会产生 {@code 1 > 2} 语句
   */
  NOT_IN_INC_EMPTY
  /*
  EXISTS,
  NOT_EXISTS
  */
}
