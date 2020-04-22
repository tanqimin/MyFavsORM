package work.myfavs.framework.orm.repository;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import work.myfavs.framework.orm.DB;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.repository.func.FunRepository;

/**
 * 仓储基类
 *
 * @param <TModel> 实体类
 */
@SuppressWarnings("unchecked")
public class Repository<TModel>
    extends Query {

  protected Class<TModel> modelClass;
  protected FunRepository<TModel> funRepository;

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @SuppressWarnings("unchecked")
  public Repository(DBTemplate dbTemplate) {

    super(dbTemplate);
    this.modelClass = (Class<TModel>) ((ParameterizedType) this.getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  @Override
  public FunRepository<TModel> func() {
    if (funRepository == null) {
      funRepository = new FunRepository<>(dbTemplate);
    }
    return funRepository;
  }

  /**
   * 根据主键获取记录
   *
   * @param id 主键
   * @return 记录
   */
  public TModel getById(Object id) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.getById(modelClass, id);
    }
  }

  /**
   * 根据指定字段获取记录
   *
   * @param field 字段名
   * @param param 参数
   * @return 记录
   */
  public TModel getByField(String field,
      Object param) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.getByField(modelClass, field, param);
    }
  }

  /**
   * 根据条件获取记录
   *
   * @param cond 条件
   * @return 记录
   */
  protected TModel getByCond(Cond cond) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.getByCond(modelClass, cond);
    }
  }

  /**
   * 根据@Condition注解生成的条件查询记录
   *
   * @param object 包含@Condition注解Field的对象
   * @return 记录
   */
  public TModel getByCondition(Object object) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.getByCondition(modelClass, object);
    }
  }

  /**
   * 根据SQL获取记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 记录
   */
  public TModel get(String sql,
      Collection params) {

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
   * @param sql    SQL语句
   * @param params 参数
   * @return 实体集合
   */
  public List<TModel> find(String sql,
      Collection params) {

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
  public Map<Object, TModel> findMap(String sql,
      Collection params) {
    final String fieldName = Metadata.get(modelClass).getPrimaryKey().getFieldName();
    return findMap(modelClass, fieldName, sql, params);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql SQL
   * @return Map，Key为主键值， Value为实体对象
   */
  public Map<Object, TModel> findMap(Sql sql) {
    return this.findMap(sql.getSqlString(), sql.getParams());
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field 字段名
   * @param param 参数
   * @return 实体集合
   */
  public List<TModel> findByField(String field,
      Object param) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.findByField(modelClass, field, param);
    }
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field  字段名
   * @param params 参数集合
   * @return 实体集合
   */
  public List<TModel> findByField(String field,
      Collection params) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.findByField(modelClass, field, params);
    }
  }

  /**
   * 根据条件查询实体集合
   *
   * @param cond 查询条件
   * @return 实体集合
   */
  protected List<TModel> findByCond(Cond cond) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.findByCond(modelClass, cond);
    }
  }

  /**
   * 根据@Condition注解生成的条件查询实体集合
   *
   * @param object 包含@Condition注解Field的对象
   * @return 实体集合
   */
  public List<TModel> findByCondition(Object object) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.findByCondition(modelClass, object);
    }
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param ids 主键ID集合
   * @return 实体集合
   */
  public List<TModel> findByIds(Collection ids) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.findByIds(modelClass, ids);
    }
  }

  /**
   * 根据条件获取查询的行数
   *
   * @param cond 条件
   * @return 行数
   */
  public long countByCond(Cond cond) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.countByCond(modelClass, cond);
    }
  }

  /**
   * 根据条件判断是否存在符合条件的数据
   *
   * @param cond 条件
   * @return 查询结果行数大于0返回true，否则返回false
   */
  public boolean existsByCond(Cond cond) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.existsByCond(modelClass, cond);
    }
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
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.execute(sqlList);
    }
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 影响行数
   */
  public int execute(String sql,
      Collection params) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.execute(sql, params);
    }
  }

  /**
   * 创建实体
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int create(TModel entity) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.create(modelClass, entity);
    }
  }

  /**
   * 批量创建实体
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int create(Collection<TModel> entities) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.create(modelClass, entities);
    }
  }

  /**
   * 更新实体
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int update(TModel entity) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.update(modelClass, entity);
    }
  }

  /**
   * 更新实体
   *
   * @param entity  实体
   * @param columns 需要更新的列
   * @return 影响行数
   */
  public int update(TModel entity,
      String[] columns) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.update(modelClass, entity, columns);
    }
  }

  /**
   * 更新实体，忽略Null属性的字段
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int updateIgnoreNull(TModel entity) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.updateIgnoreNull(modelClass, entity);
    }
  }

  /**
   * 更新实体
   *
   * @param entities 实体集合
   * @param columns  需要更新的列
   * @return 影响行数
   */
  public int update(Collection<TModel> entities,
      String[] columns) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.update(modelClass, entities, columns);
    }
  }

  /**
   * 更新实体
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int update(List<TModel> entities) {

    return this.update(entities, null);
  }

  /**
   * 删除记录
   *
   * @param entity 实体
   * @return 影响行数
   */
  public int delete(TModel entity) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.delete(modelClass, entity);
    }
  }

  /**
   * 批量删除记录
   *
   * @param entities 实体集合
   * @return 影响行数
   */
  public int delete(List<TModel> entities) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.delete(modelClass, entities);
    }
  }

  /**
   * 根据ID删除记录
   *
   * @param id ID值
   * @return 影响行数
   */
  public int deleteById(Object id) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.deleteById(modelClass, id);
    }
  }

  /**
   * 根据条件删除记录
   *
   * @param cond 条件值
   * @return 影响行数
   */
  protected int deleteByCond(Cond cond) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.deleteByCond(modelClass, cond);
    }
  }

  /**
   * 根据ID集合删除记录
   *
   * @param ids ID集合
   * @return 影响行数
   */
  public int deleteByIds(Collection ids) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.deleteByIds(modelClass, ids);
    }
  }

}
