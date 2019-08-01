package work.myfavs.framework.orm.meta.dialect;


import java.util.List;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * 通用数据库方言接口
 * Created by tanqimin on 2015/11/3.
 */
public interface IDialect {

  /**
   * 获取数据库方言名称
   *
   * @return 数据库方言名称
   */
  String getDialectName();

  /**
   * 获取插入语句（带参数）
   *
   * @param clazz    实体类Class
   * @param entity   实体类对象
   * @param <TModel> 实体类类型
   *
   * @return Sql对象
   */
  <TModel> Sql insert(Class<TModel> clazz, TModel entity);

  /**
   * 获取插入语句（不带参数）
   *
   * @param clazz    实体类Class
   * @param <TModel> 实体类类型
   *
   * @return Sql对象
   */
  <TModel> Sql insert(Class<TModel> clazz);
//  /**
//   * 获取插入语句
//   *
//   * @param clazz    实体类Class
//   * @param columns  列元数据集合
//   * @param <TModel> 实体类类型
//   *
//   * @return Sql语句
//   */
//  <TModel> String create(Class<TModel> clazz, List<AttributeMeta> columns);

  /**
   * 获取所有记录语句
   *
   * @param clazz    实体类Class
   * @param <TModel> 实体类类型
   *
   * @return Sql对象
   */
  <TModel> Sql select(Class<TModel> clazz);

  /**
   * 获取返回行数语句
   *
   * @param sql    SQL语句
   * @param params 参数
   *
   * @return 返回行数语句
   */
  Sql count(String sql, List<Object> params);

  /**
   * 返回分页查询语句
   * 如果 pageSize = -1L，则不分页
   *
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param sql         SQL语句
   * @param params      参数
   *
   * @return Sql对象
   */
  Sql selectTop(int currentPage, int pageSize, String sql, List<Object> params);

  /**
   * 获取更新语句
   *
   * @param clazz    实体类Class
   * @param model    实体类对象
   * @param <TModel> 实体类类型
   *
   * @return Sql对象
   */
  <TModel> Sql update(Class<TModel> clazz, TModel model);

  /**
   * 获取删除实体语句
   *
   * @param clazz    实体类Class
   * @param <TModel> 实体类类型
   *
   * @return Sql对象
   */
  <TModel> Sql delete(Class<TModel> clazz);

}
