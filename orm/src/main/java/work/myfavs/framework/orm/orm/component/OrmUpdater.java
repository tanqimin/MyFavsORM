package work.myfavs.framework.orm.orm.component;

import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.DruidUtil;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.util.*;

/**
 * 实体更新器，处理实体更新逻辑。
 * <p>提供单条更新、忽略 Null 更新、指定列更新、批量更新等能力。
 * 批量更新时根据数据库类型自动选择策略：</p>
 * <ul>
 *   <li>SQL Server → JDBC {@code executeBatch()}（避免 2100 参数限制）</li>
 *   <li>其他数据库 → CASE WHEN 多行 UPDATE（单条 SQL 更新多条记录）</li>
 * </ul>
 */
public class OrmUpdater {

  private final Database database;
  private final OrmSqlBuilder sqlBuilder;
  private final OrmExecutor executor;

  /**
   * 构造 OrmUpdater 实例.
   *
   * @param database   {@link Database} 实例
   * @param sqlBuilder {@link OrmSqlBuilder} 实例
   * @param executor   {@link OrmExecutor} 实例
   */
  public OrmUpdater(Database database, OrmSqlBuilder sqlBuilder, OrmExecutor executor) {
    this.database = database;
    this.sqlBuilder = sqlBuilder;
    this.executor = executor;
  }

  /**
   * 更新单条实体记录。
   *
   * @param modelClass 实体类型
   * @param entity     实体实例
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, TModel entity) {

    if (null == entity) return 0;

    final ClassMeta classMeta = Metadata.entityMeta(modelClass);
    final Sql sql = this.sqlBuilder.update(classMeta, entity, false);
    return executor.execute(sql);
  }

  /**
   * 更新单条实体记录，忽略 {@code null} 属性字段。
   *
   * @param modelClass 实体类型
   * @param entity     实体实例
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int updateIgnoreNull(Class<TModel> modelClass, TModel entity) {

    if (null == entity) return 0;

    final ClassMeta classMeta = Metadata.entityMeta(modelClass);
    final Sql sql = this.sqlBuilder.update(classMeta, entity, true);
    return executor.execute(sql);
  }

  /**
   * 更新单条实体记录，仅更新指定列。
   *
   * @param modelClass 实体类型
   * @param entity     实体实例
   * @param columns    需要更新的列名数组
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, TModel entity, String[] columns) {

    if (null == entity) return 0;

    return update(modelClass, List.of(entity), columns);
  }

  /**
   * 批量更新实体记录。
   * <p>SQL Server 使用 JDBC Batch 逐条更新（避免 2100 参数限制）；\n
   * 其他数据库使用 CASE WHEN 多行 UPDATE。</p>
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param columns    需要更新的列名数组（{@code null} 表示更新所有可写列）
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    if (CollectionUtil.isEmpty(entities)) {
      return 0;
    }

    if (this.database.isSqlServer()) {
      return batchUpdateSqlServer(modelClass, entities, columns);
    }

    return batchUpdateCaseStyle(modelClass, entities, columns);
  }

  /**
   * SQL Server 批量更新：使用 JDBC Batch
   */
  private <TModel> int batchUpdateSqlServer(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    Attribute pk = entityMeta.checkPrimaryKey();
    Collection<Attribute> updAttrs = entityMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new InvalidDataAccessException("不能匹配到标记为可更新的属性Attribute.");
    }

    String tableName = OrmSqlBuilder.getTableName(entityMeta);
    Attribute logicDelete = entityMeta.getLogicDelete();

    SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);
    for (Attribute attr : updAttrs) {
      updateStatement.addItem(DruidUtil.createUpdateSetItem(attr.getColumnName()));
    }
    updateStatement.addWhere(OrmSqlBuilder.createCondition(pk, logicDelete));
    String sql = updateStatement.toUnformattedString();

    Collection<Collection<?>> paramsList = new ArrayList<>();

    for (TModel entity : entities) {
      Collection<Object> params = new ArrayList<>();
      for (Attribute attributeMeta : updAttrs) {
        params.add(attributeMeta.getFieldVisitor().getValue(entity));
      }
      params.add(pk.getFieldVisitor().getValue(entity));
      paramsList.add(params);
    }

    try (Query query = this.database.createQuery(sql)) {
      for (Collection<?> batchParams : paramsList) {
        query.addParameters(batchParams).addBatch();
      }
      return query.executeBatch().length;
    }
  }

  /**
   * 非 SQL Server 批量更新：使用 CASE WHEN 多行 UPDATE
   */
  private <TModel> int batchUpdateCaseStyle(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();
    final String tableName = OrmSqlBuilder.getTableName(entityMeta);
    final Collection<Attribute> updAttrs = entityMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new InvalidDataAccessException("不能匹配到标记为可更新的属性Attribute.");
    }

    final int batchSize = this.database.getDbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);

    final List<Sql> sqlList = new ArrayList<>();

    for (List<TModel> entityList : batchList) {
      final Sql sql = new Sql();

      final SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);

      for (Attribute updateAttribute : updAttrs) {
        final SQLCaseExpr caseExpr = new SQLCaseExpr();
        for (TModel model : entityList) {
          caseExpr.addItem(
              new SQLBinaryOpExpr(
                  new SQLIdentifierExpr(primaryKey.getColumnName()),
                  SQLBinaryOperator.Equality,
                  new SQLVariantRefExpr("?")
              ),
              new SQLVariantRefExpr("?")
          );

          sql.getParams().add(primaryKey.getValue(model));
          sql.getParams().add(updateAttribute.getValue(model));
        }

        final SQLUpdateSetItem sqlUpdateSetItem = new SQLUpdateSetItem();
        sqlUpdateSetItem.setColumn(new SQLIdentifierExpr(updateAttribute.getColumnName()));
        sqlUpdateSetItem.setValue(caseExpr);
        updateStatement.addItem(sqlUpdateSetItem);
      }

      final SQLInListExpr condition = new SQLInListExpr();
      condition.setExpr(new SQLIdentifierExpr(primaryKey.getColumnName()));
      for (TModel model : entityList) {
        condition.addTarget(new SQLVariantRefExpr("?"));
        sql.getParams().add(primaryKey.getValue(model));
      }

      if (null == logicDelete) {
        updateStatement.addWhere(condition);
      } else {
        updateStatement.addWhere(new SQLBinaryOpExpr(
            condition,
            SQLBinaryOperator.BooleanAnd,
            new SQLBinaryOpExpr(
                new SQLIdentifierExpr(logicDelete.getColumnName()),
                SQLBinaryOperator.Equality,
                new SQLIntegerExpr(0)
            )
        ));
      }

      sql.append(updateStatement.toUnformattedString());
      sqlList.add(sql);
    }

    final int[] execute = this.executor.execute(sqlList);
    return Arrays.stream(execute).sum();
  }

  /**
   * 批量更新实体记录（更新所有可写列）。
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities) {
    return this.update(modelClass, entities, null);
  }

  /**
   * 如果记录存在则更新，不存在则创建。
   *
   * @param modelClass 实体类型
   * @param entity     实体实例
   * @param inserter   {@link OrmInserter} 实例，用于不存在时的插入操作
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int createOrUpdate(Class<TModel> modelClass, TModel entity, OrmInserter inserter) {
    if (exists(modelClass, entity)) {
      return update(modelClass, entity);
    } else {
      return inserter.create(modelClass, entity);
    }
  }

  private <TModel> boolean exists(Class<TModel> modelClass, TModel entity) {
    if (null == entity) return false;

    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Object pkVal = primaryKey.getValue(entity);

    if (null == pkVal) return false;

    final Sql existSql = this.sqlBuilder.countSql(entityMeta).where(Cond.eq(primaryKey.getColumnName(), pkVal));
    return this.executor.execute(existSql.toString(), existSql.getParams()) > 0;
  }
}
