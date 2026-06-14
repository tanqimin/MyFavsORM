package work.myfavs.framework.orm.orm.component;

import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.DruidUtil;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;
import work.myfavs.framework.orm.util.id.PKGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

  private final Database      database;
  private final OrmSqlBuilder sqlBuilder;
  private final OrmExecutor   executor;
  private final OrmInserter   inserter;

  /**
   * 构造 OrmUpdater 实例.
   *
   * @param database   {@link Database} 实例
   * @param sqlBuilder {@link OrmSqlBuilder} 实例
   * @param executor   {@link OrmExecutor} 实例
   * @param inserter   {@link OrmInserter} 实例，用于 IDENTITY 策略主键为 null 时的回退插入
   */
  public OrmUpdater(Database database, OrmSqlBuilder sqlBuilder, OrmExecutor executor, OrmInserter inserter) {
    this.database   = database;
    this.sqlBuilder = sqlBuilder;
    this.executor   = executor;
    this.inserter   = inserter;
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

    if (null == entity) {
      return 0;
    }

    final ClassMeta classMeta = Metadata.entityMeta(modelClass);
    final Sql       sql       = this.sqlBuilder.update(classMeta, entity, false);
    if (null == sql) {
      return 0;
    }
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

    if (null == entity) {
      return 0;
    }

    final ClassMeta classMeta = Metadata.entityMeta(modelClass);
    final Sql       sql       = this.sqlBuilder.update(classMeta, entity, true);
    if (null == sql) {
      return 0;
    }
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

    if (null == entity) {
      return 0;
    }

    final ClassMeta classMeta = Metadata.entityMeta(modelClass);
    final Sql       sql       = this.sqlBuilder.update(classMeta, entity, false, columns);
    if (null == sql) {
      return 0;
    }
    return executor.execute(sql);
  }

  /**
   * 批量更新实体记录。
   * <p>SQL Server 使用 JDBC Batch 逐条更新（避免 2100 参数限制）；<br>
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

    if (this.database.isSqlServer()
        || DbType.POSTGRE_SQL.equals(this.database.getDbConfig().getDbType())) {
      return batchUpdateJdbc(modelClass, entities, columns);
    }

    return batchUpdateCaseStyle(modelClass, entities, columns);
  }

  /**
   * SQL Server 批量更新：使用 JDBC Batch
   */
  private <TModel> int batchUpdateJdbc(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    ClassMeta             entityMeta = Metadata.entityMeta(modelClass);
    Attribute             pk         = entityMeta.checkPrimaryKey();
    Collection<Attribute> updAttrs   = entityMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new InvalidDataAccessException("不能匹配到标记为可更新的属性Attribute.");
    }

    String    tableName   = OrmSqlBuilder.getTableName(entityMeta);
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

    final ClassMeta             entityMeta  = Metadata.entityMeta(modelClass);
    final Attribute             primaryKey  = entityMeta.checkPrimaryKey();
    final Attribute             logicDelete = entityMeta.getLogicDelete();
    final String                tableName   = OrmSqlBuilder.getTableName(entityMeta);
    final Collection<Attribute> updAttrs    = entityMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new InvalidDataAccessException("不能匹配到标记为可更新的属性Attribute.");
    }

    final int                batchSize = this.database.getDbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);

    final List<Sql> sqlList = new ArrayList<>();

    for (List<TModel> entityList : batchList) {
      final Sql sql = new Sql();

      // 提前读取一遍主键值，避免在 CASE WHEN 和 WHERE IN 中各反射一次
      final List<Object> pkValues = new ArrayList<>(entityList.size());
      for (TModel model : entityList) {
        pkValues.add(primaryKey.getValue(model));
      }

      final SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);

      int idx = 0;
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

          sql.getParams().add(pkValues.get(idx++));
          sql.getParams().add(updateAttribute.getValue(model));
        }
        idx = 0;

        final SQLUpdateSetItem sqlUpdateSetItem = new SQLUpdateSetItem();
        sqlUpdateSetItem.setColumn(new SQLIdentifierExpr(updateAttribute.getColumnName()));
        sqlUpdateSetItem.setValue(caseExpr);
        updateStatement.addItem(sqlUpdateSetItem);
      }

      final SQLInListExpr condition = new SQLInListExpr();
      condition.setExpr(new SQLIdentifierExpr(primaryKey.getColumnName()));
      for (Object pkVal : pkValues) {
        condition.addTarget(new SQLVariantRefExpr("?"));
        sql.getParams().add(pkVal);
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
   * <p>使用数据库原生的 UPSERT / MERGE 语义，以单条 SQL 原子性完成插入或更新操作，
   * 避免了旧版"先更新后插入"模式的非线程安全问题。</p>
   *
   * <p>各数据库使用的语法：</p>
   * <ul>
   *   <li>MySQL / H2 → {@code INSERT ... ON DUPLICATE KEY UPDATE ...}</li>
   *   <li>PostgreSQL → {@code INSERT ... ON CONFLICT (pk) DO UPDATE SET ...}</li>
   *   <li>SQL Server → {@code MERGE INTO ... USING (VALUES ...) AS source ...}</li>
   *   <li>Oracle → {@code MERGE INTO ... USING (SELECT ... FROM DUAL) source ...}</li>
   * </ul>
   *
   * <p>对于非 {@link GenerationType#IDENTITY} 策略且主键为 {@code null} 的实体，
   * 会自动根据策略生成主键值。对于 {@code IDENTITY} 策略，调用方需确保实体主键不为 {@code null}。</p>
   *
   * @param modelClass 实体类型
   * @param entity     实体实例
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int createOrUpdate(Class<TModel> modelClass, TModel entity) {

    if (null == entity) {
      return 0;
    }

    final ClassMeta     classMeta = Metadata.entityMeta(modelClass);
    final Attribute     primaryKey = classMeta.checkPrimaryKey();
    final GenerationType strategy = classMeta.getStrategy();

    // 非 IDENTITY 策略且主键为 null 时，自动生成主键
    if (strategy != GenerationType.IDENTITY) {
      Object pkVal = primaryKey.getValue(entity);
      if (null == pkVal) {
        pkVal = generatePrimaryKey(strategy, primaryKey, entity);
      }
    }

    // IDENTITY 策略：数据库自增主键，特殊处理
    if (strategy == GenerationType.IDENTITY) {
      final Object pkValue = primaryKey.getValue(entity);
      if (pkValue == null) {
        // 主键为 null → 走 INSERT，让数据库自增
        return this.inserter.create(modelClass, entity);
      }
      if (this.database.isSqlServer()) {
        // SQL Server 的 MERGE 可使用 OUTPUT INSERTED 回读自增主键
        return upsertWithGeneratedKeys(classMeta, entity, primaryKey);
      }
      // 其他数据库 IDENTITY 有 PK 值 → 使用数据库原生的 UPSERT 语法
      // 用户已提供 PK 值，无需回读自增主键
      final String sqlTemplate = this.sqlBuilder.createOrUpdateSql(classMeta);
      final Sql    sql         = new Sql(sqlTemplate);
      sql.getParams().add(primaryKey.getValue(entity));
      for (Attribute attr : classMeta.getUpdateAttributes().values()) {
        sql.getParams().add(attr.getValue(entity));
      }
      if (null != classMeta.getLogicDelete()) {
        sql.getParams().add(0);
      }
      return this.executor.execute(sql);
    }

    // 非 IDENTITY 策略：使用数据库原生的 UPSERT/MERGE 语法
    final String sqlTemplate = this.sqlBuilder.createOrUpdateSql(classMeta);
    final Sql    sql         = new Sql(sqlTemplate);

    sql.getParams().add(primaryKey.getValue(entity));
    for (Attribute attr : classMeta.getUpdateAttributes().values()) {
      sql.getParams().add(attr.getValue(entity));
    }
    if (null != classMeta.getLogicDelete()) {
      sql.getParams().add(0);
    }

    return this.executor.execute(sql);
  }

  /**
   * 使用 UPSERT + RETURN_GENERATED_KEYS 方式执行 createOrUpdate（用于 IDENTITY 策略）。
   * <p>生成不含自增列的 MERGE SQL，通过 {@code OUTPUT INSERTED.pk} 或 JDBC
   * {@code getGeneratedKeys()} 回读数据库生成的主键。</p>
   */
  private <TModel> int upsertWithGeneratedKeys(ClassMeta classMeta, TModel entity, Attribute primaryKey) {

    final String sqlTemplate = this.sqlBuilder.createOrUpdateSql(classMeta, true);
    final Sql    sql         = new Sql(sqlTemplate);

    // 绑定参数：PK 用于 ON 匹配，不含自增列 INSERT（方言已排除）
    sql.getParams().add(primaryKey.getValue(entity));
    for (Attribute attr : classMeta.getUpdateAttributes().values()) {
      sql.getParams().add(attr.getValue(entity));
    }
    if (null != classMeta.getLogicDelete()) {
      sql.getParams().add(0);
    }

    try (Query query = this.database.createQuery(sqlTemplate, true)) {
      return query.addParameters(sql.getParams())
          .execute(null, rs -> primaryKey.setPrimaryKey(entity, rs));
    }
  }

  /**
   * 根据主键策略自动生成主键值。
   *
   * @param strategy   主键生成策略
   * @param primaryKey 主键属性元数据
   * @param entity     实体实例
   * @param <TModel>   实体类型泛型
   * @return 生成的主键值
   */
  private <TModel> Object generatePrimaryKey(GenerationType strategy, Attribute primaryKey, TModel entity) {

    final PKGenerator pkGenerator = this.database.getDbTemplate().getPkGenerator();
    final Object      pkVal;
    switch (strategy) {
      case SNOW_FLAKE:
        pkVal = pkGenerator.nextSnowFakeId();
        break;
      case UUID:
        pkVal = pkGenerator.nextUUID();
        break;
      case ASSIGNED:
        throw new InvalidDataAccessException("使用 ASSIGNED 主键策略时，必须要为主键赋值.");
      default:
        throw new InvalidDataAccessException("自动生成主键失败.");
    }
    primaryKey.setValue(entity, pkVal);
    return pkVal;
  }
}
