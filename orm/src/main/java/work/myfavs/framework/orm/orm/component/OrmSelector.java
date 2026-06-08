package work.myfavs.framework.orm.orm.component;

import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.annotation.Criteria;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体查询器，封装实体查询、单行查询、Map 查询、条件查询等
 */
public class OrmSelector {

  private final Database database;
  private final OrmSqlBuilder sqlBuilder;

  public OrmSelector(Database database, OrmSqlBuilder sqlBuilder) {
    this.database = database;
    this.sqlBuilder = sqlBuilder;
  }

  /**
   * 执行 SQL，返回多行记录
   */
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {
    try (Query query = this.database.createQuery(sql)) {
      return query.addParameters(params).find(viewClass);
    }
  }

  /**
   * 执行 {@link Sql}，返回多行记录
   */
  public <TView> List<TView> find(Class<TView> viewClass, Sql sql) {
    return this.find(viewClass, sql.toString(), sql.getParams());
  }

  /**
   * 执行 SQL ,并返回 1 行记录
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
   */
  public <TView> TView get(Class<TView> viewClass, Sql sql) {
    return this.get(viewClass, sql.toString(), sql.getParams());
  }

  /**
   * 执行SQL，并返回Map
   */
  public <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, String sql, Collection<?> params) {
    Field field = ReflectUtil.getField(viewClass, keyField);

    Objects.requireNonNull(field, String.format("Class %s not exist field named %s", viewClass.getName(), keyField));

    return this.find(viewClass, sql, params).stream()
        .collect(Collectors.toMap(tView -> ReflectUtil.getFieldValue(field, tView), tView -> tView, (a, b) -> b));
  }

  /**
   * 执行 {@link Sql}，并返回 Map
   */
  public <TKey, TView> Map<TKey, TView> findMap(Class<TView> viewClass, String keyField, Sql sql) {
    return findMap(viewClass, keyField, sql.toString(), sql.getParams());
  }

  /**
   * 根据主键获取记录
   */
  public <TView> TView getById(Class<TView> viewClass, Object id) {
    if (null == id) {
      return null;
    }

    final ClassMeta entityMeta = Metadata.entityMeta(viewClass);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.select(entityMeta)
        .where(Cond.eq(primaryKey.getColumnName(), id))
        .and(Cond.logicalDelete(logicDelete));

    return this.get(viewClass, sql);
  }

  /**
   * 根据指定字段获取记录
   */
  public <TView> TView getByField(Class<TView> viewClass, String field, Object param) {

    final ClassMeta classMeta = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = classMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.select(classMeta)
        .where(Cond.eq(field, param, false))
        .and(Cond.logicalDelete(logicDelete));
    return this.get(viewClass, sql);
  }

  /**
   * 根据 {@link Cond} 条件获取记录
   */
  public <TView> TView getByCond(Class<TView> viewClass, Cond cond) {

    final ClassMeta classMeta = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = classMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.select(classMeta)
        .where()
        .and(cond)
        .and(Cond.logicalDelete(logicDelete));
    return this.get(viewClass, sql);
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询记录
   */
  public <TView> TView getByCriteria(Class<TView> viewClass, Object object) {
    return this.getByCond(viewClass, Cond.criteria(object));
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询记录
   */
  public <TView> TView getByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup) {
    return this.getByCond(viewClass, Cond.criteria(object, criteriaGroup));
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询实体集合
   */
  public <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object) {
    return findByCond(viewClass, Cond.criteria(object));
  }

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询实体集合
   */
  public <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup) {
    return findByCond(viewClass, Cond.criteria(object, criteriaGroup));
  }

  /**
   * 根据多个主键ID查询实体集合
   */
  public <TView> List<TView> findByIds(Class<TView> viewClass, Collection<?> ids) {

    final ClassMeta entityMeta = Metadata.entityMeta(viewClass);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.select(entityMeta)
        .where()
        .and(Cond.in(primaryKey.getColumnName(), ids, false))
        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Object param) {

    final ClassMeta entityMeta = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.select(entityMeta)
        .where(Cond.eq(field, param, false))
        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 根据字段查询实体集合
   */
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Collection<?> params) {

    final ClassMeta entityMeta = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.select(entityMeta)
        .where()
        .and(Cond.in(field, params, false))
        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 根据 {@link Cond} 条件查询实体集合
   */
  public <TView> List<TView> findByCond(Class<TView> viewClass, Cond cond) {

    final ClassMeta entityMeta = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = entityMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.select(entityMeta)
        .where()
        .and(cond)
        .and(Cond.logicalDelete(logicDelete));
    return this.find(viewClass, sql);
  }

  /**
   * 获取 SQL 的行数
   */
  public long count(String sql, Collection<?> params) {
    final Sql countSql = this.sqlBuilder.countSql(sql, params);
    return this.get(Number.class, countSql).longValue();
  }

  /**
   * 获取 {@link Sql} 的行数
   */
  public long count(Sql sql) {
    return this.count(sql.toString(), sql.getParams());
  }

  /**
   * 根据 {@link Cond} 条件获取查询的行数
   */
  public <TView> long countByCond(Class<TView> viewClass, Cond cond) {

    final ClassMeta classMeta = Metadata.entityMeta(viewClass);
    final Attribute logicDelete = classMeta.getLogicDelete();

    final Sql sql = this.sqlBuilder.countSql(classMeta)
        .where()
        .and(cond)
        .and(Cond.logicalDelete(logicDelete));
    return this.get(Number.class, sql).longValue();
  }

  /**
   * 根据传入的 {@link Sql} 判断是否存在符合条件的数据
   */
  public boolean exists(Sql sql) {
    return exists(sql.toString(), sql.getParams());
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   */
  public boolean exists(String sql, Collection<?> params) {
    return this.count(sql, params) > 0L;
  }

  /**
   * 根据 {@link Cond} 条件判断是否存在符合条件的数据
   */
  public <TView> boolean existsByCond(Class<TView> viewClass, Cond cond) {
    return this.countByCond(viewClass, cond) > 0L;
  }

  /**
   * 判断实体（根据ID）是否存在
   */
  public <TModel> boolean exists(Class<TModel> modelClass, TModel entity) {
    if (null == entity) return false;

    final ClassMeta entityMeta = Metadata.entityMeta(modelClass);
    final Attribute primaryKey = entityMeta.checkPrimaryKey();
    final Object pkVal = primaryKey.getValue(entity);

    if (null == pkVal) return false;

    final Sql existSql = this.sqlBuilder.countSql(entityMeta).where(Cond.eq(primaryKey.getColumnName(), pkVal));
    return this.count(existSql) > 0L;
  }
}
