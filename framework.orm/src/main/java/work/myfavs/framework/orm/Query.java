package work.myfavs.framework.orm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import work.myfavs.framework.orm.meta.Record;
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
   * 执行SQL，返回多行记录
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
   * 执行SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
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
  public List<Record> find(String sql, Collection<?> params) {

    return this.find(Record.class, sql, params);
  }

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql SQL
   * @return 结果集
   */
  public List<Record> find(Sql sql) {

    return this.find(Record.class, sql);
  }

  /**
   * 执行SQL，并返回Map
   *
   * @param viewClass 结果集类型
   * @param keyField  返回Map的Key的字段，必须是viewClass中存在的字段
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
   * 执行SQL，并返回Map
   *
   * @param viewClass 结果集类型
   * @param keyField  返回Map的Key的字段，必须是viewClass中存在的字段
   * @param sql       SQL
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

    Sql querySql = getDialect().selectPage(1, top, sql, params);
    return this.find(viewClass, querySql);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       SQL
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
  public List<Record> findTop(int top, String sql, Collection<?> params) {

    return this.findTop(Record.class, top, sql, params);
  }

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql SQL
   * @return 结果集
   */
  public List<Record> findTop(int top, Sql sql) {

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
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL
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
  public Record get(String sql, Collection<?> params) {

    return this.get(Record.class, sql, params);
  }

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql SQL
   * @return 记录
   */
  public Record get(Sql sql) {

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

    ClassMeta classMeta  = Metadata.get(viewClass);
    Attribute primaryKey = classMeta.checkPrimaryKey();

    Sql sql =
        getDialect()
            .select(viewClass)
            .where(Cond.eq(primaryKey.getColumnName(), id))
            .and(Cond.logicalDeleteCond(classMeta));

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

    Sql sql =
        getDialect()
            .select(viewClass)
            .where(Cond.eq(field, param, false))
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(viewClass, sql);
  }

  /**
   * 根据条件获取记录
   *
   * @param viewClass 结果类型
   * @param cond      条件
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> TView getByCond(Class<TView> viewClass, Cond cond) {

    Sql sql =
        getDialect()
            .select(viewClass)
            .where()
            .and(cond)
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(viewClass, sql);
  }

  /**
   * 根据 @Criteria 注解生成的条件查询记录
   *
   * @param viewClass 结果类型
   * @param object    包含 @Criteria 注解Field的对象
   * @param <TView>   实体类型
   * @return 记录
   */
  public <TView> TView getByCriteria(Class<TView> viewClass, Object object) {

    return this.getByCond(viewClass, Cond.create(object));
  }

  /**
   * 根据 @Criteria 注解生成的条件查询记录
   *
   * @param viewClass     结果类型
   * @param object        包含 @Criteria 注解Field的对象
   * @param criteriaGroup 条件组名
   * @param <TView>       实体类型
   * @return 记录
   */
  public <TView> TView getByCriteria(
      Class<TView> viewClass, Object object, Class<?> criteriaGroup) {

    return this.getByCond(viewClass, Cond.create(object, criteriaGroup));
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

    ClassMeta classMeta  = Metadata.get(viewClass);
    Attribute primaryKey = classMeta.checkPrimaryKey();
    Sql sql =
        getDialect()
            .select(viewClass)
            .where()
            .and(Cond.in(primaryKey.getColumnName(), ids, false))
            .and(Cond.logicalDeleteCond(classMeta));
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

    Sql sql =
        getDialect()
            .select(viewClass)
            .where(Cond.eq(field, param, false))
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
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
  public <TView> List<TView> findByField(
      Class<TView> viewClass, String field, Collection<?> params) {

    Sql sql =
        getDialect()
            .select(viewClass)
            .where()
            .and(Cond.in(field, params, false))
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param cond      查询条件
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCond(Class<TView> viewClass, Cond cond) {

    Sql sql =
        getDialect()
            .select(viewClass)
            .where()
            .and(cond)
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.find(viewClass, sql);
  }

  /**
   * 根据 @Criterion 注解生成的条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param object    包含 @Criterion 注解Field的对象
   * @param <TView>   实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object) {

    return findByCond(viewClass, Cond.create(object));
  }

  /**
   * 根据 @Criteria 注解生成的条件查询实体集合
   *
   * @param viewClass     结果类型
   * @param object        包含 @Criteria 注解Field的对象
   * @param criteriaGroup 条件组名
   * @param <TView>       实体类型
   * @return 实体集合
   */
  public <TView> List<TView> findByCriteria(
      Class<TView> viewClass, Object object, Class<?> criteriaGroup) {

    return findByCond(viewClass, Cond.create(object, criteriaGroup));
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
   * 获取 SQL 的行数
   *
   * @param sql SQL
   * @return 行数
   */
  public long count(Sql sql) {

    return this.count(sql.toString(), sql.getParams());
  }

  /**
   * 根据条件获取查询的行数
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 行数
   */
  public <TView> long countByCond(Class<TView> viewClass, Cond cond) {

    Sql sql =
        getDialect()
            .count(viewClass)
            .where()
            .and(cond)
            .and(Cond.logicalDeleteCond(Metadata.get(viewClass)));
    return this.get(Number.class, sql).longValue();
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql SQL
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public boolean exists(Sql sql) {

    return exists(sql.toString(), sql.getParams());
  }

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 查询结果行数大于0返回true，否则返回false
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
   * @return 存在返回true，不存在返回false
   */
  public <TModel> boolean exists(Class<TModel> modelClass, TModel entity) {
    if (entity == null) {
      return false;
    }

    Attribute primaryKey = Metadata.get(modelClass).checkPrimaryKey();
    Object    pkVal      = primaryKey.getFieldVisitor().getValue(entity);
    if (pkVal == null) {
      return false;
    }
    Sql existSql = getDialect().count(modelClass).where(Cond.eq(primaryKey.getColumnName(), pkVal));
    return exists(existSql);
  }

  /**
   * 根据条件判断是否存在符合条件的数据
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public <TView> boolean existsByCond(Class<TView> viewClass, Cond cond) {

    return this.countByCond(viewClass, cond) > 0L;
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    Sql querySql = new Sql(sql, params);

    if (enablePage) {
      querySql = createPageSql(currentPage, pageSize, sql, params);
    }

    List<TView> data = this.find(viewClass, querySql);

    return PageLite.createInstance(this.dbTemplate, data, currentPage, pageSize);
  }

  /**
   * 创建分页SQL
   *
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param sql         SQL语句
   * @param params      参数
   * @return 分页SQL
   */
  private Sql createPageSql(int currentPage, int pageSize, String sql, Collection<?> params) {
    if (currentPage < 1) {
      throw new DBException("当前页码 (currentPage) 参数必须大于等于 1");
    }

    if (pageSize < 1) {
      throw new DBException("每页记录数 (pageSize) 参数必须大于等于 1");
    }

    long maxPageSize = getDbConfig().getMaxPageSize();
    if (maxPageSize > 0L && pageSize > maxPageSize) {
      throw new DBException("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize);
    }

    return getDialect().selectPage(currentPage, pageSize, sql, params);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(
        viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  分页对象
   * @param <TView>   结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {

    return this.findPageLite(
        viewClass,
        sql,
        params,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL
   * @param pageable  分页对象
   * @param <TView>   结果类型泛型
   * @return 简单分页结果集
   */
  public <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable) {

    return this.findPageLite(
        viewClass,
        sql.toString(),
        sql.getParams(),
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPageLite(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(String sql, Collection<?> params, IPageable pageable) {

    return this.findPageLite(Record.class, sql, params, pageable);
  }

  /**
   * 执行 SQL 语句，返回简单分页结果集
   *
   * @param sql      SQL
   * @param pageable 分页对象
   * @return 简单分页结果集
   */
  public PageLite<Record> findPageLite(Sql sql, IPageable pageable) {

    return this.findPageLite(Record.class, sql, pageable);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize) {

    long totalPages   = 1;
    long totalRecords = 0;
    Sql  querySql     = new Sql(sql, params);

    if (enablePage) {
      totalRecords = this.count(sql, params);
      totalPages = totalRecords / pageSize;

      if (totalRecords % pageSize != 0) {
        totalPages++;
      }

      querySql = createPageSql(currentPage, pageSize, sql, params);
    }

    List<TView> data = this.find(viewClass, querySql);

    if (!enablePage) {
      totalRecords = data.size();
    }

    return Page.createInstance(
        this.dbTemplate, data, currentPage, pageSize, totalPages, totalRecords);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass   返回的数据类型
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TView>     结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return findPage(viewClass, sql.toString(), sql.getParams(), enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL语句
   * @param params    参数
   * @param pageable  可分页对象
   * @param <TView>   结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable) {

    return findPage(
        viewClass,
        sql,
        params,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       SQL
   * @param pageable  可分页对象
   * @param <TView>   结果类型泛型
   * @return 分页结果集
   */
  public <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable) {

    return findPage(
        viewClass,
        sql.toString(),
        sql.getParams(),
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL语句
   * @param params      参数
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public Page<Record> findPage(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, params, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql         SQL
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql, boolean enablePage, int currentPage, int pageSize) {

    return this.findPage(Record.class, sql, enablePage, currentPage, pageSize);
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable 可分页对象
   * @return 分页结果集
   */
  public Page<Record> findPage(String sql, Collection<?> params, IPageable pageable) {

    return this.findPage(
        Record.class,
        sql,
        params,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }

  /**
   * 执行 SQL 语句，返回分页结果集
   *
   * @param sql      SQL
   * @param pageable 可分页对象
   * @return 分页结果集
   */
  public Page<Record> findPage(Sql sql, IPageable pageable) {

    return this.findPage(
        Record.class,
        sql,
        pageable.getEnablePage(),
        pageable.getCurrentPage(),
        pageable.getPageSize());
  }
}
