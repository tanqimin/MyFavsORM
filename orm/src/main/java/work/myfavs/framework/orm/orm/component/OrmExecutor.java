package work.myfavs.framework.orm.orm.component;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * SQL 执行器，封装 JDBC 执行逻辑。
 * <p>提供 {@code execute} 方法的重载体系，支持：</p>
 * <ul>
 *   <li>原生 SQL + 参数 + 超时时间</li>
 *   <li>{@link Sql} 构建器对象</li>
 *   <li>多语句批量执行（共享 {@link Query} 连接）</li>
 * </ul>
 */
public class OrmExecutor {

  private static final ThrowingConsumer<PreparedStatement, SQLException> NOOP = ps -> {
  };

  private final Database database;

  /**
   * 构造 OrmExecutor 实例.
   *
   * @param database {@link Database} 实例，用于创建 {@link Query}
   */
  public OrmExecutor(Database database) {
    this.database = database;
  }

  /**
   * 执行 SQL，返回影响行数，允许对 {@link PreparedStatement} 进行额外配置。
   *
   * @param sql            SQL 语句
   * @param params         参数集合
   * @param configConsumer PreparedStatement 配置回调（如设置超时、fetchSize 等）
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params, ThrowingConsumer<PreparedStatement, SQLException> configConsumer) {
    try (Query query = this.database.createQuery(sql)) {
      return query.addParameters(params).execute(configConsumer, null);
    }
  }

  /**
   * 执行 SQL，返回影响行数。
   *
   * @param sql     SQL 语句
   * @param params  参数集合
   * @param timeout 查询超时时间（秒）
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params, int timeout) {
    return execute(sql, params, preparedStatement -> preparedStatement.setQueryTimeout(timeout));
  }

  /**
   * 执行 SQL，返回影响行数。
   *
   * @param sql    SQL 语句
   * @param params 参数集合
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params) {
    return execute(sql, params, NOOP);
  }

  /**
   * 执行 {@link Sql}，返回影响行数。
   *
   * @param sql     {@link Sql} 语句
   * @param timeout 查询超时时间（秒）
   * @return 影响行数
   */
  public int execute(Sql sql, int timeout) {
    return this.execute(sql.toString(), sql.getParams(), preparedStatement -> preparedStatement.setQueryTimeout(timeout));
  }

  /**
   * 执行 {@link Sql}，返回影响行数。
   *
   * @param sql {@link Sql} 语句
   * @return 影响行数
   */
  public int execute(Sql sql) {
    return this.execute(sql.toString(), sql.getParams(), NOOP);
  }

  /**
   * 批量执行多个 {@link Sql} 语句。
   * <p>所有语句共享同一个 {@link Query} 连接，依次执行。</p>
   *
   * @param sqlList {@link Sql} 语句集合
   * @return 影响行数数组，顺序与 {@code sqlList} 一致
   */
  public int[] execute(List<Sql> sqlList) {
    return this.execute(sqlList, null);
  }

  /**
   * 批量执行多个 {@link Sql} 语句。
   *
   * @param sqlList {@link Sql} 语句集合
   * @param timeout 查询超时时间（秒）
   * @return 影响行数数组
   */
  public int[] execute(List<Sql> sqlList, int timeout) {
    return this.execute(sqlList, ps -> ps.setQueryTimeout(timeout));
  }

  /**
   * 批量执行多个 {@link Sql} 语句，允许对 {@link PreparedStatement} 进行额外配置。
   * <p>所有语句共享同一个 {@link Query} 连接，依次执行。</p>
   *
   * @param sqlList        {@link Sql} 语句集合
   * @param configConsumer PreparedStatement 配置回调
   * @return 影响行数数组
   */
  public int[] execute(List<Sql> sqlList, ThrowingConsumer<PreparedStatement, SQLException> configConsumer) {
    final int sqlCnt = sqlList.size();
    final int[] results = new int[sqlCnt];

    if (CollectionUtil.isEmpty(sqlList)) return results;

    Iterator<Sql> iterator = sqlList.iterator();
    int index = 0;
    Sql sql = iterator.next();
    try (Query query = database.createQuery(sql.toString())) {
      results[index++] = query
          .addParameters(sql.getParams())
          .execute(configConsumer, null);
      while (iterator.hasNext()) {
        Sql next = iterator.next();
        results[index++] = query
            .createQuery(next.toString())
            .addParameters(next.getParams())
            .execute(configConsumer, null);
      }
    }

    return results;
  }
}
