package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 仓储基类，提供实体的 CRUD 操作.
 * <p>继承 {@link Query} 的所有查询能力，额外提供 {@code TModel} 类型的
 * 创建、更新、删除、根据主键查询等快捷方法.</p>
 *
 * @param <TModel> 实体类型
 * @see Query
 * @author tanqimin
 */
@SuppressWarnings("unused")
public class Repository<TModel> extends Query {

  protected final Class<TModel> modelClass;

  /**
   * 构造方法，通过泛型参数自动推断实体类型.
   *
   * @param dbTemplate {@link DBTemplate} 实例
   */
  public Repository(DBTemplate dbTemplate) {
    super(dbTemplate);
    this.modelClass = ReflectUtil.getGenericActualTypeArguments(this.getClass());
  }

  /**
   * 根据主键获取记录.
   *
   * @param id 主键值
   * @return 实体对象，不存在时返回 {@code null}
   */
  public TModel getById(Object id) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().getById(modelClass, id);
    }
  }

  /**
   * 根据指定字段值获取记录.
   *
   * @param field 字段名
   * @param param 字段值
   * @return 实体对象，不存在时返回 {@code null}
   */
  public TModel getByField(String field, Object param) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().getByField(modelClass, field, param);
    }
  }

  /**
   * 根据条件获取记录.
   *
   * @param cond 查询条件
   * @return 实体对象，不存在时返回 {@code null}
   */
  protected TModel getByCond(Cond cond) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().getByCond(modelClass, cond);
    }
  }

  /**
   * 根据 {@code @Criterion} 注解生成的条件查询记录.
   *
   * @param object 包含 {@code @Criterion} 注解字段的对象
   * @return 实体对象，不存在时返回 {@code null}
   */
  public TModel getByCondition(Object object) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().getByCriteria(modelClass, object);
    }
  }

  /**
   * 根据 SQL 查询单条记录.
   *
   * @param sql    SQL 语句
   * @param params 查询参数
   * @return 实体对象，不存在时返回 {@code null}
   */
  public TModel get(String sql, Collection<?> params) {
    return super.get(this.modelClass, new Sql(sql, params));
  }

  /**
   * 根据 SQL 查询单条记录.
   *
   * @param sql {@link Sql} 构建器对象
   * @return 实体对象，不存在时返回 {@code null}
   */
  public TModel get(Sql sql) {
    return super.get(this.modelClass, sql);
  }

  /**
   * 查询实体集合.
   *
   * @param sql    SQL 语句
   * @param params 查询参数
   * @return 实体列表
   */
  public List<TModel> find(String sql, Collection<?> params) {
    return super.find(modelClass, new Sql(sql, params));
  }

  /**
   * 查询实体集合.
   *
   * @param sql {@link Sql} 构建器对象
   * @return 实体列表
   */
  public List<TModel> find(Sql sql) {
    return super.find(modelClass, sql);
  }

  /**
   * 查询实体集合并返回以主键为键的 Map.
   *
   * @param <TKey> Map 键类型
   * @param sql    SQL 语句
   * @param params 查询参数
   * @return 以主键值为键的实体映射
   */
  public <TKey> Map<TKey, TModel> findMap(String sql, Collection<?> params) {
    final String fieldName = Metadata.classMeta(modelClass).getPrimaryKey().getFieldVisitor().getName();
    return findMap(modelClass, fieldName, sql, params);
  }

  /**
   * 查询实体集合并返回以主键为键的 Map.
   *
   * @param <TKey> Map 键类型
   * @param sql    {@link Sql} 构建器对象
   * @return 以主键值为键的实体映射
   */
  public <TKey> Map<TKey, TModel> findMap(Sql sql) {
    return this.findMap(sql.toString(), sql.getParams());
  }

  /**
   * 根据字段值查询实体集合.
   *
   * @param field 字段名
   * @param param 字段值
   * @return 实体列表
   */
  public List<TModel> findByField(String field, Object param) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findByField(modelClass, field, param);
    }
  }

  /**
   * 根据字段值集合查询实体集合.
   *
   * @param field  字段名
   * @param params 字段值集合
   * @return 实体列表
   */
  public List<TModel> findByField(String field, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findByField(modelClass, field, params);
    }
  }

  /**
   * 根据条件查询实体集合.
   *
   * @param cond 查询条件
   * @return 实体列表
   */
  protected List<TModel> findByCond(Cond cond) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findByCond(modelClass, cond);
    }
  }

  /**
   * 根据 {@code @Criterion} 注解生成的条件查询实体集合.
   *
   * @param object 包含 {@code @Criterion} 注解字段的对象
   * @return 实体列表
   */
  public List<TModel> findByCondition(Object object) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findByCriteria(modelClass, object);
    }
  }

  /**
   * 根据多个主键 ID 查询实体集合.
   *
   * @param ids 主键 ID 集合
   * @return 实体列表
   */
  public List<TModel> findByIds(Collection<?> ids) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().findByIds(modelClass, ids);
    }
  }

  /**
   * 根据条件统计行数.
   *
   * @param cond 查询条件
   * @return 符合条件的行数
   */
  public long countByCond(Cond cond) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().countByCond(modelClass, cond);
    }
  }

  /**
   * 判断实体（根据主键）是否存在.
   *
   * @param entity 实体对象
   * @return 存在返回 {@code true}，否则返回 {@code false}
   */
  public boolean exists(TModel entity) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().exists(modelClass, entity);
    }
  }

  /**
   * 根据条件判断是否存在记录.
   *
   * @param cond 查询条件
   * @return 符合条件的记录数大于 0 返回 {@code true}，否则返回 {@code false}
   */
  public boolean existsByCond(Cond cond) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().existsByCond(modelClass, cond);
    }
  }

  /**
   * 执行 SQL 语句.
   *
   * @param sql {@link Sql} 构建器对象
   * @return 影响行数
   */
  public int execute(Sql sql) {
    return this.execute(sql.toString(), sql.getParams());
  }

  /**
   * 执行 SQL 语句，指定超时时间.
   *
   * @param sql          {@link Sql} 构建器对象
   * @param queryTimeout 查询超时时间（秒）
   * @return 影响行数
   */
  public int execute(Sql sql, int queryTimeout) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().execute(sql, queryTimeout);
    }
  }

  /**
   * 批量执行多个 SQL 语句.
   *
   * @param sqlList SQL 语句集合
   * @return 每条 SQL 影响的行数数组
   */
  public int[] execute(List<Sql> sqlList) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().execute(sqlList);
    }
  }

  /**
   * 批量执行多个 SQL 语句，指定超时时间.
   *
   * @param sqlList      SQL 语句集合
   * @param queryTimeout 查询超时时间（秒）
   * @return 每条 SQL 影响的行数数组
   */
  public int[] execute(List<Sql> sqlList, int queryTimeout) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().execute(sqlList, queryTimeout);
    }
  }

  /**
   * 执行 SQL 语句.
   *
   * @param sql    SQL 语句
   * @param params 参数
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().execute(sql, params);
    }
  }

  /**
   * 执行 SQL 语句，指定超时时间.
   *
   * @param sql          SQL 语句
   * @param params       参数
   * @param queryTimeout 查询超时时间（秒）
   * @return 影响行数
   */
  public int execute(String sql, Collection<?> params, int queryTimeout) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().execute(sql, params, queryTimeout);
    }
  }

  /**
   * 创建实体.
   *
   * @param entity 实体对象
   * @return 影响行数
   */
  public int create(TModel entity) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().create(modelClass, entity);
    }
  }

  /**
   * 批量创建实体.
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int create(Collection<TModel> entities) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().create(modelClass, entities);
    }
  }

  /**
   * 更新实体.
   *
   * @param entity 实体对象
   * @return 影响行数
   */
  public int update(TModel entity) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().update(modelClass, entity);
    }
  }

  /**
   * 更新实体的指定列.
   *
   * @param entity  实体对象
   * @param columns 需要更新的列名数组
   * @return 影响行数
   */
  public int update(TModel entity, String[] columns) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().update(modelClass, entity, columns);
    }
  }

  /**
   * 更新实体，忽略值为 {@code null} 的属性.
   *
   * @param entity 实体对象
   * @return 影响行数
   */
  public int updateIgnoreNull(TModel entity) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().updateIgnoreNull(modelClass, entity);
    }
  }

  /**
   * 批量更新实体.
   *
   * @param entities 实体集合
   * @param columns  需要更新的列名数组
   * @return 影响行数
   */
  public int update(Collection<TModel> entities, String[] columns) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().update(modelClass, entities, columns);
    }
  }

  /**
   * 批量更新实体的所有列.
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int update(Collection<TModel> entities) {
    return this.update(entities, null);
  }

  /**
   * 如果记录存在则更新，不存在则创建.
   *
   * @param entity 实体对象
   * @return 影响行数
   */
  public int createOrUpdate(TModel entity) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().createOrUpdate(modelClass, entity);
    }
  }

  /**
   * 删除实体.
   *
   * @param entity 实体对象
   * @return 影响行数
   */
  public int delete(TModel entity) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().delete(modelClass, entity);
    }
  }

  /**
   * 批量删除实体.
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int delete(Collection<TModel> entities) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().delete(modelClass, entities);
    }
  }

  /**
   * 根据 ID 删除记录.
   *
   * @param id 主键值
   * @return 影响行数
   */
  public int deleteById(Object id) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().deleteById(modelClass, id);
    }
  }

  /**
   * 根据条件删除记录.
   *
   * @param cond 查询条件
   * @return 影响行数
   */
  protected int deleteByCond(Cond cond) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().deleteByCond(modelClass, cond);
    }
  }

  /**
   * 根据多个 ID 批量删除记录.
   *
   * @param ids 主键 ID 集合
   * @return 影响行数
   */
  public int deleteByIds(Collection<?> ids) {
    try (Database database = this.dbTemplate.createDatabase()) {
      return database.createOrm().deleteByIds(modelClass, ids);
    }
  }

  /**
   * 生成 UUID 主键值.
   *
   * @return UUID 字符串
   */
  public String uuid() {
    return this.dbTemplate.getPkGenerator().nextUUID();
  }

  /**
   * 生成 Snowflake 分布式 ID.
   *
   * @return Snowflake ID
   */
  public long snowFlakeId() {
    return this.dbTemplate.getPkGenerator().nextSnowFakeId();
  }
}
