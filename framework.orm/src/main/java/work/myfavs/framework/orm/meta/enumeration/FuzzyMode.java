package work.myfavs.framework.orm.meta.enumeration;

import work.myfavs.framework.orm.meta.clause.Cond;

/**
 * 模糊查询模式，在 {@link Cond#}
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
