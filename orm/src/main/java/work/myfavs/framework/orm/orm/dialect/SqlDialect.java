package work.myfavs.framework.orm.orm.dialect;

import com.alibaba.druid.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;

import java.util.Collection;
import java.util.List;

/**
 * 数据库方言接口，统一封装数据库相关的 SQL 行为。
 * <p>每个数据库类型对应一个实现，涵盖分页查询、UPSERT 语句和 Druid 类型映射。</p>
 * <p>扩展新数据库时只需创建新的 {@link SqlDialect} 实现，
 * 无需修改 {@link work.myfavs.framework.orm.orm.component.OrmSqlBuilder} 等核心组件。</p>
 */
public interface SqlDialect {

  /**
   * 获取 Druid 数据库类型，用于 COUNT 语句改写等操作。
   *
   * @return Druid {@link DbType}
   */
  DbType getDruidDbType();

  /**
   * 生成分页查询 SQL。
   *
   * @param sql         原始 SQL
   * @param params      SQL 参数
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页查询 {@link Sql}
   */
  Sql applyPageSql(String sql, Collection<?> params, int currentPage, int pageSize);

  /**
   * 生成 UPSERT（INSERT OR UPDATE）SQL 语句模板。
   * <p>参数占位符统一使用 {@code ?}，参数顺序为：{@code [pk, col1, col2, ..., logicDelete]}。</p>
   *
   * @param tableName   表名
   * @param columnNames 所有列名列表（含主键和逻辑删除列）
   * @param pkColumn    主键列名
   * @return UPSERT SQL 模板
   */
  String getUpsertSql(String tableName, List<String> columnNames, String pkColumn);

  /**
   * 生成 UPSERT SQL 语句模板（含自增主键标记）。
   * <p>当 {@code isIdentity} 为 {@code true} 时，方言可以调整 SQL 避免显式写入自增列，
   * 或用 {@code OUTPUT INSERTED} 回读数据库生成的主键。</p>
   *
   * @param tableName   表名
   * @param columnNames 所有列名列表（含主键和逻辑删除列）
   * @param pkColumn    主键列名
   * @param isIdentity  是否自增主键策略
   * @return UPSERT SQL 模板
   */
  default String getUpsertSql(String tableName, List<String> columnNames, String pkColumn, boolean isIdentity) {
    return getUpsertSql(tableName, columnNames, pkColumn);
  }
}
