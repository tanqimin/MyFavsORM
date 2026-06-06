package work.myfavs.framework.orm.meta.enumeration;

import work.myfavs.framework.orm.meta.clause.Cond;

/**
 * 模糊查询模式，用于 {@link Cond#like(String, Object, FuzzyMode)} 指定通配符匹配方式。
 */
public enum FuzzyMode {
  /**
   * 仅支持 {@code _} 单字符模糊
   */
  SINGLE,
  /**
   * 仅支持 {@code %} 多字符模糊
   */
  MULTIPLE,
  /**
   * 同时支持 {@code _} 单字符模糊 和 {@code %} 多字符模糊
   */
  ALL
}
