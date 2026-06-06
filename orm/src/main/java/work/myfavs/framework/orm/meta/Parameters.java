package work.myfavs.framework.orm.meta;

import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DataRetrievalException;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SQL 参数封装，维护参数索引到参数值的映射，支持批量设置和应用到 {@link PreparedStatement}
 */
public class Parameters {
  private final Map<Integer/*paramIndex*/, Object> parameters = new LinkedHashMap<>();

  /**
   * 批量添加参数，索引按顺序自增
   *
   * @param params 参数集合
   */
  public void addParameters(Collection<?> params) {
    if (CollectionUtil.isEmpty(params)) return;

    int paramIndex = parameters.size();
    for (Object param : params) {
      parameters.put(++paramIndex, param);
    }
  }

  /**
   * 添加指定索引的参数
   *
   * @param paramIndex 参数索引（从 1 开始）
   * @param param      参数值
   * @throws work.myfavs.framework.orm.util.exception.DBException 如果索引已存在则抛出异常
   */
  public void addParameter(int paramIndex, Object param) {
    if (parameters.containsKey(paramIndex))
      throw new InvalidDataAccessException("设置参数 %s 时出现异常: 参数索引 %d 已存在! ", StringUtil.toStr(param), paramIndex);

    parameters.put(paramIndex, param);
  }

  /**
   * 添加参数，索引按当前大小自增
   *
   * @param param 参数值
   */
  public void addParameter(Object param) {
    int paramIndex = parameters.size();
    parameters.put(++paramIndex, param);
  }

  /**
   * 判断是否没有参数
   *
   * @return 无参数返回 {@code true}
   */
  public boolean isEmpty() {
    return parameters.isEmpty();
  }

  /**
   * 将所有参数应用到 {@link PreparedStatement}
   *
   * @param statement {@link PreparedStatement} 实例
   * @throws work.myfavs.framework.orm.util.exception.DBException 设置参数时发生异常
   */
  @SuppressWarnings("unchecked")
  public void applyParameters(PreparedStatement statement) {
    if (isEmpty()) return;

    try {
      for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
        Integer paramIndex = entry.getKey();
        Object  value      = entry.getValue();

        if (null == value) {
          statement.setObject(paramIndex, null);
          continue;
        }

        PropertyHandlerFactory
            .getInstance(value.getClass())
            .addParameter(statement, paramIndex, value);
      }
    } catch (SQLException ex) {
      throw new DataRetrievalException(ex, "设置参数时发生异常: %s", ex.getMessage());
    }
  }

  /**
   * 获取参数索引到参数值的映射
   *
   * @return 参数映射
   */
  public Map<Integer, Object> getParameters() {
    return this.parameters;
  }

  /**
   * 获取参数数量
   *
   * @return 参数数量
   */
  public int size() {
    return this.parameters.size();
  }
}
