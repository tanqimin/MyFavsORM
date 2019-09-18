package work.myfavs.framework.orm.repository;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * 仓储基类
 *
 * @param <TModel>
 */
@Slf4j
public class Repository<TModel>
    extends Query {

  protected Class<TModel> modelClass;

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @SuppressWarnings("unchecked")
  public Repository(DBTemplate dbTemplate) {

    super(dbTemplate);
    this.modelClass = (Class<TModel>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  /**
   * 根据主键获取记录
   *
   * @param id 主键
   *
   * @return 记录
   */
  public TModel getById(Object id) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.getById(modelClass, id);
    }
  }

  /**
   * 根据指定字段获取记录
   *
   * @param field 字段名
   * @param param 参数
   *
   * @return 记录
   */
  public TModel getByField(String field, Object param) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.getByField(modelClass, field, param);
    }
  }

  /**
   * 根据SQL获取记录
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 记录
   */
  public TModel get(String sql, List<Object> params) {

    return super.get(this.modelClass, sql, params);
  }

  /**
   * 根据SQL获取记录
   *
   * @param sql SQL
   *
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
   *
   * @return 实体集合
   */
  public List<TModel> find(String sql, List<Object> params) {

    return super.find(modelClass, sql, params);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql SQL
   *
   * @return 实体集合
   */
  public List<TModel> find(Sql sql) {

    return super.find(modelClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field 字段名
   * @param param 参数
   *
   * @return 实体集合
   */
  public List<TModel> findByField(String field, Object param) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.findByField(modelClass, field, param);
    }
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field  字段名
   * @param params 参数集合
   *
   * @return 实体集合
   */
  public List<TModel> findByField(String field, List<Object> params) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.findByField(modelClass, field, params);
    }
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param ids 主键ID集合
   *
   * @return 实体集合
   */
  public List<TModel> findByIds(List ids) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.findByIds(modelClass, ids);
    }
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql SQL
   *
   * @return 影响行数
   */
  public int execute(Sql sql) {

    return this.execute(sql.getSql().toString(), sql.getParams());
  }

  /**
   * 执行多个SQL语句
   *
   * @param sqlList SQL集合
   *
   * @return 返回多个影响行数
   */
  public int[] execute(List<Sql> sqlList) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.execute(sqlList);
    }
  }

  /**
   * 执行一个SQL语句
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 影响行数
   */
  public int execute(String sql, List<Object> params) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.execute(sql, params);
    }
  }

  /**
   * 创建实体
   *
   * @param entity 实体
   *
   * @return 影响行数
   */
  public int create(TModel entity) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.create(modelClass, entity);
    }
  }

  /**
   * 批量创建实体
   *
   * @param entities 实体集合
   *
   * @return 影响行数
   */
  public int create(Collection<TModel> entities) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.create(modelClass, entities);
    }
  }

  /**
   * 更新实体
   *
   * @param entity 实体
   *
   * @return 影响行数
   */
  public int update(TModel entity) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.update(modelClass, entity);
    }
  }

  /**
   * 更新实体
   *
   * @param entity  实体
   * @param columns 需要更新的列
   *
   * @return 影响行数
   */
  public int update(TModel entity, String[] columns) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.update(modelClass, entity, columns);
    }
  }

  /**
   * 更新实体
   *
   * @param entities 实体集合
   * @param columns  需要更新的列
   *
   * @return 影响行数
   */
  public int update(Collection<TModel> entities, String[] columns) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.update(modelClass, entities, columns);
    }
  }

  /**
   * 更新实体
   *
   * @param entities 实体集合
   *
   * @return 影响行数
   */
  public int update(List<TModel> entities) {

    return this.update(entities, null);
  }

  /**
   * 删除记录
   *
   * @param entity 实体
   *
   * @return 影响行数
   */
  public int delete(TModel entity) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.delete(modelClass, entity);
    }
  }

  /**
   * 批量删除记录
   *
   * @param entities 实体集合
   *
   * @return 影响行数
   */
  public int delete(List<TModel> entities) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.delete(modelClass, entities);
    }
  }

  /**
   * 根据ID删除记录
   *
   * @param id ID值
   *
   * @return 影响行数
   */
  public int deleteById(Object id) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.deleteById(modelClass, id);
    }
  }

  /**
   * 根据ID集合删除记录
   *
   * @param ids ID集合
   *
   * @return 影响行数
   */
  public int deleteByIds(Collection ids) {

    try (Database conn = this.dbTemplate.open()) {
      return conn.deleteByIds(modelClass, ids);
    }
  }

}
