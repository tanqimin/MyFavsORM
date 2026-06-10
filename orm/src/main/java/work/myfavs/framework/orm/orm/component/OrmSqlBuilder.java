package work.myfavs.framework.orm.orm.component;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import work.myfavs.framework.orm.meta.TableAlias;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.orm.dialect.SqlDialect;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.*;

/**
 * SQL 语句构建器，封装 Druid AST 构建的通用 SQL 语句生成逻辑。
 * <p>数据库方言相关的行为（分页、UPSERT 等）委派给 {@link SqlDialect}。</p>
 */
public class OrmSqlBuilder {

  private final SqlDialect dialect;

  /**
   * 构造 OrmSqlBuilder 实例.
   *
   * @param dialect {@link SqlDialect} 方言实例
   */
  public OrmSqlBuilder(SqlDialect dialect) {
    this.dialect = dialect;
  }

  /**
   * 获取表名，优先返回 TableAlias 中设置的别名
   *
   * @param entityMeta 实体类元数据
   * @return 实际执行的数据表名称
   */
  public static String getTableName(ClassMeta entityMeta) {
    return TableAlias.getOpt().orElse(entityMeta.getTableName());
  }

  /**
   * 创建通用 INSERT 语句。
   * <p>根据主键策略决定是否包含主键列：</p>
   * <ul>
   *   <li>{@link GenerationType#IDENTITY} — 不包含主键列（由数据库自增）</li>
   *   <li>其他策略 — 包含主键列，值为参数占位符 {@code ?}</li>
   * </ul>
   * 若存在逻辑删除字段，则在列列表末尾附加该列并赋值 {@code 0}。
   *
   * @param entityMeta 实体类元数据
   * @return INSERT 语句
   */
  public String insert(ClassMeta entityMeta) {

    final GenerationType strategy = entityMeta.getStrategy();
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();
    final Map<String, Attribute> updateAttributes = entityMeta.getUpdateAttributes();

    final String tableName = getTableName(entityMeta);

    final SQLInsertStatement insertStatement = DruidUtil.createSQLInsertStatement(tableName);
    final List<SQLExpr> columns = new ArrayList<>();
    final List<SQLExpr> values = new ArrayList<>();

    if (strategy != GenerationType.IDENTITY) {
      columns.add(DruidUtil.createColumn(primaryKey.getColumnName()));
      values.add(DruidUtil.createParam());
    }

    for (Attribute attr : updateAttributes.values()) {
      columns.add(DruidUtil.createColumn(attr.getColumnName()));
      values.add(DruidUtil.createParam());
    }

    if (null != logicDelete) {
      columns.add(DruidUtil.createColumn(logicDelete.getColumnName()));
      values.add(DruidUtil.createParam());
    }

    insertStatement.getColumns().addAll(columns);
    insertStatement.setValues(new SQLInsertStatement.ValuesClause(values));

    return insertStatement.toUnformattedString();
  }

  /**
   * 创建通用 UPDATE 语句（单个实体）。
   * <p>仅更新可写字段（排除主键和逻辑删除列），条件为 {@code pk = ?}。
   * 若存在逻辑删除字段，自动追加 {@code AND delCol = 0} 条件。</p>
   *
   * @param entityMeta      实体类元数据
   * @param model           实体
   * @param ignoreNullValue 是否忽略 {@code null} 值字段
   * @param <TModel>        实体类型泛型
   * @return {@link Sql}
   */
  public <TModel> Sql update(ClassMeta entityMeta, TModel model, boolean ignoreNullValue) {

    final Sql sql = new Sql();

    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();
    final String tableName = getTableName(entityMeta);

    final Map<String, Attribute> updateAttributes = entityMeta.getUpdateAttributes();

    final SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);

    for (Attribute attr : updateAttributes.values()) {
      final Object fieldValue = attr.getValue(model);
      if (ignoreNullValue && null == fieldValue) continue;

      final SQLUpdateSetItem sqlUpdateSetItem = DruidUtil.createUpdateSetItem(attr.getColumnName());
      updateStatement.addItem(sqlUpdateSetItem);

      sql.getParams().add(fieldValue);
    }

    if (updateStatement.getItems().isEmpty()) {
      return null;
    }

    updateStatement.addWhere(createCondition(primaryKey, logicDelete));

    sql.append(updateStatement.toUnformattedString());
    sql.getParams().add(primaryKey.getValue(model));

    return sql;
  }

  /**
   * 创建通用 UPDATE 语句（按指定列更新）。
   *
   * @param entityMeta      实体类元数据
   * @param model           实体
   * @param ignoreNullValue 是否忽略 {@code null} 值字段
   * @param columns         需要更新的列名数组（{@code null} 表示更新所有可写列）
   * @param <TModel>        实体类型泛型
   * @return {@link Sql}
   */
  public <TModel> Sql update(ClassMeta entityMeta, TModel model, boolean ignoreNullValue, String[] columns) {

    final Sql sql = new Sql();

    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();
    final String tableName = getTableName(entityMeta);

    final Collection<Attribute> updAttrs = entityMeta.getUpdateAttributes(columns);

    final SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);

    for (Attribute attr : updAttrs) {
      final Object fieldValue = attr.getValue(model);
      if (ignoreNullValue && null == fieldValue) continue;

      final SQLUpdateSetItem sqlUpdateSetItem = DruidUtil.createUpdateSetItem(attr.getColumnName());
      updateStatement.addItem(sqlUpdateSetItem);

      sql.getParams().add(fieldValue);
    }

    if (updateStatement.getItems().isEmpty()) {
      return null;
    }

    updateStatement.addWhere(createCondition(primaryKey, logicDelete));

    sql.append(updateStatement.toUnformattedString());
    sql.getParams().add(primaryKey.getValue(model));

    return sql;
  }

  /**
   * 创建通用 SELECT 语句
   *
   * @param entityMeta 实体类元数据
   * @return SELECT {@link Sql}
   */
  public Sql select(ClassMeta entityMeta) {
    return new Sql(String.format("SELECT * FROM %s", getTableName(entityMeta)));
  }

  /**
   * 创建通用 SELECT COUNT(*) 语句
   *
   * @param entityMeta 实体类元数据
   * @return SELECT COUNT(*) {@link Sql}
   */
  public Sql countSql(ClassMeta entityMeta) {
    return new Sql(String.format("SELECT COUNT(*) FROM %s", getTableName(entityMeta)));
  }

  /**
   * 把 SQL 重构为 COUNT(*) 语句（使用 Druid PagerUtils）
   *
   * @param sql    原始 SQL
   * @param params SQL 参数
   * @return COUNT(*) 语句
   */
  public Sql countSql(String sql, Collection<?> params) {
    com.alibaba.druid.DbType druidDbType = this.dialect.getDruidDbType();
    String count = SQLUtils.format(
        PagerUtils.count(sql, druidDbType),
        druidDbType, new SQLUtils.FormatOption(true, false));
    return new Sql(count, params);
  }

  /**
   * 根据主键和逻辑删除字段创建查询条件
   *
   * @param primaryKey  主键 {@link Attribute}
   * @param logicDelete 逻辑删除字段 {@link Attribute}
   * @return {@link SQLBinaryOpExpr}
   */
  public static SQLBinaryOpExpr createCondition(Attribute primaryKey, Attribute logicDelete) {
    SQLBinaryOpExpr condition = new SQLBinaryOpExpr(
        DruidUtil.createColumn(primaryKey.getColumnName()),
        SQLBinaryOperator.Equality,
        DruidUtil.createParam());

    if (null != logicDelete) {
      condition = new SQLBinaryOpExpr(
          condition,
          SQLBinaryOperator.BooleanAnd,
          new SQLBinaryOpExpr(
              DruidUtil.createColumn(logicDelete.getColumnName()),
              SQLBinaryOperator.Equality,
              new SQLIntegerExpr(0)
          )
      );
    }
    return condition;
  }

  /**
   * 创建 UPSERT（INSERT OR UPDATE）SQL 语句模板。
   * <p>委派给 {@link SqlDialect#getUpsertSql(String, List, String)} 生成，
   * 各数据库的 UPSERT 语法差异由方言实现隔离。</p>
   *
   * @param entityMeta 实体类元数据
   * @return UPSERT SQL 模板（参数为 {@code ?} 占位符）
   */
  public String createOrUpdateSql(ClassMeta entityMeta) {

    final Attribute              primaryKey       = entityMeta.checkPrimaryKey();
    final Attribute              logicDelete      = entityMeta.getLogicDelete();
    final Map<String, Attribute> updateAttributes = entityMeta.getUpdateAttributes();
    final String                 tableName        = getTableName(entityMeta);

    final List<String> columnNames = new ArrayList<>();
    columnNames.add(primaryKey.getColumnName());
    for (Attribute attr : updateAttributes.values()) {
      columnNames.add(attr.getColumnName());
    }
    if (null != logicDelete) {
      columnNames.add(logicDelete.getColumnName());
    }

    return this.dialect.getUpsertSql(tableName, columnNames, primaryKey.getColumnName());
  }

  /**
   * 创建 UPSERT SQL 语句模板（含自增主键标记）。
   *
   * @param entityMeta 实体类元数据
   * @param isIdentity 是否自增主键策略
   * @return UPSERT SQL 模板
   * @see SqlDialect#getUpsertSql(String, List, String, boolean)
   */
  public String createOrUpdateSql(ClassMeta entityMeta, boolean isIdentity) {

    final Attribute              primaryKey       = entityMeta.checkPrimaryKey();
    final Attribute              logicDelete      = entityMeta.getLogicDelete();
    final Map<String, Attribute> updateAttributes = entityMeta.getUpdateAttributes();
    final String                 tableName        = getTableName(entityMeta);

    final List<String> columnNames = new ArrayList<>();
    columnNames.add(primaryKey.getColumnName());
    for (Attribute attr : updateAttributes.values()) {
      columnNames.add(attr.getColumnName());
    }
    if (null != logicDelete) {
      columnNames.add(logicDelete.getColumnName());
    }

    return this.dialect.getUpsertSql(tableName, columnNames, primaryKey.getColumnName(), isIdentity);
  }
}
