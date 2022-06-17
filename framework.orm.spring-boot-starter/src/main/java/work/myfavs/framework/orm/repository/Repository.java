package work.myfavs.framework.orm.repository;

import java.lang.reflect.ParameterizedType;
import java.sql.CallableStatement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import work.myfavs.framework.orm.DB;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Metadata;

/**
 * 仓储基类
 *
 * @param <TModel> 实体类
 */
@SuppressWarnings("unchecked")
public class Repository<TModel> extends Query {

  protected Class<TModel> modelClass;

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @SuppressWarnings("unchecked")
  public Repository(DBTemplate dbTemplate) {

    super(dbTemplate);
    this.modelClass =
        (Class<TModel>)
            ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
  }

  /**
   * 根据主键获取记录
   *
   * @param id 主键
   * @return 记录
   */
  public TModel getById(Object id) {

    return DB.conn(this.dbTemplate).getById(modelClass, id);
  }

  /**
   * 根据指定字段获取记录
   *
   * @param field 字段名
   * @param param 参数
   * @return 记录
   */
  public TModel getByField(String field, Object param) {

    return DB.conn(this.dbTemplate).getByField(modelClass, field, param);
  }

  /**
   * 根据条件获取记录
   *
   * @param cond 条件
   * @return 记录
   */
  protected TModel getByCond(Cond cond) {

    return DB.conn(this.dbTemplate).getByCond(modelClass, cond);
  }

  /**
   * 根据@Condition注解生成的条件查询记录
   *
   * @param object 包含@Condition注解Field的对象
   * @return 记录
   */
  public TModel getByCondition(Object object) {

    return DB.conn(this.dbTemplate).getByCondition(modelClass, object);
  }

  /**
   * 根据SQL获取记录
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 记录
   */
  public TModel get(String sql, Collection params) {

    return super.get(this.modelClass, sql, params);
  }

  /**
   * 根据SQL获取记录
   *
   * @param sql SQL
   * @return 记录
   */
  public TModel get(Sql sql) {

    return super.get(this.modelClass, sql);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 实体集合
   */
  public List<TModel> find(String sql, Collection params) {

    return super.find(modelClass, sql, params);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql SQL
   * @return 实体集合
   */
  public List<TModel> find(Sql sql) {

    return super.find(modelClass, sql);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql SQL
   * @param params 参数
   * @return Map，Key为主键值， Value为实体对象
   */
  public <TKey> Map<TKey, TModel> findMap(String sql, Collection params) {
    final String fieldName = Metadata.get(modelClass).getPrimaryKey().getFieldName();
    return findMap(modelClass, fieldName, sql, params);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql SQL
   * @return Map，Key为主键值， Value为实体对象
   */
  public <TKey> Map<TKey, TModel> findMap(Sql sql) {
    return this.findMap(sql.getSqlString(), sql.getParams());
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field 字段名
   * @param param 参数
   * @return 实体集合
   */
  public List<TModel> findByField(String field, Object param) {

    return DB.conn(this.dbTemplate).findByField(modelClass, field, param);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field 字段名
   * @param params 参数集合
   * @return 实体集合
   */
  public List<TModel> findByField(String field, Collection params) {

    return DB.conn(this.dbTemplate).findByField(modelClass, field, params);
  }

  /**
   * 根据条件查询实体集合
   *
   * @param cond 查询条件
   * @return 实体集合
   */
  protected List<TModel> findByCond(Cond cond) {

    return DB.conn(this.dbTemplate).findByCond(modelClass, cond);
  }

  /**
   * 根据@Condition注解生成的条件查询实体集合
   *
   * @param object 包含@Condition注解Field的对象
   * @return 实体集合
   */
  public List<TModel> findByCondition(Object object) {

    return DB.conn(this.dbTemplate).findByCondition(modelClass, object);
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param ids 主键ID集合
   * @return 实体集合
   */
  public List<TModel> findByIds(Collection ids) {

    return DB.conn(this.dbTemplate).findByIds(modelClass, ids);
  }

  /**
   * 根据条件获取查询的行数
   *
   * @param cond 条件
   * @return 行数
   */
  public long countByCond(Cond cond) {

    return DB.conn(this.dbTemplate).countByCond(modelClass, cond);
  }

  /**
   * 判断实体（根据ID）是否存在
   *
   * @param entity 实体
   * @return 存在返回true，不存在返回false
   */
  public boolean exists(TModel entity) {

    return DB.conn(this.dbTemplate).exists(modelClass, entity);
  }

  /**
   * 根据条件判断是否存在符合条件的数据
   *
   * @param cond 条件
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public boolean existsByCond(Cond cond) {

    return DB.conn(this.dbTemplate).existsByCond(modelClass, cond);
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   * @return 影响行数
   */
  public int execute(Sql sql) {

    return this.execute(sql.getSqlString(), sql.getParams());
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   * @param queryTimeout 超时时间
   * @return 影响行数
   */
  public int execute(Sql sql, int queryTimeout) {

    return DB.conn(this.dbTemplate).execute(sql, queryTimeout);
  }

  /**
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList) {

    return DB.conn(this.dbTemplate).execute(sqlList);
  }

  /**
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   * @param queryTimeout 超时时间
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList, int queryTimeout) {

    return DB.conn(this.dbTemplate).execute(sqlList, queryTimeout);
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL语句
   * @param params 参数
   * @return 影响行数
   */
  public int execute(String sql, Collection params) {

    return DB.conn(this.dbTemplate).execute(sql, params);
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL语句
   * @param params 参数
   * @param queryTimeout 超时时间
   * @return 影响行数
   */
  public int execute(String sql, Collection params, int queryTimeout) {

    return DB.conn(this.dbTemplate).execute(sql, params, queryTimeout);
  }

  /**
   * 调用存储过程
   *
   * @param sql 调用存储过程语句，如：{ call proc_name(?,?,?)}
   * @param func func
   * @param <TResult> 结果
   * @return TResult
   */
  public <TResult> TResult call(String sql, Function<CallableStatement, TResult> func) {
    return DB.conn(this.dbTemplate).call(sql, func);
  }

  /**
   * 调用存储过程
   *
   * @param sql 调用存储过程语句，如：{ call proc_name(?,?,?)}
   * @param func func
   * @param queryTimeout 超时时间
   * @param <TResult> 结果
   * @return TResult
   */
  public <TResult> TResult call(
      String sql, Function<CallableStatement, TResult> func, int queryTimeout) {
    return DB.conn(this.dbTemplate).call(sql, func, queryTimeout);
  }

  /**
   * 创建实体
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int create(TModel entity) {

    return DB.conn(this.dbTemplate).create(modelClass, entity);
  }

  /**
   * 批量创建实体
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int create(Collection<TModel> entities) {

    return DB.conn(this.dbTemplate).create(modelClass, entities);
  }

  /**
   * 更新实体
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int update(TModel entity) {

    return DB.conn(this.dbTemplate).update(modelClass, entity);
  }

  /**
   * 更新实体
   *
   * @param entity 实体
   * @param columns 需要更新的列
   * @return 影响行数
   */
  public int update(TModel entity, String[] columns) {

    return DB.conn(this.dbTemplate).update(modelClass, entity, columns);
  }

  /**
   * 更新实体，忽略Null属性的字段
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int updateIgnoreNull(TModel entity) {

    return DB.conn(this.dbTemplate).updateIgnoreNull(modelClass, entity);
  }

  /**
   * 更新实体
   *
   * @param entities 实体集合
   * @param columns 需要更新的列
   * @return 影响行数
   */
  public int update(Collection<TModel> entities, String[] columns) {

    return DB.conn(this.dbTemplate).update(modelClass, entities, columns);
  }

  /**
   * 更新实体
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int update(Collection<TModel> entities) {

    return this.update(entities, null);
  }

  /**
   * 如果记录存在更新，不存在则创建
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int createOrUpdate(TModel entity) {
    return DB.conn(this.dbTemplate).createOrUpdate(modelClass, entity);
  }

  /**
   * 删除记录
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int delete(TModel entity) {

    return DB.conn(this.dbTemplate).delete(modelClass, entity);
  }

  /**
   * 批量删除记录
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int delete(Collection<TModel> entities) {

    return DB.conn(this.dbTemplate).delete(modelClass, entities);
  }

  /**
   * 根据ID删除记录
   *
   * @param id ID值
   * @return 影响行数
   */
  public int deleteById(Object id) {

    return DB.conn(this.dbTemplate).deleteById(modelClass, id);
  }

  /**
   * 根据条件删除记录
   *
   * @param cond 条件值
   * @return 影响行数
   */
  protected int deleteByCond(Cond cond) {

    return DB.conn(this.dbTemplate).deleteByCond(modelClass, cond);
  }

  /**
   * 根据ID集合删除记录
   *
   * @param ids ID集合
   * @return 影响行数
   */
  public int deleteByIds(Collection ids) {

    return DB.conn(this.dbTemplate).deleteByIds(modelClass, ids);
  }

  /**
   * 创建一个UUID值
   *
   * @return UUID
   */
  public String uuid() {
    return this.dbTemplate.getPkGenerator().nextUUID();
  }

  /**
   * 创建一个雪花值
   *
   * @return 雪花值
   */
  public long snowFlakeId() {
    return this.dbTemplate.getPkGenerator().nextSnowFakeId();
  }
}
