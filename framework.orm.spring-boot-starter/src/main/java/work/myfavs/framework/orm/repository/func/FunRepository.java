package work.myfavs.framework.orm.repository.func;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import work.myfavs.framework.orm.DB;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;

@SuppressWarnings("unchecked")
public class FunRepository<TModel> extends FuncQuery {

  protected Class<TModel> modelClass;

  public FunRepository(DBTemplate dbTemplate) {
    super(dbTemplate);
    this.modelClass = (Class<TModel>) ((ParameterizedType) this.getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  /**
   * 根据主键获取记录
   *
   * @param id 主键
   * @return 记录
   */
  public Optional<TModel> getById(Object id) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().getById(modelClass, id);
    }
  }

  /**
   * 根据指定字段获取记录
   *
   * @param field 字段名
   * @param param 参数
   * @return 记录
   */
  public Optional<TModel> getByField(String field,
      Object param) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().getByField(modelClass, field, param);
    }
  }

  /**
   * 根据条件获取记录
   *
   * @param cond 条件
   * @return 记录
   */
  protected Optional<TModel> getByCond(Cond cond) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().getByCond(modelClass, cond);
    }
  }

  /**
   * 根据@Condition注解生成的条件查询记录
   *
   * @param object 包含@Condition注解Field的对象
   * @return 记录
   */
  public Optional<TModel> getByCondition(Object object) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().getByCondition(modelClass, object);
    }
  }

  /**
   * 根据SQL获取记录
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 记录
   */
  public Optional<TModel> get(String sql,
      Collection params) {

    return super.get(this.modelClass, sql, params);
  }

  /**
   * 根据SQL获取记录
   *
   * @param sql SQL
   * @return 记录
   */
  public Optional<TModel> get(Sql sql) {

    return super.get(this.modelClass, sql);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql    SQL语句
   * @param params 参数
   * @return 实体集合
   */
  public Stream<TModel> find(String sql,
      Collection params) {

    return super.find(modelClass, sql, params);
  }

  /**
   * 根据SQL查询实体集合
   *
   * @param sql SQL
   * @return 实体集合
   */
  public Stream<TModel> find(Sql sql) {

    return super.find(modelClass, sql);
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field 字段名
   * @param param 参数
   * @return 实体集合
   */
  public Stream<TModel> findByField(String field,
      Object param) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().findByField(modelClass, field, param);
    }
  }

  /**
   * 根据字段查询实体集合
   *
   * @param field  字段名
   * @param params 参数集合
   * @return 实体集合
   */
  public Stream<TModel> findByField(String field,
      Collection params) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().findByField(modelClass, field, params);
    }
  }

  /**
   * 根据条件查询实体集合
   *
   * @param cond 查询条件
   * @return 实体集合
   */
  protected Stream<TModel> findByCond(Cond cond) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().findByCond(modelClass, cond);
    }
  }

  /**
   * 根据@Condition注解生成的条件查询实体集合
   *
   * @param object 包含@Condition注解Field的对象
   * @return 实体集合
   */
  public Stream<TModel> findByCondition(Object object) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().findByCondition(modelClass, object);
    }
  }

  /**
   * 根据多个主键ID查询实体集合
   *
   * @param ids 主键ID集合
   * @return 实体集合
   */
  public Stream<TModel> findByIds(Collection ids) {

    try (DB conn = this.dbTemplate.open()) {
      return conn.func().findByIds(modelClass, ids);
    }
  }
}
