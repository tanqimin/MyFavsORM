package work.myfavs.framework.orm.orm.impl;

import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.orm.Orm;
import work.myfavs.framework.orm.orm.component.*;
import work.myfavs.framework.orm.orm.dialect.SqlDialect;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * ORM 通用实体操作
 * <p>通过组合模式将各职责委派给对应的组件：</p>
 * <ul>
 *   <li>{@link OrmExecutor} — SQL 执行</li>
 *   <li>{@link OrmInserter} — 实体创建</li>
 *   <li>{@link OrmUpdater} — 实体更新</li>
 *   <li>{@link OrmDeleter} — 实体删除</li>
 *   <li>{@link OrmSelector} — 实体查询</li>
 *   <li>{@link OrmPager} — 分页查询</li>
 *   <li>{@link OrmSqlBuilder} — SQL 语句构建</li>
 * </ul>
 * 子类通过 {@link work.myfavs.framework.orm.orm.dialect.SqlDialect} 注入方言策略。
 */
public abstract class AbstractOrm implements Orm {

  protected final Database database;
  protected final DBTemplate dbTemplate;
  protected final DBConfig dbConfig;

  protected final OrmExecutor executor;
  protected final OrmInserter inserter;
  protected final OrmUpdater updater;
  protected final OrmDeleter deleter;
  protected final OrmSelector selector;
  protected final OrmPager pager;

  /**
   * 构造 AbstractOrm 实例
   *
   * @param database {@link Database} 实例
   * @param dialect  {@link SqlDialect} 方言实例
   */
  public AbstractOrm(Database database, SqlDialect dialect) {
    this.database = database;
    this.dbTemplate = this.database.getDbTemplate();
    this.dbConfig = this.dbTemplate.getDbConfig();

    OrmSqlBuilder sqlBuilder = new OrmSqlBuilder(dialect);
    this.executor = new OrmExecutor(database);
    this.inserter = new OrmInserter(database, dbTemplate, sqlBuilder, executor);
    this.updater = new OrmUpdater(database, sqlBuilder, executor, inserter);
    this.deleter = new OrmDeleter(database, sqlBuilder, executor);
    this.selector = new OrmSelector(database, sqlBuilder);
    this.pager = new OrmPager(selector, sqlBuilder, dbConfig, dialect);
  }

  // ======================== execute ========================

  @Override
  public int execute(String sql, Collection<?> params, ThrowingConsumer<PreparedStatement, SQLException> configConsumer) {
    return executor.execute(sql, params, configConsumer);
  }

  @Override
  public int execute(String sql, Collection<?> params, int timeout) {
    return executor.execute(sql, params, timeout);
  }

  @Override
  public int execute(String sql, Collection<?> params) {
    return executor.execute(sql, params);
  }

  @Override
  public int execute(Sql sql, int timeout) {
    return executor.execute(sql, timeout);
  }

  @Override
  public int execute(Sql sql) {
    return executor.execute(sql);
  }

  @Override
  public int[] execute(List<Sql> sqlList) {
    return executor.execute(sqlList);
  }

  @Override
  public int[] execute(List<Sql> sqlList, int timeout) {
    return executor.execute(sqlList, timeout);
  }

  @Override
  public int[] execute(List<Sql> sqlList, ThrowingConsumer<PreparedStatement, SQLException> configConsumer) {
    return executor.execute(sqlList, configConsumer);
  }

  // ======================== create ========================

  @Override
  public <TModel> int create(Class<TModel> modelClass, TModel entity) {
    return inserter.create(modelClass, entity);
  }

  @Override
  public <TModel> int create(Class<TModel> modelClass, Collection<TModel> entities) {
    return inserter.create(modelClass, entities);
  }

  // ======================== update ========================

  @Override
  public <TModel> int update(Class<TModel> modelClass, TModel entity) {
    return updater.update(modelClass, entity);
  }

  @Override
  public <TModel> int updateIgnoreNull(Class<TModel> modelClass, TModel entity) {
    return updater.updateIgnoreNull(modelClass, entity);
  }

  @Override
  public <TModel> int update(Class<TModel> modelClass, TModel entity, String[] columns) {
    return updater.update(modelClass, entity, columns);
  }

  @Override
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {
    return updater.update(modelClass, entities, columns);
  }

  @Override
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities) {
    return updater.update(modelClass, entities);
  }

  @Override
  public <TModel> int createOrUpdate(Class<TModel> modelClass, TModel entity) {
    return updater.createOrUpdate(modelClass, entity);
  }

  // ======================== delete ========================

  @Override
  public <TModel> int delete(Class<TModel> modelClass, TModel entity) {
    return deleter.delete(modelClass, entity);
  }

  @Override
  public <TModel> int delete(Class<TModel> modelClass, Collection<TModel> entities) {
    return deleter.delete(modelClass, entities);
  }

  @Override
  public <TModel> int deleteByIds(Class<TModel> modelClass, Collection<?> ids) {
    return deleter.deleteByIds(modelClass, ids);
  }

  @Override
  public <TModel> int deleteById(Class<TModel> modelClass, Object id) {
    return deleter.deleteById(modelClass, id);
  }

  @Override
  public <TModel> int deleteByCond(Class<TModel> modelClass, Cond cond) {
    return deleter.deleteByCond(modelClass, cond);
  }

  @Override
  public <TModel> void truncate(Class<TModel> modelClass) {
    deleter.truncate(modelClass);
  }

  // ======================== find ========================

  @Override
  public <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params) {
    return selector.find(viewClass, sql, params);
  }

  @Override
  public <TView> List<TView> find(Class<TView> viewClass, Sql sql) {
    return selector.find(viewClass, sql);
  }

  @Override
  public List<Record> findRecords(String sql, Collection<?> params) {
    return selector.find(Record.class, sql, params);
  }

  @Override
  public List<Record> findRecords(Sql sql) {
    return selector.find(Record.class, sql);
  }

  @Override
  public <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, String sql, Collection<?> params) {
    return selector.findMap(viewClass, keyField, sql, params);
  }

  @Override
  public <TKey, TView> Map<TKey, TView> findMap(Class<TView> viewClass, String keyField, Sql sql) {
    return selector.findMap(viewClass, keyField, sql);
  }

  @Override
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, String sql, Collection<?> params) {
    return pager.findTop(viewClass, top, sql, params);
  }

  @Override
  public <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql) {
    return pager.findTop(viewClass, top, sql);
  }

  @Override
  public List<Record> findTopRecords(int top, String sql, Collection<?> params) {
    return pager.findTop(Record.class, top, sql, params);
  }

  @Override
  public List<Record> findTopRecords(int top, Sql sql) {
    return pager.findTop(Record.class, top, sql);
  }

  // ======================== get ========================

  @Override
  public <TView> TView get(Class<TView> viewClass, String sql, Collection<?> params) {
    return selector.get(viewClass, sql, params);
  }

  @Override
  public <TView> TView get(Class<TView> viewClass, Sql sql) {
    return selector.get(viewClass, sql);
  }

  @Override
  public Record getRecord(String sql, Collection<?> params) {
    return selector.get(Record.class, sql, params);
  }

  @Override
  public Record getRecord(Sql sql) {
    return selector.get(Record.class, sql);
  }

  // ======================== getBy ========================

  @Override
  public <TView> TView getById(Class<TView> viewClass, Object id) {
    return selector.getById(viewClass, id);
  }

  @Override
  public <TView> TView getByField(Class<TView> viewClass, String field, Object param) {
    return selector.getByField(viewClass, field, param);
  }

  @Override
  public <TView> TView getByCond(Class<TView> viewClass, Cond cond) {
    return selector.getByCond(viewClass, cond);
  }

  @Override
  public <TView> TView getByCriteria(Class<TView> viewClass, Object object) {
    return selector.getByCriteria(viewClass, object);
  }

  @Override
  public <TView> TView getByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup) {
    return selector.getByCriteria(viewClass, object, criteriaGroup);
  }

  // ======================== findBy ========================

  @Override
  public <TView> List<TView> findByIds(Class<TView> viewClass, Collection<?> ids) {
    return selector.findByIds(viewClass, ids);
  }

  @Override
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Object param) {
    return selector.findByField(viewClass, field, param);
  }

  @Override
  public <TView> List<TView> findByField(Class<TView> viewClass, String field, Collection<?> params) {
    return selector.findByField(viewClass, field, params);
  }

  @Override
  public <TView> List<TView> findByCond(Class<TView> viewClass, Cond cond) {
    return selector.findByCond(viewClass, cond);
  }

  @Override
  public <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object) {
    return selector.findByCriteria(viewClass, object);
  }

  @Override
  public <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup) {
    return selector.findByCriteria(viewClass, object, criteriaGroup);
  }

  // ======================== count ========================

  @Override
  public long count(String sql, Collection<?> params) {
    return selector.count(sql, params);
  }

  @Override
  public long count(Sql sql) {
    return selector.count(sql);
  }

  @Override
  public <TView> long countByCond(Class<TView> viewClass, Cond cond) {
    return selector.countByCond(viewClass, cond);
  }

  // ======================== exists ========================

  @Override
  public boolean exists(Sql sql) {
    return selector.exists(sql);
  }

  @Override
  public boolean exists(String sql, Collection<?> params) {
    return selector.exists(sql, params);
  }

  @Override
  public <TModel> boolean exists(Class<TModel> modelClass, TModel entity) {
    return selector.exists(modelClass, entity);
  }

  @Override
  public <TView> boolean existsByCond(Class<TView> viewClass, Cond cond) {
    return selector.existsByCond(viewClass, cond);
  }

  // ======================== page ========================

  @Override
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPageLite(viewClass, sql, params, enablePage, currentPage, pageSize);
  }

  @Override
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPageLite(viewClass, sql, enablePage, currentPage, pageSize);
  }

  @Override
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    return pager.findPageLite(viewClass, sql, params, pageable);
  }

  @Override
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {
    return pager.findPageLite(viewClass, sql, pageable);
  }

  @Override
  public PageLite<Record> findRecordsPageLite(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  @Override
  public PageLite<Record> findRecordsPageLite(Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
  }

  @Override
  public PageLite<Record> findRecordsPageLite(String sql, Collection<?> params, IPageable pageable) {
    return pager.findPageLite(Record.class, sql, params, pageable);
  }

  @Override
  public PageLite<Record> findRecordsPageLite(Sql sql, IPageable pageable) {
    return pager.findPageLite(Record.class, sql, pageable);
  }

  @Override
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPage(viewClass, sql, params, enablePage, currentPage, pageSize);
  }

  @Override
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPage(viewClass, sql, enablePage, currentPage, pageSize);
  }

  @Override
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {
    return pager.findPage(viewClass, sql, params, pageable);
  }

  @Override
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {
    return pager.findPage(viewClass, sql, pageable);
  }

  @Override
  public Page<Record> findRecordsPage(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  @Override
  public Page<Record> findRecordsPage(Sql sql, boolean enablePage, int currentPage, int pageSize) {
    return pager.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  @Override
  public Page<Record> findRecordsPage(String sql, Collection<?> params, IPageable pageable) {
    return pager.findPage(Record.class, sql, params, pageable);
  }

  @Override
  public Page<Record> findRecordsPage(Sql sql, IPageable pageable) {
    return pager.findPage(Record.class, sql, pageable);
  }
}
