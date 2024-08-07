package work.myfavs.framework.orm;

import work.myfavs.framework.orm.meta.BatchParameters;
import work.myfavs.framework.orm.meta.SqlLog;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.convert.DBConvert;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingRunnable;

import java.io.Closeable;
import java.sql.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 对 JDBC 查询基本封装
 */
public class Query implements Closeable {

  private final Database          database;
  private final int               batchSize;
  private final int               fetchSize;
  private final SqlLog            sqlLog;
  private       PreparedStatement preparedStatement;
  private       String            sql;
  private       boolean           autoGeneratedPK     = false;
  private       boolean           alreadySetFetchSize = false;


  //批量查询参数
  private final BatchParameters batchParameters = new BatchParameters();

  /**
   * 构造方法，推荐使用 {@link Database#createQuery(String, boolean)} 创建示例
   *
   * @param database        {@link Database}
   * @param sql             SQL语句
   * @param autoGeneratedPK 是否自动生成主键
   */
  public Query(Database database, String sql, boolean autoGeneratedPK) {

    this.database = database;
    this.batchSize = database.getDbConfig().getBatchSize();
    this.fetchSize = database.getDbConfig().getFetchSize();
    this.sqlLog = new SqlLog(
        database.getDbConfig().getShowSql(),
        database.getDbConfig().getShowResult()
    );

    createQuery(sql, autoGeneratedPK);
  }

  /**
   * 创建一个新的 Query 对象(默认不自动生成主键)
   *
   * @param sql SQL语句
   * @return {@link Query}
   */
  public Query createQuery(String sql) {

    return createQuery(sql, false);
  }

  /**
   * 创建一个新的 Query 对象
   *
   * @param sql             SQL语句
   * @param autoGeneratedPK 是否自动生成主键
   * @return {@link Query}
   */
  public Query createQuery(String sql, boolean autoGeneratedPK) {

    this.sql = sql;
    this.autoGeneratedPK = autoGeneratedPK;
    this.alreadySetFetchSize = false;
    this.clearParameters();
    this.closePreparedStatement();
    this.sqlLog.showSql(sql);
    return this;
  }

  /**
   * 创建 PreparedStatement
   *
   * @return {@link PreparedStatement}
   */
  private PreparedStatement createPreparedStatement() {

    if (null != this.preparedStatement) return this.preparedStatement;

    try {
      if (this.autoGeneratedPK) {
        return this.preparedStatement = getConnection().prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
      }

      return this.preparedStatement = getConnection().prepareStatement(this.sql);
    } catch (SQLException e) {
      throw new DBException(e, "创建 preparedStatement 时发生异常: %s", e.getMessage());
    }
  }

  private Connection getConnection() {

    return this.database.getConnection();
  }

  /**
   * 批量增加参数
   *
   * @param params 参数集合
   * @return {@link Query}
   */
  public Query addParameters(Collection<?> params) {

    if (CollectionUtil.isEmpty(params)) return this;

    this.batchParameters.getCurrentBatchParameters().addParameters(params);
    return this;
  }

  /**
   * 增加参数
   *
   * @param paramIndex 参数序号，从 1 开始
   * @param param      参数
   * @return {@link Query}
   */
  public Query addParameter(int paramIndex, Object param) {

    this.batchParameters.getCurrentBatchParameters().addParameter(paramIndex, param);
    return this;
  }

  /**
   * 增加参数，根据现有参数数量设置序号
   *
   * @param param 参数
   * @return {@link Query}
   */
  public Query addParameter(Object param) {

    this.batchParameters.getCurrentBatchParameters().addParameter(param);
    return this;
  }

  /**
   * 执行查询，并返回指定类型的实体集合
   *
   * @param modelClass 实体类型
   * @param <TModel>   实体类型泛型
   * @return 实体集合
   */
  public <TModel> List<TModel> find(Class<TModel> modelClass) {

    final PreparedStatement preparedStatement = createPreparedStatement();

    this.setFetchSize(preparedStatement);
    this.applyParameters(preparedStatement);
    this.showParameters();

    try (final ResultSet resultSet = this.execQuery(preparedStatement)) {
      return this.convertToList(modelClass, resultSet);
    } catch (SQLException ex) {
      throw new DBException(ex, "执行 executeQuery 查询时发生异常: %s", ex.getMessage());
    } finally {
      this.clearParameters();
    }
  }

  /**
   * 执行查询，并返回指定类型的实体
   *
   * @param modelClass 实体类型
   * @param <TModel>   实体类型泛型
   * @return 实体
   */
  public <TModel> TModel get(Class<TModel> modelClass) {

    final Iterator<TModel> iterator = find(modelClass).iterator();
    return iterator.hasNext() ? iterator.next() : null;
  }

  /**
   * 执行查询，返回影响行数
   *
   * @param configConsumer 在执行查询前允许，可对 PreparedStatement 进行设置
   * @param keysConsumer   在执行查询后执行，对于 {@code autoGeneratedPK = true} 则可返回 {@link ResultSet} 获取生成的 Key 值
   * @return 影响行数
   */
  public int execute(ThrowingConsumer<PreparedStatement, SQLException> configConsumer,
                     ThrowingConsumer<ResultSet, SQLException> keysConsumer) {

    final PreparedStatement preparedStatement = createPreparedStatement();

    try {
      if (null != configConsumer)
        configConsumer.accept(preparedStatement);
      this.applyParameters(preparedStatement);
      this.showParameters();

      final int result = execUpdate(preparedStatement);
      this.generatedKeys(preparedStatement, keysConsumer);
      return result;
    } catch (SQLException e) {
      throw new DBException(e, "执行 executeUpdate 查询时发生异常: %s", e.getMessage());
    } finally {
      this.clearParameters();
    }
  }

  /**
   * 执行查询，返回影响行数
   *
   * @return 影响行数
   */
  public int execute() {

    return this.execute(null, null);
  }

  /**
   * 调用 {@link PreparedStatement#addBatch()} 并提交执行
   */
  public void addBatch() {

    this.batchParameters.addBatch();
  }

  /**
   * 执行批量查询，返回数组，包含每个查询的影响行数
   *
   * @param keysConsumer 在执行查询后执行，对于 {@code autoGeneratedPK = true} 则可返回 {@link ResultSet} 获取生成的 Key 值
   * @return 返回数组，包含每个查询的影响行数
   */
  public int[] executeBatch(ThrowingConsumer<ResultSet, SQLException> keysConsumer) {

    final PreparedStatement preparedStatement = createPreparedStatement();

    try {
      this.applyBatchParameters(preparedStatement);
      this.showParameters();

      final int[] result = execBatch(preparedStatement);
      this.generatedKeys(preparedStatement, keysConsumer);
      return result;
    } catch (SQLException e) {
      throw new DBException(e, "执行 executeBatch 查询时发生异常: %s", e.getMessage());
    } finally {
      this.clearParameters();
    }
  }

  /**
   * 执行批量查询，返回数组，包含每个查询的影响行数
   *
   * @return 返回数组，包含每个查询的影响行数
   */
  public int[] executeBatch() {

    return this.executeBatch(null);
  }

  /**
   * 释放 PreparedStatement
   */
  @Override
  public void close() {

    this.closePreparedStatement();
  }

  /**
   * 设置 FetchSize
   *
   * @param preparedStatement {@link PreparedStatement}
   */
  private void setFetchSize(PreparedStatement preparedStatement) {
    if (alreadySetFetchSize || this.fetchSize <= 0) return;

    try {
      preparedStatement.setFetchSize(this.fetchSize);
      alreadySetFetchSize = true;
    } catch (SQLException ex) {
      throw new DBException(ex, "设置 fetch size 时发生异常: %s", ex.getMessage());
    }
  }

  /**
   * 执行查询，并进行性能统计
   *
   * @param preparedStatement {@link PreparedStatement}
   * @return {@link ResultSet}
   * @throws SQLException 执行查询过程抛出的异常
   */
  private ResultSet execQuery(PreparedStatement preparedStatement) throws SQLException {

    final long start     = System.currentTimeMillis();
    ResultSet  resultSet = preparedStatement.executeQuery();
    final long end       = System.currentTimeMillis();

    this.sqlLog.showResult("执行 executeQuery 查询消耗时间: {} ms", end - start);
    return resultSet;
  }

  /**
   * 转换 {@link ResultSet} 为指定类型的实体集合
   *
   * @param modelClass 目标集合成员类型
   * @param resultSet  {@link ResultSet}
   * @param <TModel>   目标集合成员类型泛型
   * @return 指定类型的实体集合
   * @throws SQLException 转换过程抛出的异常
   */
  private <TModel> List<TModel> convertToList(Class<TModel> modelClass, ResultSet resultSet) throws SQLException {

    final long   start  = System.currentTimeMillis();
    List<TModel> result = DBConvert.toList(modelClass, resultSet);
    final long   end    = System.currentTimeMillis();

    this.sqlLog.showResult(modelClass, result);
    this.sqlLog.showResult("ResultSet 转换成 List<{}> 消耗时间: {} ms", modelClass.getSimpleName(), end - start);
    return result;
  }


  /**
   * 生成主键值，并进行性能统计
   *
   * @param preparedStatement {@link PreparedStatement}
   * @return {@link ResultSet}
   * @throws SQLException 生成主键值过程抛出的异常
   */
  private ResultSet getGeneratedKeys(PreparedStatement preparedStatement) throws SQLException {

    final long start     = System.currentTimeMillis();
    ResultSet  resultSet = preparedStatement.getGeneratedKeys();
    final long end       = System.currentTimeMillis();

    this.sqlLog.showResult("生成主键消耗时间: {} ms", end - start);
    return resultSet;
  }

  /**
   * 执行更新，并进行性能统计
   *
   * @param preparedStatement {@link PreparedStatement}
   * @return 影响行数
   * @throws SQLException 执行更新过程抛出的异常
   */
  private Integer execUpdate(PreparedStatement preparedStatement) throws SQLException {

    final long start  = System.currentTimeMillis();
    int        result = preparedStatement.executeUpdate();
    final long end    = System.currentTimeMillis();

    this.sqlLog.showAffectedRows(result);
    this.sqlLog.showResult("执行 executeUpdate 查询消耗时间: {} ms", end - start);
    return result;
  }

  /**
   * 执行批量更新，并进行性能统计
   *
   * @param preparedStatement {@link PreparedStatement}
   * @return 返回数组，包含每个查询的影响行数
   * @throws SQLException 执行批量更新过程抛出的异常
   */
  private int[] execBatch(PreparedStatement preparedStatement) throws SQLException {

    final long start  = System.currentTimeMillis();
    int[]      result = preparedStatement.executeBatch();
    final long end    = System.currentTimeMillis();

    this.sqlLog.showAffectedRows(result.length);
    this.sqlLog.showResult("执行 executeBatch 查询消耗时间: {} ms", end - start);
    return result;
  }

  /**
   * 封装主键值转换为主键类型，并进行性能统计
   *
   * @param runnable 对转换主键值为主键类型的 {@link ThrowingRunnable} 封装
   * @throws SQLException 转换过程抛出的异常
   */
  private void acceptKeysRunnable(ThrowingRunnable<SQLException> runnable) throws SQLException {

    final long start = System.currentTimeMillis();
    runnable.run();
    final long end = System.currentTimeMillis();

    this.sqlLog.showResult("主键 ResultSet 转换消耗时间: {} ms", end - start);
  }

  /**
   * 把参数打印到日志
   */
  private void showParameters() {

    this.sqlLog.showParams(this.batchParameters);
  }

  /**
   * 把参数应用到 {@link PreparedStatement} 中
   *
   * @param preparedStatement {@link PreparedStatement}
   */
  private void applyParameters(PreparedStatement preparedStatement) {

    this.batchParameters.applyParameters(preparedStatement);
  }

  /**
   * 把批量参数应用到 {@link PreparedStatement} 中
   *
   * @param preparedStatement {@link PreparedStatement}
   */
  private void applyBatchParameters(PreparedStatement preparedStatement) {

    this.batchParameters.applyBatchParameters(preparedStatement, this.batchSize);
  }

  /**
   * 清空参数
   */
  private void clearParameters() {

    this.batchParameters.clear();
  }

  /**
   * 生成主键
   *
   * @param preparedStatement {@link PreparedStatement}
   * @param keysConsumer      在执行查询后执行，对于 {@code autoGeneratedPK = true} 则可返回 {@link ResultSet} 获取生成的 Key 值
   * @throws SQLException 异常 {@see SQLException}
   */
  private void generatedKeys(PreparedStatement preparedStatement,
                             ThrowingConsumer<ResultSet, SQLException> keysConsumer) throws SQLException {

    if (null == keysConsumer) return;

    if (!this.autoGeneratedPK) return;

    try (final ResultSet resultSet = getGeneratedKeys(preparedStatement)) {
      this.acceptKeysRunnable(() -> keysConsumer.accept(resultSet));
    }
  }

  /**
   * 关闭 {@link PreparedStatement}
   */
  private void closePreparedStatement() {

    if (null == this.preparedStatement) return;

    try {
      if (this.preparedStatement.isClosed()) return;

      this.preparedStatement.close();
    } catch (SQLException e) {
      throw new DBException(e, "关闭 preparedStatement 时发生异常: %s", e.getMessage());
    } finally {
      this.preparedStatement = null;
    }
  }
}
