package work.myfavs.framework.orm.orm.component;

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.Constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 实体删除器，处理删除和截断逻辑
 */
public class OrmDeleter {

  private final Database database;
  private final OrmSqlBuilder sqlBuilder;
  private final OrmExecutor executor;

  /**
   * 构造 OrmDeleter 实例.
   *
   * @param database   {@link Database} 实例
   * @param sqlBuilder {@link OrmSqlBuilder} 实例
   * @param executor   {@link OrmExecutor} 实例
   */
  public OrmDeleter(Database database, OrmSqlBuilder sqlBuilder, OrmExecutor executor) {
    this.database = database;
    this.sqlBuilder = sqlBuilder;
    this.executor = executor;
  }

  /**
   * 删除记录
   */
  public <TModel> int delete(Class<TModel> modelClass, TModel entity) {

    if (null == entity) return 0;

    final ClassMeta classMeta = Metadata.entityMeta(modelClass);
    final Object    pkVal     = classMeta.checkPrimaryKey().getValue(entity);

    return deleteById(classMeta, pkVal);
  }

  /**
   * 批量删除记录
   */
  public <TModel> int delete(Class<TModel> modelClass, Collection<TModel> entities) {

    if (CollectionUtil.isEmpty(entities)) return 0;

    final Attribute primaryKey = Metadata.entityMeta(modelClass).checkPrimaryKey();
    final List<Object> ids = new ArrayList<>();

    for (TModel entity : entities) {
      final Object pkVal = primaryKey.getValue(entity);

      if (null == pkVal) continue;

      ids.add(pkVal);
    }

    return deleteByIds(modelClass, ids);
  }

  /**
   * 根据ID集合删除记录
   */
  public <TModel> int deleteByIds(Class<TModel> modelClass, Collection<?> ids) {

    if (CollectionUtil.isEmpty(ids)) {
      return 0;
    }

    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final String pkColumnName = primaryKey.getColumnName();

    if (this.database.isSqlServer()) {
      int ret = 0;
      List<? extends List<?>> splitParams = CollectionUtil.split(ids, Constant.MAX_PARAM_SIZE_FOR_MSSQL);
      for (List<?> splitParam : splitParams) {
        Cond deleteCond = Cond.in(pkColumnName, splitParam, false);
        ret += deleteByCond(entityMeta, deleteCond);
      }
      return ret;
    }

    final Cond deleteCond = Cond.in(pkColumnName, new ArrayList<>(ids), false);
    return deleteByCond(entityMeta, deleteCond);
  }

  /**
   * 根据 ID 删除记录
   */
  public <TModel> int deleteById(Class<TModel> modelClass, Object id) {

    if (null == id) {
      return 0;
    }
    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    return deleteById(entityMeta, id);
  }

  private int deleteById(ClassMeta entityMeta, Object id) {
    final String pkColumnName = entityMeta.getPrimaryKeyColumnName();
    final Cond deleteCond = Cond.eq(pkColumnName, id);

    return deleteByCond(entityMeta, deleteCond);
  }

  /**
   * 根据条件删除记录
   */
  public <TModel> int deleteByCond(Class<TModel> modelClass, Cond cond) {

    if (null == cond) {
      return 0;
    }

    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    return deleteByCond(entityMeta, cond);
  }

  private int deleteByCond(ClassMeta entityMeta, Cond deleteCond) {
    final String tableName = OrmSqlBuilder.getTableName(entityMeta);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();
    final Sql sql;
    if (null != logicDelete) {
      sql = Sql.Update(tableName)
          .set(String.format("%s = %s", logicDelete.getColumnName(), primaryKey.getColumnName()))
          .where(deleteCond).and(Cond.logicalDelete(logicDelete));
    } else {
      sql = Sql.Delete(tableName).where(deleteCond);
    }
    return executor.execute(sql);
  }

  /**
   * 快速截断表数据
   */
  public <TModel> void truncate(Class<TModel> modelClass) {
    final SQLTruncateStatement truncateStatement = new SQLTruncateStatement();
    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    final String tableName = OrmSqlBuilder.getTableName(entityMeta);

    truncateStatement.getTableSources().add(new SQLExprTableSource(tableName));

    executor.execute(new Sql(truncateStatement.toUnformattedString()));
  }
}
