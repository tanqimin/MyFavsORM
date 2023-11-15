package work.myfavs.framework.orm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.lang.Assert;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.annotation.Criteria;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.JdbcUtil;
import work.myfavs.framework.orm.util.convert.DBConvert;
import work.myfavs.framework.orm.util.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

abstract public class Query extends MyfavsConnection {

  public Query(DBTemplate dbTemplate) {
    super(dbTemplate);
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

    Metadata.get(viewClass);

    Connection        conn;
    PreparedStatement statement = null;
    ResultSet         rs        = null;
    List<TView>       result;

    try {
      conn = this.open();
      statement = JdbcUtil.getPstForQuery(conn, sql, params);
      statement.setQueryTimeout(getDbConfig().getQueryTimeout());
      statement.setFetchSize(getDbConfig().getFetchSize());
      rs = statement.executeQuery();

      result = DBConvert.toList(viewClass, rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      JdbcUtil.close(rs, statement, this::close);
    }

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
