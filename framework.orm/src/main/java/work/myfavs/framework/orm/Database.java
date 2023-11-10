package work.myfavs.framework.orm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.TableAlias;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.Attributes;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.JdbcUtil;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Database extends Query {


  public Database(DBTemplate dbTemplate) {
    super(dbTemplate);
  }

  public <TResult> TResult tx(ThrowingFunction<Database, TResult, SQLException> func) {
    Database database = this;
    return this.tx(() -> func.apply(database));
  }

  public void tx(ThrowingConsumer<Database, SQLException> consumer) {
    Database database = this;
    this.tx(() -> consumer.accept(database));
  }

  public int execute(String sql, Collection<?> params, int queryTimeOut) {
    sqlLog.showSql(sql, params);

    Connection        conn;
    PreparedStatement statement = null;

    try {
      conn = this.open();
      statement = JdbcUtil.getPstForUpdate(conn, false, sql, params);
      statement.setQueryTimeout(queryTimeOut);
      int executed = JdbcUtil.executeUpdate(statement);

      return this.sqlLog.showAffectedRows(executed);
    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      JdbcUtil.close(null, statement, this::close);
    }
  }

  private int create(String sql, Collection<?> params, boolean autoGeneratedPK, ThrowingConsumer<ResultSet, SQLException> consumer) {
    this.sqlLog.showSql(sql, params);

    int               result;
    Connection        conn;
    PreparedStatement statement = null;
    ResultSet         rs        = null;

    try {
      conn = this.open();
      statement = JdbcUtil.getPstForUpdate(conn, autoGeneratedPK, sql, params);
      statement.setQueryTimeout(this.dbTemplate.getDbConfig().getQueryTimeout());
      result = JdbcUtil.executeUpdate(statement);

      if (autoGeneratedPK) {
        rs = statement.getGeneratedKeys();
        consumer.accept(rs);
      }

      return this.sqlLog.showAffectedRows(result);
    } catch (Exception ex) {
      throw new DBException(ex);
    } finally {
      JdbcUtil.close(rs, statement, this::close);
    }
  }

  private int createBatch(String sql, Collection<Collection<?>> paramsList, boolean autoGeneratedPK, ThrowingConsumer<ResultSet, SQLException> consumer) {
    this.sqlLog.showBatchSql(sql, paramsList);

    int               result;
    Connection        conn;
    PreparedStatement statement = null;
    ResultSet         rs        = null;

    try {

      conn = this.open();
      statement = JdbcUtil.getPstForUpdate(conn, autoGeneratedPK, sql);
      statement.setQueryTimeout(this.dbTemplate.getDbConfig().getQueryTimeout());
      result = JdbcUtil.executeBatch(statement, paramsList, this.dbTemplate.getDbConfig().getBatchSize());

      if (autoGeneratedPK) {
        rs = statement.getGeneratedKeys();
        consumer.accept(rs);
      }

      return this.sqlLog.showAffectedRows(result);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      JdbcUtil.close(rs, statement, this::close);
    }
  }

  private int updateBatch(String sql, Collection<Collection<?>> paramsList) {
    this.sqlLog.showBatchSql(sql, paramsList);

    Connection        conn;
    PreparedStatement statement = null;

    try {
      conn = this.open();
      statement = JdbcUtil.getPstForUpdate(conn, false, sql);
      statement.setQueryTimeout(this.dbTemplate.getDbConfig().getQueryTimeout());
      int executed = JdbcUtil.executeBatch(statement, paramsList, this.dbTemplate.getDbConfig().getBatchSize());

      return this.sqlLog.showAffectedRows(executed);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      JdbcUtil.close(null, statement, this::close);
    }
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params) {

    return execute(sql, params, this.dbTemplate.getDbConfig().getQueryTimeout());
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql          SQL
   * @param queryTimeout 超时时间(单位：秒)
   * @return 影响行数
   */
  public int execute(Sql sql, int queryTimeout) {

    return this.execute(sql.toString(), sql.getParams(), queryTimeout);
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   * @return 影响行数
   */
  public int execute(Sql sql) {

    return this.execute(sql, this.dbTemplate.getDbConfig().getQueryTimeout());
  }

  /**
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   * @return 返回多个影响行数
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
   * 执行多个SQL语句
   *
   * @param sqlList      SQL集合
   * @param queryTimeout 超时时间
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList, int queryTimeout) {
    int   sqlCnt  = sqlList.size();
    int[] results = new int[sqlCnt];

    return tx(() -> {
      for (int i = 0; i < sqlCnt; i++) {
        results[i] = execute(sqlList.get(i), queryTimeout);
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

    ClassMeta      classMeta       = Metadata.get(modelClass);
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

    sql = this.dbTemplate.getDbConfig().getDialect().insert(modelClass, entity);

    result = this.create(sql.toString(), sql.getParams(), autoGeneratedPK, rs -> {
      if (rs.next()) {
//        ReflectUtil.setFieldValue(entity, primaryKey.getFieldVisitor().getField(), rs.getObject(1));
        primaryKey.getFieldVisitor().setValue(entity, rs.getObject(1));
      }
    });

    return result;
  }

  public long snowFlakeId() {
    return dbTemplate.getPkGenerator().nextSnowFakeId();
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
    if (CollectionUtil.isEmpty(entities)) {
      return 0;
    }

    ClassMeta classMeta = Metadata.get(modelClass);

    final boolean isMySQL    = this.dbTemplate.getDbConfig().getDbType().equals(DbType.MYSQL);
    final boolean isIdentity = classMeta.getStrategy().equals(GenerationType.IDENTITY);

    if (!isMySQL) {
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

    final Attributes     updateAttributes = classMeta.getUpdateAttributes();
    final GenerationType strategy         = classMeta.getStrategy();
    final Attribute      primaryKey       = classMeta.checkPrimaryKey();

    final List<Sql> sqlList = new ArrayList<>();

    final int                batchSize = this.dbTemplate.getDbConfig().getBatchSize();
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

  private <TModel> Object checkAndGeneratePKValue(GenerationType strategy, Attribute primaryKey, TModel entity) {
    Object pkVal = primaryKey.getFieldVisitor().getValue(entity);
    if (pkVal != null) return pkVal;

    if (strategy == GenerationType.ASSIGNED)
      throw new DBException("Assigned ID can not be null.");

    if (strategy == GenerationType.UUID) {
      pkVal = uuid();
    } else if (strategy == GenerationType.SNOW_FLAKE) {
      pkVal = snowFlakeId();
    }
    primaryKey.getFieldVisitor().setValue(entity, pkVal);
    return pkVal;
  }

  private <TModel> int createInJdbcBatch(ClassMeta classMeta, Collection<TModel> entities) {

    int                       result;
    GenerationType            strategy;
    Object                    pkVal;
    boolean                   autoGeneratedPK = false;
    Attributes                updateAttributes;
    Sql                       sql;
    Collection<Collection<?>> paramsList;
    Collection<Object>        params;

    Attribute primaryKey = classMeta.checkPrimaryKey();

    strategy = classMeta.getStrategy();
    updateAttributes = classMeta.getUpdateAttributes();
    sql = this.dbTemplate.getDbConfig().getDialect().insert(classMeta.getClazz());
    paramsList = new LinkedList<>();

    if (strategy == GenerationType.IDENTITY) {
      autoGeneratedPK = true;
    }

    for (TModel entity : entities) {
      params = new LinkedList<>();

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

    result = this.createBatch(sql.toString(), paramsList, autoGeneratedPK, rs -> {
      for (TModel tModel : entities) {
        if (rs.next()) {
          primaryKey.getFieldVisitor().setValue(tModel, rs.getObject(1));
        }
      }
    });

    return result;
  }

  public String uuid() {
    return this.dbTemplate.getPkGenerator().nextUUID();
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

    if (entity == null) {
      return 0;
    }
    Sql sql = this.dbTemplate.getDbConfig().getDialect().update(modelClass, entity, false).and(Cond.logicalDeleteCond(Metadata.get(modelClass)));
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

    if (entity == null) {
      return 0;
    }
    Sql sql = this.dbTemplate.getDbConfig().getDialect().update(modelClass, entity, true).and(Cond.logicalDeleteCond(Metadata.get(modelClass)));
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

    if (entity == null) {
      return 0;
    }
    List<TModel> entities = new ArrayList<>();
    entities.add(entity);
    return update(modelClass, entities, columns);
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

    ClassMeta       classMeta = Metadata.get(modelClass);
    Attribute       pk        = classMeta.checkPrimaryKey();
    List<Attribute> updAttrs  = classMeta.getUpdateAttributes(columns);

    if (updAttrs.isEmpty()) {
      throw new DBException("Could not match update attributes.");
    }

    final int                batchSize = this.dbTemplate.getDbConfig().getBatchSize();
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

    int result;

    ClassMeta       classMeta = Metadata.get(modelClass);
    Attribute       pk        = classMeta.checkPrimaryKey();
    List<Attribute> updAttrs  = classMeta.getUpdateAttributes(columns);

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

    paramsList = new LinkedList<>();

    for (TModel entity : entities) {
      params = new LinkedList<>();

      for (Attribute attributeMeta : updAttrs) {
        params.add(attributeMeta.getFieldVisitor().getValue(entity));
      }

      params.add(pk.getFieldVisitor().getValue(entity));
      paramsList.add(params);
    }

    result = this.updateBatch(sql.toString(), paramsList);

    return result;
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

    ClassMeta classMeta = ClassMeta.createInstance(modelClass);
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

    Attribute    primaryKey = Metadata.get(modelClass).getPrimaryKey();
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

    ClassMeta classMeta    = Metadata.get(modelClass);
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
    ClassMeta classMeta = Metadata.get(modelClass);
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

    ClassMeta classMeta = Metadata.get(modelClass);
    return deleteByCond(classMeta, cond);
  }

  /**
   * 快速截断表数据
   *
   * @param modelClass 实体类型
   * @param <TModel>   实体类型泛型
   */
  public <TModel> void truncate(Class<TModel> modelClass) {
    ClassMeta classMeta = Metadata.get(modelClass);
    String    tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    execute(new Sql(StrUtil.format("TRUNCATE TABLE {}", tableName)));
  }

  private int deleteByCond(ClassMeta classMeta, Cond deleteCond) {
    Sql    sql;
    String tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    if (classMeta.getLogicDelete() != null) {
      sql = Sql.Update(tableName)
               .set(StrUtil.format("{} = {}", classMeta.getLogicDelete().getColumnName(), classMeta.getPrimaryKey().getColumnName()))
               .where(deleteCond).and(Cond.logicalDeleteCond(classMeta));
    } else {
      sql = Sql.Delete(tableName).where(deleteCond);
    }

    return execute(sql);
  }
}
