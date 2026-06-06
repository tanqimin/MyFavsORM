package work.myfavs.framework.orm.meta;

import work.myfavs.framework.orm.util.exception.DataRetrievalException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 批量参数封装，维护多组 {@link Parameters} 索引，用于 JDBC 批量操作
 */
public class BatchParameters {
  private final Map<Integer/*batchIndex*/, Parameters> batchParameters = new LinkedHashMap<>();

  private int currentBatchSize;

  public BatchParameters() {
    currentBatchSize = 1;
    this.put(this.currentBatchSize, new Parameters());
  }

  /**
   * 获取当前批次的参数
   *
   * @return 当前批次的 {@link Parameters} 实例
   */
  public Parameters getCurrentBatchParameters() {
    return batchParameters.get(this.currentBatchSize);
  }

  /**
   * 获取所有批次的参数映射
   *
   * @return 批次索引到 {@link Parameters} 的映射
   */
  public Map<Integer, Parameters> getBatchParameters() {
    return this.batchParameters;
  }

  /**
   * 向当前批次添加参数集合
   *
   * @param params 参数集合
   */
  public void addParameters(Collection<?> params) {
    Parameters parameters = batchParameters.get(currentBatchSize);
    parameters.addParameters(params);
  }

  /**
   * 向当前批次添加指定索引的参数
   *
   * @param paramIndex 参数索引
   * @param param      参数值
   */
  public void addParameter(int paramIndex, Object param) {
    Parameters parameters = batchParameters.get(currentBatchSize);
    parameters.addParameter(paramIndex, param);
  }

  /**
   * 将当前批次的参数应用到 {@link PreparedStatement}
   *
   * @param statement {@link PreparedStatement} 实例
   */
  public void applyParameters(PreparedStatement statement) {
    getCurrentBatchParameters().applyParameters(statement);
  }

  /**
   * 将所有批次的参数批量应用到 {@link PreparedStatement} 并执行批量操作
   *
   * @param statement {@link PreparedStatement} 实例
   * @param batchSize 每执行多少批次后执行一次 {@link PreparedStatement#executeBatch()}，小于等于 0 表示最后统一执行
   */
  public void applyBatchParameters(PreparedStatement statement, int batchSize) {
    try {
      for (Map.Entry<Integer, Parameters> entry : batchParameters.entrySet()) {
        Parameters parameters = entry.getValue();
        if (parameters.isEmpty()) continue;

        parameters.applyParameters(statement);
        statement.addBatch();

        if (batchSize > 0 && entry.getKey() % batchSize == 0)
          statement.executeBatch();
      }
    } catch (SQLException ex) {
      throw new DataRetrievalException(ex, "设置批量参数时发生异常: %s", ex.getMessage());
    }
  }

  /**
   * 判断是否为批量模式
   *
   * @return 批量模式返回 {@code true}
   */
  public boolean isBatch() {
    return this.currentBatchSize > 1;
  }

  /**
   * 新增一个批次
   */
  public void addBatch() {
    this.batchParameters.put(++this.currentBatchSize, new Parameters());
  }

  /**
   * 清空所有批次参数并重置为初始状态
   */
  public void clear() {
    this.batchParameters.clear();
    this.put(this.currentBatchSize = 1, new Parameters());
  }

  private void put(int batchIndex, Parameters parameters) {
    this.batchParameters.put(batchIndex, parameters);
  }

  /**
   * 判断是否没有参数
   *
   * @return 无参数返回 {@code true}
   */
  public boolean isEmpty() {
    return this.batchParameters.size() == 1 && this.batchParameters.get(1).isEmpty();
  }
}
