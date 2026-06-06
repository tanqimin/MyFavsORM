package work.myfavs.framework.orm.orm;

import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.annotation.Criteria;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ORM 实体操作
 */
public interface Orm {

  /**
   * 执行查询，返回影响行数
   *
   * @param sql            SQL语句
   * @param params         参数
   * @param configConsumer 在执行查询前允许，可对 PreparedStatement 进行设置
   * @return 影响行数
   */
  int execute(String sql, Collection<?> params, ThrowingConsumer<PreparedStatement, SQLException> configConsumer);

  /**
   * 执行 SQL 语句，返回影响行数
   *
   * @param sql     SQL语句
   * @param params  参数
   * @param timeout 超时时间(单位：秒)
   * @return 影响行数
   */
  int execute(String sql, Collection<?> params, int timeout);

  /**
   * 执行 SQL 语句，返回影响行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 影响行数
   */
  int execute(String sql, Collection<?> params);

  /**
   * 执行 {@link Sql} 语句，返回影响行数
   *
   * @param sql     {@link Sql} 语句
   * @param timeout 超时时间(单位：秒)
   * @return 影响行数
   */
  int execute(Sql sql, int timeout);

  /**
   * 执行 {@link Sql} 语句，返回影响行数
   *
   * @param sql {@link Sql} 语句
   * @return 影响行数
   */
  int execute(Sql sql);

  /**
   * 执行多个 {@link Sql} 语句
   *
   * @param sqlList {@link Sql} 语句集合
   * @return 返回数组，包含每个查询的影响行数
   */
  int[] execute(List<Sql> sqlList);

  /**
   * 执行多个 {@link Sql} 语句
   *
   * @param sqlList {@link Sql} 语句集合
   * @param timeout 超时时间(单位：秒)
   * @return 返回数组，包含每个查询的影响行数
   */
  int[] execute(List<Sql> sqlList, int timeout);

  /**
   * 执行多个 {@link Sql} 语句
   *
   * @param sqlList        {@link Sql} 语句集合
   * @param configConsumer 在执行查询前允许，可对 PreparedStatement 进行设置
   * @return 返回数组，包含每个查询的影响行数
   */
  int[] execute(List<Sql> sqlList, ThrowingConsumer<PreparedStatement, SQLException> configConsumer);

  /**
   * 创建实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int create(Class<TModel> modelClass, TModel entity);

  /**
   * 批量创建实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int create(Class<TModel> modelClass, Collection<TModel> entities);

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int update(Class<TModel> modelClass, TModel entity);

  /**
   * 更新实体，忽略Null属性的字段
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int updateIgnoreNull(Class<TModel> modelClass, TModel entity);

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param columns    需要更新的列
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int update(Class<TModel> modelClass, TModel entity, String[] columns);

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param columns    需要更新的列
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities, String[] columns);

  /**
   * 更新实体
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities);

  /**
   * 如果记录存在更新，不存在则创建
   *
   * @param modelClass 实体类型
   * @param entity     实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int createOrUpdate(Class<TModel> modelClass, TModel entity);

  /**
   * 删除记录
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int delete(Class<TModel> modelClass, TModel entity);

  /**
   * 批量删除记录
   *
   * @param modelClass 实体类型
   * @param entities   实体集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int delete(Class<TModel> modelClass, Collection<TModel> entities);

  /**
   * 根据ID集合删除记录
   *
   * @param modelClass 实体类型
   * @param ids        ID集合
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int deleteByIds(Class<TModel> modelClass, Collection<?> ids);

  /**
   * 根据ID删除记录
   *
   * @param modelClass 实体类型
   * @param id         ID值
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int deleteById(Class<TModel> modelClass, Object id);

  /**
   * 根据条件删除记录
   *
   * @param modelClass 实体类型
   * @param cond       条件值
   * @param <TModel>   实体类型泛型
   * @return 影响行数
   */
  <TModel> int deleteByCond(Class<TModel> modelClass, Cond cond);

  /**
   * 快速截断表数据
   *
   * @param modelClass 实体类型
   * @param <TModel>   实体类型泛型
   */
  <TModel> void truncate(Class<TModel> modelClass);

  /**
   * 执行 SQL，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  <TView> List<TView> find(Class<TView> viewClass, String sql, Collection<?> params);

  /**
   * 执行 {@link Sql}，返回多行记录
   *
   * @param viewClass 结果集类型
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  <TView> List<TView> find(Class<TView> viewClass, Sql sql);

  /**
   * 执行SQL， 并返回多行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  List<Record> findRecords(String sql, Collection<?> params);

  /**
   * 执行 {@link Sql}， 并返回多行记录
   *
   * @param sql {@link Sql}
   * @return 结果集
   */
  List<Record> findRecords(Sql sql);

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
  <TKey, TView> Map<TKey, TView> findMap(
      Class<TView> viewClass, String keyField, String sql, Collection<?> params);

  /**
   * 执行 {@link Sql}，并返回 Map
   *
   * @param viewClass 结果集类型
   * @param keyField  返回 Map 的 Key 的字段，必须是 viewClass 中存在的字段
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return Map
   */
  <TKey, TView> Map<TKey, TView> findMap(Class<TView> viewClass, String keyField, Sql sql);

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
  <TView> List<TView> findTop(
      Class<TView> viewClass, int top, String sql, Collection<?> params);

  /**
   * 执行 {@link Sql}，返回指定行数的结果集
   *
   * @param viewClass 结果集类型
   * @param top       行数
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return 结果集
   */
  <TView> List<TView> findTop(Class<TView> viewClass, int top, Sql sql);

  /**
   * 执行SQL，返回指定行数的结果集
   *
   * @param top    行数
   * @param sql    SQL语句
   * @param params 参数
   * @return 结果集
   */
  List<Record> findTopRecords(int top, String sql, Collection<?> params);

  /**
   * 执行 {@link Sql}，返回指定行数的结果集
   *
   * @param top 行数
   * @param sql {@link Sql}
   * @return 结果集
   */
  List<Record> findTopRecords(int top, Sql sql);

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       SQL语句
   * @param params    参数
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  <TView> TView get(Class<TView> viewClass, String sql, Collection<?> params);

  /**
   * 执行 {@link Sql} ,并返回 1 行记录
   *
   * @param viewClass 结果集类型
   * @param sql       {@link Sql}
   * @param <TView>   结果集类型泛型
   * @return 记录
   */
  <TView> TView get(Class<TView> viewClass, Sql sql);

  /**
   * 执行 SQL ,并返回 1 行记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 记录
   */
  Record getRecord(String sql, Collection<?> params);

  /**
   * 执行 {@link Sql} ,并返回 1 行记录
   *
   * @param sql {@link Sql}
   * @return 记录
   */
  Record getRecord(Sql sql);


  /**
   * 根据主键获取记录
   *
   * @param viewClass 结果类型
   * @param id        主键
   * @param <TView>   实体类型
   * @return 记录
   */
  <TView> TView getById(Class<TView> viewClass, Object id);

  /**
   * 根据指定字段获取记录
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   * @param <TView>   实体类型
   * @return 记录
   */
  <TView> TView getByField(Class<TView> viewClass, String field, Object param);

  /**
   * 根据 {@link Cond} 条件获取记录
   *
   * @param viewClass 结果类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   实体类型
   * @return 记录
   */
  <TView> TView getByCond(Class<TView> viewClass, Cond cond);

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询记录
   *
   * @param viewClass 结果类型
   * @param object    包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param <TView>   实体类型
   * @return 记录
   */
  <TView> TView getByCriteria(Class<TView> viewClass, Object object);

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询记录
   *
   * @param viewClass     结果类型
   * @param object        包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param criteriaGroup 条件组名, 参考 {@link Criterion#group() @Criterion(group = CriteriaGroupClass.class)}
   * @param <TView>       实体类型
   * @return 记录
   */
  <TView> TView getByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup);

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param viewClass 结果类型
   * @param ids       主键ID集合
   * @param <TView>   实体类型
   * @return 实体集合
   */
  <TView> List<TView> findByIds(Class<TView> viewClass, Collection<?> ids);

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param param     参数
   * @param <TView>   实体类型
   * @return 实体集合
   */
  <TView> List<TView> findByField(Class<TView> viewClass, String field, Object param);

  /**
   * 根据字段查询实体集合
   *
   * @param viewClass 结果类型
   * @param field     字段名
   * @param params    参数集合
   * @param <TView>   实体类型
   * @return 实体集合
   */
  <TView> List<TView> findByField(Class<TView> viewClass, String field, Collection<?> params);

  /**
   * 根据 {@link Cond} 条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   实体类型
   * @return 实体集合
   */
  <TView> List<TView> findByCond(Class<TView> viewClass, Cond cond);

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询实体集合
   *
   * @param viewClass 结果类型
   * @param object    包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param <TView>   实体类型
   * @return 实体集合
   */
  <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object);

  /**
   * 根据 {@link Criteria @Criteria} 注解生成的条件查询实体集合
   *
   * @param viewClass     结果类型
   * @param object        包含 {@link Criteria @Criteria} 注解 Field 的对象
   * @param criteriaGroup 条件组名, 参考 {@link Criterion#group() @Criterion(group = CriteriaGroupClass.class)}
   * @param <TView>       实体类型
   * @return 实体集合
   */
  <TView> List<TView> findByCriteria(Class<TView> viewClass, Object object, Class<?> criteriaGroup);

  /**
   * 获取 SQL 的行数
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 行数
   */
  long count(String sql, Collection<?> params);

  /**
   * 获取 {@link Sql} 的行数
   *
   * @param sql {@link Sql}
   * @return 行数
   */
  long count(Sql sql);

  /**
   * 根据 {@link Cond} 条件获取查询的行数
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 行数
   */
  <TView> long countByCond(Class<TView> viewClass, Cond cond);

  /**
   * 根据传入的 {@link Sql} 判断是否存在符合条件的数据
   *
   * @param sql {@link Sql}
   * @return 查询结果行数大于 0 返回 {@code true}，否则返回 {@code false}
   */
  boolean exists(Sql sql);

  /**
   * 根据传入的SQL判断是否存在符合条件的数据
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 查询结果行数大于 0 返回 {@code true}，否则返回 {@code false}
   */
  boolean exists(String sql, Collection<?> params);

  /**
   * 判断实体（根据ID）是否存在
   *
   * @param modelClass 实体类型
   * @param entity     实体
   * @param <TModel>   实体类型泛型
   * @return 存在返回 {@code true}，不存在返回 {@code false}
   */
  <TModel> boolean exists(Class<TModel> modelClass, TModel entity);

  /**
   * 根据 {@link Cond} 条件判断是否存在符合条件的数据
   *
   * @param viewClass 查询的数据表、视图对应的Java View类型
   * @param cond      {@link Cond} 条件
   * @param <TView>   查询的数据表、视图对应的Java View类型
   * @return 查询结果行数大于 0 返回 {@code true}，否则返回 {@code false}
   */
  <TView> boolean existsByCond(Class<TView> viewClass, Cond cond);

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
  <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize);

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
  <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize);

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
  <TView> PageLite<TView> findPageLite(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable);

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       {@link Sql}
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link PageLite} 简单分页结果集
   */
  <TView> PageLite<TView> findPageLite(Class<TView> viewClass, Sql sql, IPageable pageable);

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
  PageLite<Record> findRecordsPageLite(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize);

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return {@link PageLite} 简单分页结果集
   */
  PageLite<Record> findRecordsPageLite(Sql sql, boolean enablePage, int currentPage, int pageSize);

  /**
   * 执行 SQL 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable {@link IPageable} 对象
   * @return {@link PageLite} 简单分页结果集
   */
  PageLite<Record> findRecordsPageLite(String sql, Collection<?> params, IPageable pageable);

  /**
   * 执行 {@link Sql} 语句，返回 {@link PageLite} 简单分页结果集
   *
   * @param sql      {@link Sql}
   * @param pageable {@link IPageable} 对象
   * @return {@link PageLite} 简单分页结果集
   */
  PageLite<Record> findRecordsPageLite(Sql sql, IPageable pageable);

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
  <TView> Page<TView> findPage(
      Class<TView> viewClass,
      String sql,
      Collection<?> params,
      boolean enablePage,
      int currentPage,
      int pageSize);

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
  <TView> Page<TView> findPage(
      Class<TView> viewClass, Sql sql, boolean enablePage, int currentPage, int pageSize);

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
  <TView> Page<TView> findPage(
      Class<TView> viewClass, String sql, Collection<?> params, IPageable pageable);

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 分页结果集
   *
   * @param viewClass 返回的数据类型
   * @param sql       {@link Sql}
   * @param pageable  {@link IPageable} 对象
   * @param <TView>   结果类型泛型
   * @return {@link Page} 分页结果集
   */
  <TView> Page<TView> findPage(Class<TView> viewClass, Sql sql, IPageable pageable);

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
  Page<Record> findRecordsPage(
      String sql, Collection<?> params, boolean enablePage, int currentPage, int pageSize);

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 分页结果集
   *
   * @param sql         {@link Sql}
   * @param enablePage  是否启用分页
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @return {@link Page} 结果集
   */
  Page<Record> findRecordsPage(Sql sql, boolean enablePage, int currentPage, int pageSize);

  /**
   * 执行 SQL 语句，返回 {@link Page} 结果集
   *
   * @param sql      SQL语句
   * @param params   参数
   * @param pageable {@link IPageable} 对象
   * @return {@link Page} 结果集
   */
  Page<Record> findRecordsPage(String sql, Collection<?> params, IPageable pageable);

  /**
   * 执行 {@link Sql} 语句，返回 {@link Page} 结果集
   *
   * @param sql      {@link Sql} 对象
   * @param pageable {@link IPageable} 对象
   * @return {@link Page} 结果集
   */
  Page<Record> findRecordsPage(Sql sql, IPageable pageable);
}
