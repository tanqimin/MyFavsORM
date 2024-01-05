package work.myfavs.framework.orm.orm.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.TableAlias;
import work.myfavs.framework.orm.meta.annotation.Criteria;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.orm.Orm;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.DruidUtil;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.id.PKGenerator;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ORM 通用实体操作
 */
public abstract class AbstractOrm implements Orm {

  protected final Database   database;
  protected final DBTemplate dbTemplate;
  protected final DBConfig   dbConfig;

  public AbstractOrm(Database database) {
    this.database = database;
    this.dbTemplate = this.database.getDbTemplate();
    this.dbConfig = this.dbTemplate.getDbConfig();
  }

  /**
   * 获取表名，统一封装并获取 TableAlias 中设置的别名
   *
   * @param entityMeta 实体类元数据
   * @return 实际执行的数据表名称
   */
  protected static String getTableName(ClassMeta entityMeta) {
    return TableAlias.getOpt().orElse(entityMeta.getTableName());
  }

  /**
   * Orm 实现类标记的数据库类型
   *
   * @return 数据库类型 {@link work.myfavs.framework.orm.meta.DbType}
   */
  protected abstract String dbType();

  /**
   * 执行查询，返回影响行数
   *
   * @param sql            SQL语句
   * @param params         参数
   * @param configConsumer 在执行查询前允许，可对 PreparedStatement 进行设置
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params, ThrowingConsumer<PreparedStatement, SQLException> configConsumer) {
    try (Query query = this.database.createQuery(sql)) {
      return query.addParameters(params).execute(configConsumer, null);
    }
  }

  /**
   * 执行 SQL 语句，返回影响行数
   *
   * @param sql     SQL语句
   * @param params  参数
   * @param timeout 超时时间(单位：秒)
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params, int timeout) {

    return execute(sql, params, preparedStatement -> preparedStatement.setQueryTimeout(timeout));
  }

  /**
   * 执行 SQL 语句，返回影响行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params) {

    return execute(sql, params, preparedStatement -> {});
  }

  /**
   * 执行 {@link Sql} 语句，返回影响行数
   *
   * @param sql     {@link Sql} 语句
   * @param timeout 超时时间(单位：秒)
   * @return 影响行数
   */
  public int execute(Sql sql, int timeout) {

    return this.execute(sql.toString(), sql.getParams(), preparedStatement -> preparedStatement.setQueryTimeout(timeout));
  }

  /**
   * 执行 {@link Sql} 语句，返回影响行数
   *
   * @param sql {@link Sql} 语句
   * @return 影响行数
   */
  public int execute(Sql sql) {

    return this.execute(sql.toString(), sql.getParams(), preparedStatement -> {});
  }

  /**
   * 执行多个 {@link Sql} 语句
   *
   * @param sqlList {@link Sql} 语句集合
   * @return 返回数组，包含每个查询的影响行数
   */
  public int[] execute(List<Sql> sqlList) {
    return this.execute(sqlList, null);
  }

  /**
   * 执行多个 {@link Sql} 语句
   *
   * @param sqlList {@link Sql} 语句集合
   * @param timeout 超时时间(单位：秒)
   * @return 返回数组，包含每个查询的影响行数
   */
  public int[] execute(List<Sql> sqlList, int timeout) {
    return this.execute(sqlList, ps -> ps.setQueryTimeout(timeout));
  }

  /**
   * 执行多个 {@link Sql} 语句
   *
   * @param sqlList        {@link Sql} 语句集合
   * @param configConsumer 在执行查询前允许，可对 PreparedStatement 进行设置
   * @return 返回数组，包含每个查询的影响行数
   */
  public int[] execute(List<Sql> sqlList, ThrowingConsumer<PreparedStatement, SQLException> configConsumer) {
    final int   sqlCnt  = sqlList.size();
    final int[] results = new int[sqlCnt];

    if (CollectionUtil.isEmpty(sqlList)) return results;

    Iterator<Sql> iterator = sqlList.iterator();
    int           index    = 0;
    if (iterator.hasNext()) {
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
    }

    return results;
  }

  /**
   * 创建实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass, TModel entity) {

    int result = 0;
    if (null == entity) return result;

    final ClassMeta      classMeta   = Metadata.entityMeta(modelClass);
    final Attribute      primaryKey  = classMeta.checkPrimaryKey();
    final Attribute      logicDelete = classMeta.getLogicDelete();
    final GenerationType strategy    = classMeta.getStrategy();
    final String         tableName   = getTableName(classMeta);

    final Map<String /* columnName */, Attribute> updateAttributes = classMeta.getUpdateAttributes();

    boolean autoGeneratedPK = false;

    final Sql sql = new Sql();

    final SQLInsertStatement insertStatement = DruidUtil.createSQLInsertStatement(tableName);
    final List<SQLExpr>      columns         = new ArrayList<>();
    final List<SQLExpr>      values          = new ArrayList<>();

    /*
    如果数据库主键策略为非自增，那么需要加入主键值作为参数
    获取实体主键标识字段是否为null：
    1.ASSIGNED 不允许为空；
    2.UUID、SNOW_FLAKE如果主键标识字段为空，则生成值；
    */
    if (strategy == GenerationType.IDENTITY) {
      autoGeneratedPK = true;
    } else {
      columns.add(DruidUtil.createColumn(primaryKey.getColumnName()));
      values.add(DruidUtil.createParam());

      Object pkVal = generatePrimaryKey(strategy, primaryKey, entity);
      sql.getParams().add(pkVal);
    }

    for (Map.Entry<String, Attribute> entry : updateAttributes.entrySet()) {
      columns.add(DruidUtil.createColumn(entry.getValue().getColumnName()));
      values.add(DruidUtil.createParam());

      sql.getParams().add(entry.getValue().getValue(entity));
    }

    if (null != logicDelete) {
      columns.add(DruidUtil.createColumn(logicDelete.getColumnName()));
      values.add(new SQLIntegerExpr(0));
    }

    insertStatement.getColumns().addAll(columns);
    insertStatement.setValues(new SQLInsertStatement.ValuesClause(values));

    sql.append(insertStatement.toUnformattedString());

    try (Query query = this.database.createQuery(sql.toString(), autoGeneratedPK)) {
      return query.addParameters(sql.getParams())
                  .execute(null,
                           rs -> primaryKey.setPrimaryKey(entity, rs));
    }
  }

  /**
   * 批量创建实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int create(Class<TModel> modelClass, Collection<TModel> entities) {
    if (CollectionUtil.isEmpty(entities)) return 0;

    final ClassMeta classMeta  = Metadata.entityMeta(modelClass);
    final boolean   isIdentity = classMeta.getStrategy().equals(GenerationType.IDENTITY);

    if (!this.database.isMySql()) {
      /*
       * 此处处理了一个MSSQL的JDBC驱动问题，当批量保存时，不能返回KEY，所以使用传统的方法遍历
       * 请参考： @see <a href="http://stackoverflow.com/questions/13641832/getgeneratedkeys-after-preparedstatement-executebatch">stackoverflow</a>
       */
      if (this.database.isSqlServer() && isIdentity) {
        int result = 0;
        for (TModel entity : entities) {
          result += create(modelClass, entity);
        }
        return result;
      }
      return createInJdbcBatch(classMeta, entities);
    }

    if (isIdentity) {
      return createInJdbcBatch(classMeta, entities);
    }

    return createInSqlBatch(classMeta, entities);
  }

  /**
   * 使用SQL语句的批量创建方法 insert into table (f1, f2, f3) values (?,?,?),(?,?,?)...(?,?,?)
   *
   * @param entityMeta 实体类元数据
   * @param entities   实体
   * @param <TModel>   实体类类型
   * @return 记录数
   */
  private <TModel> int createInSqlBatch(ClassMeta entityMeta, Collection<TModel> entities) {

    int result = 0;

    final Map<String /* columnName */, Attribute> updateAttributes = entityMeta.getUpdateAttributes();
    final String                                  tableName        = getTableName(entityMeta);
    final GenerationType                          strategy         = entityMeta.getStrategy();
    final Attribute                               primaryKey       = entityMeta.checkPrimaryKey();

    final List<Sql> sqlList = new ArrayList<>();

    final int                batchSize = this.database.getDbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);

    for (List<TModel> entityList : batchList) {
      boolean insertClauseCompleted = false;
      Sql     insertClause          = Sql.New(String.format("INSERT INTO %s (", tableName));
      Sql     valuesClause          = Sql.New(") VALUES ");

      for (TModel entity : entityList) {
        Object pkVal = generatePrimaryKey(strategy, primaryKey, entity);

        if (!insertClauseCompleted) {
          insertClause.append(primaryKey.getColumnName() + ",");
        }
        valuesClause.append("(?,", pkVal);

        for (Attribute attr : updateAttributes.values()) {
          if (!insertClauseCompleted) {
            insertClause.append(attr.getColumnName() + ",");
          }
          valuesClause.append("?,", attr.getFieldVisitor().<TModel>getValue(entity));
        }

        if (null != entityMeta.getLogicDelete()) {
          if (!insertClauseCompleted) {
            insertClause.append(entityMeta.getLogicDelete().getColumnName() + ",");
          }

          valuesClause.append("?,", 0);
        }

        if (!insertClauseCompleted) {
          insertClause.deleteLastChar(",");
          insertClauseCompleted = true;
        }
        valuesClause.deleteLastChar(",");
        valuesClause.append("),");
      }

      valuesClause.deleteLastChar(",");
      sqlList.add(insertClause.append(valuesClause));
    }

    for (Sql batchSql : sqlList) {
      result += this.execute(batchSql);
    }
    return result;
  }

  private <TModel> int createInJdbcBatch(ClassMeta classMeta, Collection<TModel> entities) {

    final Attribute                               primaryKey       = classMeta.checkPrimaryKey();
    final GenerationType                          strategy         = classMeta.getStrategy();
    final Map<String /* columnName */, Attribute> updateAttributes = classMeta.getUpdateAttributes();

    final Sql                       sql        = new Sql(this.insert(classMeta));
    final Collection<Collection<?>> paramsList = new ArrayList<>();

    boolean autoGeneratedPK = strategy == GenerationType.IDENTITY;

    for (TModel entity : entities) {
      final Collection<Object> params = new ArrayList<>();

      /*
      如果数据库主键策略为非自增，那么需要加入主键值作为参数
      获取实体主键标识字段是否为null：
      1.ASSIGNED 不允许为空；
      2.UUID、SNOW_FLAKE如果主键标识字段为空，则生成值；
      */
      if (!autoGeneratedPK) {
        params.add(generatePrimaryKey(strategy, primaryKey, entity));
      }

      for (Attribute attr : updateAttributes.values()) {
        params.add(attr.getFieldVisitor().getValue(entity));
      }

      paramsList.add(params);
    }

    try (Query query = this.database.createQuery(sql.toString(), autoGeneratedPK)) {
      for (Collection<?> batchParams : paramsList) {
        query.addParameters(batchParams).addBatch();
      }
      return query.executeBatch(rs -> primaryKey.setPrimaryKeys(entities, rs)).length;
    }
  }

  /**
   * 如果实体的主键键值为 null，根据主键策略生成数据库主键值
   *
   * @param strategy   {@link GenerationType}
   * @param primaryKey 主键 {@link Attribute}
   * @param entity     实体
   * @param <TModel>   实体类泛型
   * @return 数据库主键值
   */
  protected <TModel> Object generatePrimaryKey(GenerationType strategy, Attribute primaryKey, TModel entity) {
    Object pkVal = primaryKey.getValue(entity);

    if (null == pkVal) {
      PKGenerator pkGenerator = this.dbTemplate.getPkGenerator();
      switch (strategy) {
        case SNOW_FLAKE:
          pkVal = pkGenerator.nextSnowFakeId();
          break;
        case UUID:
          pkVal = pkGenerator.nextUUID();
          break;
        case ASSIGNED:
          throw new DBException("使用 ASSIGNED 主键策略时，必须要为主键赋值.");
        default:
          throw new DBException("自动生成主键失败.");
      }

      primaryKey.setValue(entity, pkVal);
    }
    return pkVal;
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, TModel entity) {

    if (null == entity) return 0;

    final Sql sql = this.update(modelClass, entity, false);
    return execute(sql);
  }

  /**
   * 根据主键和逻辑删除字段，创建查询条件
   *
   * @param primaryKey  主键 {@link Attribute}
   * @param logicDelete 逻辑删除字段 {@link Attribute}
   * @return {@link SQLBinaryOpExpr}
   */
  protected static SQLBinaryOpExpr createCondition(Attribute primaryKey, Attribute logicDelete) {
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
   * 更新实体，忽略Null属性的字段
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int updateIgnoreNull(Class<TModel> modelClass, TModel entity) {

    if (null == entity) return 0;

    final Sql sql = this.update(modelClass, entity, true);
    return execute(sql);
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param columns    需要更新的列
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, TModel entity, String[] columns) {

    if (null == entity) return 0;

    return update(modelClass, List.of(entity), columns);
  }

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param columns    需要更新的列
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    int result = 0;

    if (CollectionUtil.isEmpty(entities)) {
      return result;
    }

    //在非 SQL Server 中，在 10000 条记录以内的更新，此方式速度较快
    final ClassMeta             entityMeta  = Metadata.entityMeta(modelClass);
    final Attribute             primaryKey  = entityMeta.checkPrimaryKey();
    final Attribute             logicDelete = entityMeta.getLogicDelete();
    final String                tableName   = getTableName(entityMeta);
    final Collection<Attribute> updAttrs    = entityMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new DBException("不能匹配到标记为可更新的属性Attribute.");
    }

    final int                batchSize = this.database.getDbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);

    final List<Sql> sqlList = new ArrayList<>();

    for (List<TModel> entityList : batchList) {
      final Sql sql = new Sql();

      //构建 Update SQL 语句
      final SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);

      for (Attribute updateAttribute : updAttrs) {
        //此处根据更新的属性，构建 CASE 语句：
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

        /*
        把 CASE 添加到 UPDATE 的字段中：
        {updateColumn} = CASE
          WHEN {primaryKey} = ? THEN ?
          WHEN {primaryKey} = ? THEN ?
          WHEN {primaryKey} = ? THEN ?
        END
         */
        final SQLUpdateSetItem sqlUpdateSetItem = new SQLUpdateSetItem();
        sqlUpdateSetItem.setColumn(new SQLIdentifierExpr(updateAttribute.getColumnName()));
        sqlUpdateSetItem.setValue(caseExpr);
        updateStatement.addItem(sqlUpdateSetItem);
      }
      //构建主键条件 WHERE {primaryKey} in (?,?,?)
      final SQLInListExpr condition = new SQLInListExpr();
      condition.setExpr(new SQLIdentifierExpr(primaryKey.getColumnName()));
      for (TModel model : entityList) {
        condition.addTarget(new SQLVariantRefExpr("?"));
        sql.getParams().add(primaryKey.getValue(model));
      }

      //构建逻辑删除条件
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

    final int[] execute = this.execute(sqlList);
    return Arrays.stream(execute).sum();
  }

  /**
   * 更新实体
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
   * 如果记录存在更新，不存在则创建
   *
   * @param modelClass 实体类型
   * @param entity     实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int createOrUpdate(Class<TModel> modelClass, TModel entity) {
    if (exists(modelClass, entity)) {
      return update(modelClass, entity);
    } else {
      return create(modelClass, entity);
    }
  }

  /**
   * 删除记录
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass, TModel entity) {

    if (null == entity) {
      return 0;
    }

    final ClassMeta classMeta = Metadata.entityMeta(modelClass);
    final Object    pkVal     = classMeta.getPrimaryKey().getValue(entity);

    return deleteById(classMeta, pkVal);
  }

  /**
   * 批量删除记录
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int delete(Class<TModel> modelClass, Collection<TModel> entities) {

    if (CollectionUtil.isEmpty(entities)) {
      return 0;
    }

    final Attribute    primaryKey = Metadata.classMeta(modelClass).getPrimaryKey();
    final List<Object> ids        = new ArrayList<>();

    for (TModel entity : entities) {
      final Object pkVal = primaryKey.getValue(entity);

      if (null == pkVal) continue;

      ids.add(pkVal);
    }

    return deleteByIds(modelClass, ids);
  }

  /**
   * 根据ID集合删除记录
   *
   * @param modelClass 实体类型
   * @param ids        ID集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteByIds(Class<TModel> modelClass, Collection<?> ids) {

    if (CollectionUtil.isEmpty(ids)) {
      return 0;
    }

    final ClassMeta entityMeta   = Metadata.entityMeta(modelClass);
    final Attribute primaryKey   = entityMeta.checkPrimaryKey();
    final String    pkColumnName = primaryKey.getColumnName();

    final Cond deleteCond = Cond.in(pkColumnName, new ArrayList<>(ids), false);
    return deleteByCond(entityMeta, deleteCond);
  }

  /**
   * 根据 ID 删除记录
   *
   * @param modelClass 实体类型
   * @param id         ID值
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteById(Class<TModel> modelClass, Object id) {

    if (null == id) {
      return 0;
    }
    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    return deleteById(entityMeta, id);
  }

  /**
   * 根据 ID 删除记录
   *
   * @param entityMeta 实体类元数据
   * @param id         ID值
   * @return 影响行数
   */
  protected int deleteById(ClassMeta entityMeta, Object id) {
    final String pkColumnName = entityMeta.getPrimaryKeyColumnName();
    final Cond   deleteCond   = Cond.eq(pkColumnName, id);

    return deleteByCond(entityMeta, deleteCond);
  }

  /**
   * 根据条件删除记录
   *
   * @param modelClass 实体类型
   * @param cond       条件值
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteByCond(Class<TModel> modelClass, Cond cond) {

    if (null == cond) {
      return 0;
    }

    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    return deleteByCond(entityMeta, cond);
  }

  /**
   * 快速截断表数据
   *
   * @param modelClass 实体类型
   * @param <TModel>   实体类型泛型
   */
  public <TModel> void truncate(Class<TModel> modelClass) {
    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    final String    tableName  = getTableName(entityMeta);

    final SQLTruncateStatement truncateStatement = new SQLTruncateStatement();

    truncateStatement.getTableSources().add(new SQLExprTableSource(tableName));

    execute(new Sql(truncateStatement.toUnformattedString()));
  }

  /**
   * 根据条件删除
   *
   * @param entityMeta 实体类元数据
   * @param deleteCond 删除条件
   * @return 影响行数
   */
  protected int deleteByCond(ClassMeta entityMeta, Cond deleteCond) {
    final String    tableName   = getTableName(entityMeta);
    final Attribute primaryKey  = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();
    final Sql       sql;
    if (null != logicDelete) {
      sql = Sql.Update(tableName)
               .set(String.format("%s = %s", logicDelete.getColumnName(), primaryKey.getColumnName()))
               .where(deleteCond).and(Cond.logicalDelete(logicDelete));
    } else {
      sql = Sql.Delete(tableName).where(deleteCond);
    }
    return execute(sql);
  }

  /**
   * 执行 SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {
    try (Query query = this.database.createQuery(sql)) {
      return query.addParameters(params).find(viewClass);
    }
  }

  /**
   * 执行 {@link Sql}，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> find(Class<TView> viewClass, Sql sql) {

    return this.find(viewClass, sql.toString(), sql.getParams());
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  public List<Record> findRecords(String sql, Collection<?> params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行 {@link Sql}， 并返回多行记录
   *
   * @param sql {@link Sql}
   * @return 结果集
   */
  public List<Record> findRecords(Sql sql) {

    return this.find(Record.class, sql);
  }

  /**
   * 执行SQL，并返回Map
   *
   * @param viewClass 结果集类型
   * @param keyField  返回 Map 的 Key 的字段，必须是 viewClass 中存在的字段
   * @param sql       SQL语句
   * @param params    SQL参数
   * @param <TView>   结果集类型泛型
   * @return Map
   */
  public <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, String sql, Collection<?> params) {
    Field field = ReflectUtil.getField(viewClass, keyField);

    Objects.requireNonNull(field, String.format("Class %s not exist field named %s", viewClass.getName(), keyField));

    return this.find(viewClass, sql, params).parallelStream()
               .collect(Collectors.toMap(tView -> ReflectUtil.getFieldValue(field, tView), tView -> tView));
  }

  /**
   * 执行 {@link Sql}，并返回 Map
   *
   * @param viewClass 结果集类型
   * @param keyField  返回 Map 的 Key 的字段，必须是 viewClass 中存在的字段
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return Map
   */
  public <TKey, TView> Map<TKey, TView> findMap(Class<TView> viewClass, String keyField, Sql sql) {

    return findMap(viewClass, keyField, sql.toString(), sql.getParams());
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params) {

    final Sql querySql = selectPage(true, sql, params, 1, top);
    return this.find(viewClass, querySql);
  }

  /**
   * 返回分页查询语句 如果 pageSize = -1L，则不分页
   *
   * @param enablePage  是否分页
   * @param sql         SQL语句
   * @param params      参数
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return Sql对象
   */
  private Sql selectPage(boolean enablePage, String sql, Collection<?> params, int currentPage, int pageSize) {
    if (!enablePage) return new Sql(sql, params);
    if (currentPage < 1)
      throw new DBException("当前页码 (currentPage) 参数必须大于等于 1");

    if (pageSize < 1)
      throw new DBException("每页记录数 (pageSize) 参数必须大于等于 1");

    long maxPageSize = this.database.getDbConfig().getMaxPageSize();
    if (maxPageSize > 0L && pageSize > maxPageSize)
      throw new DBException("每页记录数不能超出系统设置的最大记录数 %d", maxPageSize);

    return this.selectPage(sql, params, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql}，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {

    return this.findTop(viewClass, top, sql.toString(), sql.getParams());
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top    行数
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  public List<Record> findTopRecords(int top, String sql, Collection<?> params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行 {@link Sql}，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql {@link Sql}
   * @return 结果集
   */
  public List<Record> findTopRecords(int top, Sql sql) {

    return this.findTop(Record.class, top, sql);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  public <TView> TView get(Class<TView> viewClass, String sql, Collection<?> params) {

    final Iterator<TView> iterator = this.find(viewClass, sql, params).iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

  /**
   * 执行 {@link Sql} ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  public <TView> TView get(Class<TView> viewClass, Sql sql) {

    return this.get(viewClass, sql.toString(), sql.getParams());
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 记录
   */
  public Record getRecord(String sql, Collection<?> params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 {@link Sql} ,并返回 1 行记录
   *
   * @param sql {@link Sql}
   * @return 记录
   */
  public Record getRecord(Sql sql) {

    return this.get(Record.class, sql);
  }


  /**
   * 根据主键获取记录
   *
   * @param viewClass 结果类型
   * @param id        主键
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> TView getById(Class<TView> viewClass, Object id) {
    if (null == id) {
      return null;
    }

    final ClassMeta entityMeta  = Metadata.entityMeta(viewClass);
    final Attribute primaryKey  = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.select(entityMeta)
                        .where(Cond.eq(primaryKey.getColumnName(), id))
                        .and(Cond.logicalDelete(logicDelete));

    return this.get(viewClass, sql);
  }

  /**
   * 根据指定字段获取记录
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> TView getByField(Class<TView> viewClass, String field, Object param) {

    final ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = classMeta.getLogicDelete();

    final Sql sql = this.select(classMeta)
                        .where(Cond.eq(field, param, false))
                        .and(Cond.logicalDelete(logicDelete));
    return this.get(viewClass, sql);
  }

  /**
   * 根据 {@link Cond} 条件获取记录
   *
   * @param viewClass 结果类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> TView getByCond(Class<TView> viewClass, Cond cond) {

    final ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = classMeta.getLogicDelete();

    final Sql sql = this.select(classMeta)
                        .where()
                        .and(cond)
                        .and(Cond.logicalDelete(logicDelete));
    return this.get(viewClass, sql);
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询记录
   *
   * @param viewClass 结果类型
   * @param object    包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> TView getByCriteria(Class<TView> viewClass, Object object) {

    return this.getByCond(viewClass, Cond.createByCriteria(object));
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询记录
   *
   * @param viewClass     结果类型
   * @param object        包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param criteriaGroup 条件组名, 参考 {@link Criterion#group() @Criterion(group = CriteriaGroupClass.class)}
   * @param <TView>       实体类型
   * @return 记录
   */
  public <TView> TView getByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup) {

    return this.getByCond(viewClass, Cond.createByCriteria(object, criteriaGroup));
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param viewClass 结果类型
   * @param ids       主键ID集合
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByIds(Class<TView> viewClass, Collection<?> ids) {

    final ClassMeta entityMeta  = Metadata.entityMeta(viewClass);
    final Attribute primaryKey  = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.select(entityMeta)
                        .where()
                        .and(Cond.in(primaryKey.getColumnName(), ids, false))
                        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Object param) {

    final ClassMeta entityMeta  = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.select(entityMeta)
                        .where(Cond.eq(field, param, false))
                        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param params    参数集合
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Collection<?> params) {

    final ClassMeta entityMeta  = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.select(entityMeta)
                        .where()
                        .and(Cond.in(field, params, false))
                        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 根据 {@link Cond} 条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCond(Class<TView> viewClass, Cond cond) {

    final ClassMeta entityMeta  = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.select(entityMeta)
                        .where()
                        .and(cond)
                        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param object    包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object) {

    return findByCond(viewClass, Cond.createByCriteria(object));
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询实体集合
   *
   * @param viewClass     结果类型
   * @param object        包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param criteriaGroup 条件组名, 参考 {@link Criterion#group() @Criterion(group = CriteriaGroupClass.class)}
   * @param <TView>       实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup) {

    return findByCond(viewClass, Cond.createByCriteria(object, criteriaGroup));
  }

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 行数
   */
  public long count(String sql, Collection<?> params) {

    final Sql countSql = this.countSql(sql, params);
    return this.get(Number.class, countSql).longValue();
  }

  /**
   * 获取 {@link Sql} 的行数
   *
   * @param sql {@link Sql}
   * @return 行数
   */
  public long count(Sql sql) {

    return this.count(sql.toString(), sql.getParams());
  }

  /**
   * 根据 {@link Cond} 条件获取查询的行数
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 行数
   */
  public <TView> long countByCond(Class<TView> viewClass, Cond cond) {

    final ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = classMeta.getLogicDelete();

    final Sql sql = this.countSql(classMeta)
                        .where()
                        .and(cond)
                        .and(Cond.logicalDelete(logicDelete));
    return this.get(Number.class, sql).longValue();
  }

  /**
   * 根据传入的 {@link Sql} 判断是否存在符合条件的数据
   *
   * @param sql {@link Sql}
   * @return 查询结果行数大于 0 返回 {@code true}，否则返回 {@code false}
   */
  public boolean exists(Sql sql) {

    return exists(sql.toString(), sql.getParams());
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 查询结果行数大于 0 返回 {@code true}，否则返回 {@code false}
   */
  public boolean exists(String sql, Collection<?> params) {

    return this.count(sql, params) > 0L;
  }

  /**
   * 判断实体（根据ID）是否存在
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 存在返回 {@code true}，不存在返回 {@code false}
   */
  public <TModel> boolean exists(Class<TModel> modelClass, TModel entity) {
    if (null == entity) return false;

    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Object    pkVal      = primaryKey.getValue(entity);

    if (null == pkVal) return false;

    final Sql existSql = this.countSql(entityMeta).where(Cond.eq(primaryKey.getColumnName(), pkVal));
    return exists(existSql);
  }

  /**
   * 根据 {@link Cond} 条件判断是否存在符合条件的数据
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 查询结果行数大于 0 返回 {@code true}，否则返回 {@code false}
   */
  public <TView> boolean existsByCond(Class<TView> viewClass, Cond cond) {

    return this.countByCond(viewClass, cond) > 0L;
  }

  /**
   * 执行 SQL 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    final Sql         querySql = this.selectPage(enablePage, sql, params, currentPage, pageSize);
    final List<TView> data     = this.find(viewClass, querySql);
    return this.dbTemplate.createPageLite(data, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {

    Objects.requireNonNull(pageable);

    final boolean enablePage  = pageable.getEnablePage();
    final int     currentPage = pageable.getCurrentPage();
    final int     pageSize    = pageable.getPageSize();

    return this.findPageLite(viewClass, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       {@link Sql}
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {

    Objects.requireNonNull(pageable);

    final boolean enablePage  = pageable.getEnablePage();
    final int     currentPage = pageable.getCurrentPage();
    final int     pageSize    = pageable.getPageSize();

    return this.findPageLite(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return {@link PageLite} 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return {@link PageLite} 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable {@link IPageable} 对象
   * @return {@link PageLite} 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(String sql, Collection<?> params, IPageable pageable) {

    return this.findPageLite(Record.class, sql, params, pageable);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param sql      {@link Sql}
   * @param pageable {@link IPageable} 对象
   * @return {@link PageLite} 简单分页结果集
   */
  public PageLite<Record> findRecordsPageLite(Sql sql, IPageable pageable) {

    return this.findPageLite(Record.class, sql, pageable);
  }

  /**
   * 执行 SQL 语句，返回 {@link Page} 分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    final Sql         querySql = this.selectPage(enablePage, sql, params, currentPage, pageSize);
    final List<TView> data     = this.find(viewClass, querySql);

    long totalPages = 1;
    long totalRecords;

    if (enablePage) {
      totalRecords = this.count(sql, params);
      totalPages = totalRecords / pageSize;

      if (totalRecords % pageSize != 0) {
        totalPages++;
      }
    } else {
      totalRecords = data.size();
    }

    return this.dbTemplate.createPage(data, currentPage, pageSize, totalPages, totalRecords);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return findPage(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link Page} 分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {

    Objects.requireNonNull(pageable);

    final boolean enablePage  = pageable.getEnablePage();
    final int     currentPage = pageable.getCurrentPage();
    final int     pageSize    = pageable.getPageSize();

    return findPage(viewClass, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       {@link Sql}
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link Page} 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {

    Objects.requireNonNull(pageable);

    final boolean enablePage  = pageable.getEnablePage();
    final int     currentPage = pageable.getCurrentPage();
    final int     pageSize    = pageable.getPageSize();

    return findPage(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link Page} 分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return {@link Page} 分页结果集
   */
  public Page<Record> findRecordsPage(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 分页结果集
   *
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return {@link Page} 结果集
   */
  public Page<Record> findRecordsPage(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回 {@link Page} 结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable {@link IPageable} 对象
   * @return {@link Page} 结果集
   */
  public Page<Record> findRecordsPage(String sql, Collection<?> params, IPageable pageable) {

    Objects.requireNonNull(pageable);

    final boolean enablePage  = pageable.getEnablePage();
    final int     currentPage = pageable.getCurrentPage();
    final int     pageSize    = pageable.getPageSize();

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 结果集
   *
   * @param sql      {@link Sql} 对象
   * @param pageable {@link IPageable} 对象
   * @return {@link Page} 结果集
   */
  public Page<Record> findRecordsPage(Sql sql, IPageable pageable) {

    Objects.requireNonNull(pageable);

    final boolean enablePage  = pageable.getEnablePage();
    final int     currentPage = pageable.getCurrentPage();
    final int     pageSize    = pageable.getPageSize();

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 创建通用 INSERT 语句
   *
   * @param entityMeta 实体类元数据
   * @return INSERT 语句
   */
  protected String insert(ClassMeta entityMeta) {

    final GenerationType                          strategy         = entityMeta.getStrategy();
    final Attribute                               primaryKey       = entityMeta.checkPrimaryKey();
    final Attribute                               logicDelete      = entityMeta.getLogicDelete();
    final Map<String /* columnName */, Attribute> updateAttributes = entityMeta.getUpdateAttributes();

    final String tableName = getTableName(entityMeta);

    final SQLInsertStatement insertStatement = DruidUtil.createSQLInsertStatement(tableName);
    final List<SQLExpr>      columns         = new ArrayList<>();
    final List<SQLExpr>      values          = new ArrayList<>();

    if (strategy != GenerationType.IDENTITY) {
      columns.add(DruidUtil.createColumn(primaryKey.getColumnName()));
      values.add(DruidUtil.createParam());
    }

    for (Map.Entry<String, Attribute> entry : updateAttributes.entrySet()) {
      columns.add(DruidUtil.createColumn(entry.getValue().getColumnName()));
      values.add(DruidUtil.createParam());
    }

    if (null != logicDelete) {
      columns.add(DruidUtil.createColumn(logicDelete.getColumnName()));
      values.add(new SQLIntegerExpr(0));
    }

    insertStatement.getColumns().addAll(columns);
    insertStatement.setValues(new SQLInsertStatement.ValuesClause(values));

    return insertStatement.toUnformattedString();
  }

  /**
   * 创建通用 UPDATE 语句
   *
   * @param clazz           实体类型
   * @param model           实体
   * @param ignoreNullValue 是否忽略空值字段
   * @param <TModel>        实体类型泛型
   * @return {@link Sql}
   */
  protected <TModel> Sql update(Class<TModel> clazz, TModel model, boolean ignoreNullValue) {

    final Sql sql = new Sql();

    final ClassMeta classMeta   = Metadata.classMeta(clazz);
    final Attribute primaryKey  = classMeta.checkPrimaryKey();
    final Attribute logicDelete = classMeta.getLogicDelete();
    final String    tableName   = getTableName(classMeta);

    final Map<String /* columnName */, Attribute> updateAttributes = classMeta.getUpdateAttributes();

    final SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);

    for (Map.Entry<String, Attribute> entry : updateAttributes.entrySet()) {
      final Object fieldValue = entry.getValue().getValue(model);
      if (ignoreNullValue && null == fieldValue) continue;

      final SQLUpdateSetItem sqlUpdateSetItem = DruidUtil.createUpdateSetItem(entry.getValue().getColumnName());
      updateStatement.addItem(sqlUpdateSetItem);

      sql.getParams().add(fieldValue);
    }

    updateStatement.addWhere(createCondition(primaryKey, logicDelete));

    sql.append(updateStatement.toUnformattedString());
    sql.getParams().add(primaryKey.getValue(model));

    return sql;
  }

  /**
   * 把 SQL 重构为适用于分页查询的语句 (子类需实现)
   *
   * @param sql         SQL语句
   * @param params      SQL参数
   * @param currentPage 当前页码
   * @param pageSize    总页数
   * @return 分页查询语句 {@link Sql}
   */
  protected abstract Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize);

  /**
   * 创建通用 SELECT 语句
   *
   * @param entityMeta 实体类元数据
   * @return SELECT {@link Sql}
   */
  protected Sql select(ClassMeta entityMeta) {

    return new Sql(String.format("SELECT * FROM %s", getTableName(entityMeta)));
  }

  /**
   * 创建通用 SELECT COUNT(*) 语句
   *
   * @param entityMeta 实体类元数据
   * @return SELECT COUNT(*) {@link Sql}
   */
  protected Sql countSql(ClassMeta entityMeta) {

    return new Sql(String.format("SELECT COUNT(*) FROM %s", getTableName(entityMeta)));
  }

  /**
   * 把 SQL 重构为 COUNT(*) 语句
   * *
   * * @param sql         SQL语句
   * * @param params      SQL参数
   *
   * @return COUNT(*) 语句
   */
  protected Sql countSql(String sql, Collection<?> params) {

    DbType dbType = DruidUtil.convert(dbType());
    String count = SQLUtils.format(
        PagerUtils.count(sql, dbType),
        dbType, new SQLUtils.FormatOption(true, false));
    return new Sql(count, params);
  }
}
