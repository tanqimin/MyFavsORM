package work.myfavs.framework.orm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.annotation.Criteria;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.TableAlias;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库
 */
public class Database extends Query {

  /**
   * 构造方法，建议通过 {@link DBTemplate#createDatabase()} 创建
   *
   * @param dbTemplate {@link DBTemplate}
   */
  public Database(DBTemplate dbTemplate) {
    super(dbTemplate);
  }

  /**
   * 在事务中执行 {@link ThrowingFunction#apply(Object)}
   *
   * @param func      {@link ThrowingFunction} function
   * @param <TResult> function 返回结果类型
   * @return {@link ThrowingFunction#apply(Object) func.apply(Object)}
   */
  public <TResult> TResult tx(ThrowingFunction<Database, TResult, SQLException> func) {
    try {
      this.open();
      return func.apply(this);
    } catch (SQLException e) {
      this.rollback();
      throw new DBException(e, "Error in transaction: {}", e.getMessage());
    } finally {
      this.close();
    }
  }

  /**
   * 在事务中执行 {@link ThrowingConsumer#accept(Object)}
   *
   * @param consumer {@link ThrowingConsumer} consumer
   */
  public void tx(ThrowingConsumer<Database, SQLException> consumer) {

    try {
      this.open();
      consumer.accept(this);
    } catch (SQLException e) {
      this.rollback();
      throw new DBException(e, "Error in transaction: {}", e.getMessage());
    } finally {
      this.close();
    }
  }

  public int execute(String sql, Collection<?> params, ThrowingConsumer<PreparedStatement, SQLException> consumer) {
    sqlLog.showSql(sql, params);

    int result = doExecute(sql, params, false, consumer, null);
    return showAffectedRows(result);
  }

  private int showAffectedRows(int result) {
    return this.sqlLog.showAffectedRows(result);
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
    int   sqlCnt  = sqlList.size();
    int[] results = new int[sqlCnt];

    return tx(db -> {
      for (int i = 0; i < sqlCnt; i++) {
        results[i] = execute(sqlList.get(i));
      }
      return results;
    });
  }

  /**
   * 执行多个 {@link Sql} 语句
   *
   * @param sqlList {@link Sql} 语句集合
   * @param timeout 超时时间(单位：秒)
   * @return 返回数组，包含每个查询的影响行数
   */
  public int[] execute(List<Sql> sqlList, int timeout) {
    int   sqlCnt  = sqlList.size();
    int[] results = new int[sqlCnt];

    return tx(database -> {
      for (int i = 0; i < sqlCnt; i++) {
        results[i] = database.execute(sqlList.get(i), timeout);
      }
      return results;
    });
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
    if (entity == null) return result;

    ClassMeta      classMeta       = Metadata.entityMeta(modelClass);
    Attribute      primaryKey      = classMeta.checkPrimaryKey();
    GenerationType strategy        = classMeta.getStrategy();
    boolean        autoGeneratedPK = false;

    Sql sql;
    /*
    如果数据库主键策略为非自增，那么需要加入主键值作为参数
    获取实体主键标识字段是否为null：
    1.ASSIGNED 不允许为空；
    2.UUID、SNOW_FLAKE如果主键标识字段为空，则生成值；
    */
    if (strategy == GenerationType.IDENTITY) {
      autoGeneratedPK = true;
    } else {
      checkAndGeneratePKValue(strategy, primaryKey, entity);
    }

    sql = getDialect().insert(modelClass, entity);

    return doExecute(sql.toString(),
                     sql.getParams(),
                     autoGeneratedPK,
                     null,
                     rs -> setKey(primaryKey, entity, rs));
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

    ClassMeta classMeta = Metadata.entityMeta(modelClass);

    final boolean isIdentity = classMeta.getStrategy().equals(GenerationType.IDENTITY);

    if (!isMySql()) {
      /*
       * 此处处理了一个MSSQL的JDBC驱动问题，当批量保存时，不能返回KEY，所以使用传统的方法遍历
       * 请参考： @see <a href="http://stackoverflow.com/questions/13641832/getgeneratedkeys-after-preparedstatement-executebatch">stackoverflow</a>
       */
      if (isSqlServer() && isIdentity) {
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
   * @param classMeta 实体类
   * @param entities  实体
   * @param <TModel>  实体类类型
   * @return 记录数
   */
  private <TModel> int createInSqlBatch(ClassMeta classMeta, Collection<TModel> entities) {

    int result = 0;

    final Map<String /* columnName */, Attribute> updateAttributes = classMeta.getUpdateAttributes();
    final GenerationType                          strategy         = classMeta.getStrategy();
    final Attribute                               primaryKey       = classMeta.checkPrimaryKey();

    final List<Sql> sqlList = new ArrayList<>();

    final int                batchSize = getDbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);

    for (List<TModel> entityList : batchList) {
      boolean insertClauseCompleted = false;
      String  tableName             = TableAlias.getOpt().orElse(classMeta.getTableName());
      Sql     insertClause          = Sql.New(StrUtil.format("INSERT INTO {} (", tableName));
      Sql     valuesClause          = Sql.New(") VALUES ");

      for (TModel entity : entityList) {
        Object pkVal = checkAndGeneratePKValue(strategy, primaryKey, entity);

        if (!insertClauseCompleted) {
          insertClause.append(primaryKey.getColumnName() + ",");
        }
        valuesClause.append("(?,", pkVal);

        for (Attribute attr : updateAttributes.values()) {
          if (!insertClauseCompleted) {
            insertClause.append(attr.getColumnName() + ",");
          }
          valuesClause.append("?,", attr.getFieldVisitor().getValue(entity));
        }

        if (classMeta.getLogicDelete() != null) {
          if (!insertClauseCompleted) {
            insertClause.append(classMeta.getLogicDelete().getColumnName() + ",");
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

    GenerationType                          strategy;
    Object                                  pkVal;
    boolean                                 autoGeneratedPK = false;
    Map<String /* columnName */, Attribute> updateAttributes;
    Sql                                     sql;
    Collection<Collection<?>>               paramsList;
    Collection<Object>                      params;

    Attribute primaryKey = classMeta.checkPrimaryKey();

    strategy = classMeta.getStrategy();
    updateAttributes = classMeta.getUpdateAttributes();
    sql = getDialect().insert(classMeta.getClazz());
    paramsList = new ArrayList<>();

    if (strategy == GenerationType.IDENTITY) {
      autoGeneratedPK = true;
    }

    for (TModel entity : entities) {
      params = new ArrayList<>();

      /*
      如果数据库主键策略为非自增，那么需要加入主键值作为参数
      获取实体主键标识字段是否为null：
      1.ASSIGNED 不允许为空；
      2.UUID、SNOW_FLAKE如果主键标识字段为空，则生成值；
      */
      if (strategy != GenerationType.IDENTITY) {
        pkVal = checkAndGeneratePKValue(strategy, primaryKey, entity);
        params.add(pkVal);
      }

      for (Attribute attr : updateAttributes.values()) {
        params.add(attr.getFieldVisitor().getValue(entity));
      }

      paramsList.add(params);
    }

    return doExecuteBatch(sql.toString(),
                          paramsList,
                          autoGeneratedPK,
                          rs -> setKeys(primaryKey, entities, rs)).length;
  }

  private <TModel> Object checkAndGeneratePKValue(GenerationType strategy, Attribute primaryKey, TModel entity) {
    Object pkVal = primaryKey.getFieldVisitor().getValue(entity);
    if (pkVal != null) return pkVal;

    if (strategy == GenerationType.ASSIGNED)
      throw new DBException("Assigned ID can not be null.");

    if (strategy == GenerationType.UUID) {
      pkVal = this.dbTemplate.getPkGenerator().nextUUID();
    } else if (strategy == GenerationType.SNOW_FLAKE) {
      pkVal = this.dbTemplate.getPkGenerator().nextSnowFakeId();
    }
    primaryKey.getFieldVisitor().setValue(entity, pkVal);
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

    if (entity == null) return 0;

    ClassMeta classMeta   = Metadata.entityMeta(modelClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().update(modelClass, entity, false)
                          .and(Cond.logicalDelete(logicDelete));
    return execute(sql);
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

    if (entity == null) return 0;

    ClassMeta classMeta   = Metadata.entityMeta(modelClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().update(modelClass, entity, true)
                          .and(Cond.logicalDelete(logicDelete));
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

    if (entity == null) return 0;

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

    if (isSqlServer()) {
      return updateByLines(modelClass, entities, columns);
    }

    ClassMeta             classMeta = Metadata.entityMeta(modelClass);
    Attribute             pk        = classMeta.checkPrimaryKey();
    Collection<Attribute> updAttrs  = classMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new DBException("Could not match update attributes.");
    }

    final int                batchSize = getDbConfig().getBatchSize();
    final List<List<TModel>> batchList = CollectionUtil.split(entities, batchSize);
    String                   tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    List<Sql>                batchSqls = new ArrayList<>();

    for (List<TModel> entityList : batchList) {
      Sql sql = Sql.Update(tableName).append(" SET ");

      List<Object>     ids        = new ArrayList<>();
      Map<String, Sql> setClauses = new TreeMap<>();
      for (TModel entity : entityList) {
        for (Attribute updAttr : updAttrs) {
          Sql          setClause;
          final String columnName = updAttr.getColumnName();
          if (setClauses.containsKey(columnName)) {
            setClause = setClauses.get(columnName);
          } else {
            setClause = new Sql(StrUtil.format(" {} = CASE {} ", columnName, pk.getColumnName()));
          }
          setClause.append(
              new Sql(" WHEN ? THEN ? ",
                      CollectionUtil.newArrayList(
                          pk.getFieldVisitor().getValue(entity),
                          updAttr.getFieldVisitor().getValue(entity)
                      )
              )
          );

          setClauses.put(columnName, setClause);
        }
        ids.add(pk.getFieldVisitor().getValue(entity));
      }

      for (Sql setClause : setClauses.values()) {
        sql.append(setClause).append(" END,");
      }
      sql.deleteLastChar(",");

      sql.where().and(Cond.in(pk.getColumnName(), ids));
      if (classMeta.getLogicDelete() != null) {
        sql.append(StrUtil.format(" AND {} = 0", classMeta.getLogicDelete().getColumnName()));
      }
      batchSqls.add(sql);
    }
    for (Sql batchSql : batchSqls) {
      result += this.execute(batchSql);
    }
    return result;
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
  private <TModel> int updateByLines(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    ClassMeta             classMeta = Metadata.classMeta(modelClass);
    Attribute             pk        = classMeta.checkPrimaryKey();
    Collection<Attribute> updAttrs  = classMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new DBException("Could not match update attributes.");
    }

    Sql                       sql;
    Collection<Collection<?>> paramsList;
    Collection<Object>        params;

    sql = Sql.Update(classMeta.getTableName()).append(" SET ");
    for (Attribute updateAttribute : updAttrs) {
      sql.append(StrUtil.format("{} = ?,", updateAttribute.getColumnName()));
    }

    sql.getSql().deleteCharAt(sql.getSql().lastIndexOf(","));

    sql.append(StrUtil.format(" WHERE {} = ?", pk.getColumnName()));

    if (classMeta.getLogicDelete() != null) {
      sql.append(StrUtil.format(" AND {} = 0", classMeta.getLogicDelete().getColumnName()));
    }

    paramsList = new ArrayList<>();

    for (TModel entity : entities) {
      params = new ArrayList<>();

      for (Attribute attributeMeta : updAttrs) {
        params.add(attributeMeta.getFieldVisitor().getValue(entity));
      }

      params.add(pk.getFieldVisitor().getValue(entity));
      paramsList.add(params);
    }

    return doExecuteBatch(sql.toString(), paramsList, false, null).length;
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

    if (Objects.isNull(entity)) {
      return 0;
    }

    ClassMeta classMeta = Metadata.entityMeta(modelClass);
    Object    pkVal     = classMeta.getPrimaryKey().getFieldVisitor().getValue(entity);

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

    if (CollUtil.isEmpty(entities)) {
      return 0;
    }

    Attribute    primaryKey = Metadata.classMeta(modelClass).getPrimaryKey();
    List<Object> ids        = new ArrayList<>();

    Object pkVal;
    for (TModel entity : entities) {
      pkVal = primaryKey.getFieldVisitor().getValue(entity);
      if (pkVal == null) {
        continue;
      }

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

    if (CollUtil.isEmpty(ids)) {
      return 0;
    }

    ClassMeta classMeta    = Metadata.classMeta(modelClass);
    Attribute primaryKey   = classMeta.checkPrimaryKey();
    String    pkColumnName = primaryKey.getColumnName();

    if (isSqlServer()) {
      if (ids.size() > Constant.MAX_PARAM_SIZE_FOR_MSSQL) {
        int                     ret         = 0;
        List<? extends List<?>> splitParams = CollUtil.split(ids, Constant.MAX_PARAM_SIZE_FOR_MSSQL);
        for (List<?> splitParam : splitParams) {
          Cond deleteCond = Cond.in(pkColumnName, splitParam, false);
          ret += deleteByCond(classMeta, deleteCond);
        }
        return ret;
      }
    }

    Cond deleteCond = Cond.in(pkColumnName, new ArrayList<>(ids), false);
    return deleteByCond(classMeta, deleteCond);
  }

  /**
   * 根据ID删除记录
   *
   * @param modelClass 实体类型
   * @param id         ID值
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  public <TModel> int deleteById(Class<TModel> modelClass, Object id) {

    if (id == null) {
      return 0;
    }
    ClassMeta classMeta = Metadata.classMeta(modelClass);
    return deleteById(classMeta, id);
  }

  private int deleteById(ClassMeta classMeta, Object id) {
    String pkColumnName = classMeta.getPrimaryKeyColumnName();
    Cond   deleteCond   = Cond.eq(pkColumnName, id);

    return deleteByCond(classMeta, deleteCond);
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

    if (cond == null) {
      return 0;
    }

    ClassMeta classMeta = Metadata.classMeta(modelClass);
    return deleteByCond(classMeta, cond);
  }

  /**
   * 快速截断表数据
   *
   * @param modelClass 实体类型
   * @param <TModel>   实体类型泛型
   */
  public <TModel> void truncate(Class<TModel> modelClass) {
    ClassMeta classMeta = Metadata.classMeta(modelClass);
    String    tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    execute(new Sql(StrUtil.format("TRUNCATE TABLE {}", tableName)));
  }

  private int deleteByCond(ClassMeta classMeta, Cond deleteCond) {
    Sql       sql;
    String    tableName   = TableAlias.getOpt().orElse(classMeta.getTableName());
    Attribute logicDelete = classMeta.getLogicDelete();
    if (logicDelete != null) {
      sql = Sql.Update(tableName)
               .set(StrUtil.format("{} = {}", logicDelete.getColumnName(), classMeta.getPrimaryKey().getColumnName()))
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
    sqlLog.showSql(sql, params);

    Metadata.classMeta(viewClass);

    List<TView> result = this.createStatement(sql)
                             .addParameters(params)
                             .list(viewClass);
    return showResult(viewClass, result);
  }

  private <TView> List<TView> showResult(Class<TView> viewClass, List<TView> result) {
    return sqlLog.showResult(viewClass, result);
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
    final PropDesc prop = BeanUtil.getBeanDesc(viewClass).getProp(keyField);
    if (prop == null) {
      throw new DBException("Class {} not exist Prop named {}", viewClass.getName(), keyField);
    }

    return this.find(viewClass, sql, params).parallelStream()
               .collect(Collectors.toMap(tView -> BeanUtil.getProperty(tView, keyField), tView -> tView));
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

    Sql querySql = getDialect().selectPage(true, sql, params, 1, top);
    return this.find(viewClass, querySql);
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

    Iterator<TView> iterator = this.find(viewClass, sql, params).iterator();
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
    if (Objects.isNull(id)) {
      return null;
    }

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute primaryKey  = classMeta.checkPrimaryKey();
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().select(viewClass)
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

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().select(viewClass)
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

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().select(viewClass)
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

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute primaryKey  = classMeta.checkPrimaryKey();
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().select(viewClass)
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

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().select(viewClass)
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

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().select(viewClass)
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

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().select(viewClass)
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

    Sql countSql = getDialect().count(sql, params);
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

    ClassMeta classMeta   = Metadata.entityMeta(viewClass);
    Attribute logicDelete = classMeta.getLogicDelete();

    Sql sql = getDialect().count(viewClass)
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
    if (entity == null) return false;

    ClassMeta classMeta  = Metadata.entityMeta(modelClass);
    Attribute primaryKey = classMeta.checkPrimaryKey();
    Object    pkVal      = primaryKey.getFieldVisitor().getValue(entity);

    if (pkVal == null) return false;

    Sql existSql = getDialect().count(modelClass).where(Cond.eq(primaryKey.getColumnName(), pkVal));
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

    Sql         querySql = getDialect().selectPage(enablePage, sql, params, currentPage, pageSize);
    List<TView> data     = this.find(viewClass, querySql);
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

    Assert.notNull(pageable);

    boolean enablePage  = pageable.getEnablePage();
    int     currentPage = pageable.getCurrentPage();
    int     pageSize    = pageable.getPageSize();

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

    Assert.notNull(pageable);

    boolean enablePage  = pageable.getEnablePage();
    int     currentPage = pageable.getCurrentPage();
    int     pageSize    = pageable.getPageSize();

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

    Sql         querySql = getDialect().selectPage(enablePage, sql, params, currentPage, pageSize);
    List<TView> data     = this.find(viewClass, querySql);

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

    Assert.notNull(pageable);

    boolean enablePage  = pageable.getEnablePage();
    int     currentPage = pageable.getCurrentPage();
    int     pageSize    = pageable.getPageSize();

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

    Assert.notNull(pageable);

    boolean enablePage  = pageable.getEnablePage();
    int     currentPage = pageable.getCurrentPage();
    int     pageSize    = pageable.getPageSize();

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

    Assert.notNull(pageable);

    boolean enablePage  = pageable.getEnablePage();
    int     currentPage = pageable.getCurrentPage();
    int     pageSize    = pageable.getPageSize();

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

    Assert.notNull(pageable);

    boolean enablePage  = pageable.getEnablePage();
    int     currentPage = pageable.getCurrentPage();
    int     pageSize    = pageable.getPageSize();

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }
}
