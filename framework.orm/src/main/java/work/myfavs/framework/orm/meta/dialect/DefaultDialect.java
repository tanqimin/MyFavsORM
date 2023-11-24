package work.myfavs.framework.orm.meta.dialect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.PagerUtils;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.SqlCache.Opt;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DruidUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 默认数据库方言实现
 *
 * @author tanqimin
 */
public abstract class DefaultDialect extends AbstractDialect implements IDialect {
  protected int maxPageSize;

  public DefaultDialect() {
    this(-1);
  }

  public DefaultDialect(int maxPageSize) {
    this.maxPageSize = maxPageSize;
  }

  protected static <TModel> String getTableName(Class<TModel> clazz) {
    return TableAlias.getOpt().orElse(Metadata.classMeta(clazz).getTableName());
  }

  @Override
  public <TModel> Sql insert(Class<TModel> clazz, TModel model) {

    Sql sql = insert(clazz);

    ClassMeta                               classMeta        = Metadata.classMeta(clazz);
    Attribute                               primaryKey       = classMeta.getPrimaryKey();
    Map<String /* columnName */, Attribute> updateAttributes = classMeta.getUpdateAttributes();

    if (classMeta.getStrategy() != GenerationType.IDENTITY) {
      sql.getParams().add(primaryKey.getFieldVisitor().getValue(model));
    }

    for (Map.Entry<String, Attribute> entry : updateAttributes.entrySet()) {
      sql.getParams().add(entry.getValue().getFieldVisitor().getValue(model));
    }

    return sql;
  }

  @Override
  public <TModel> Sql insert(Class<TModel> clazz) {

    return SqlCache.computeIfAbsent(
        clazz,
        Opt.INSERT,
        (key) -> {
          ClassMeta                               classMeta        = Metadata.entityMeta(clazz);
          String                                  tableName        = TableAlias.getOpt().orElse(classMeta.getTableName());
          Attribute                               primaryKey       = classMeta.checkPrimaryKey();
          Map<String /* columnName */, Attribute> updateAttributes = classMeta.getUpdateAttributes();

          Sql insertSql = new Sql(StrUtil.format("INSERT INTO {} (", tableName));
          Sql valuesSql = new Sql(" VALUES (");
          if (classMeta.getStrategy() != GenerationType.IDENTITY) {
            insertSql.append(primaryKey.getColumnName().concat(","));
            valuesSql.append("?,");
          }

          if (!updateAttributes.isEmpty()) {
            updateAttributes.forEach(
                (col, attr) -> {
                  insertSql.append(attr.getColumnName().concat(","));
                  valuesSql.append("?,");
                });

            // 自动加入逻辑删除字段
            if (classMeta.getLogicDelete() != null) {
              insertSql.append(classMeta.getLogicDelete().getColumnName().concat(","));
              valuesSql.append("0,");
            }
            insertSql.deleteLastChar(",");
            valuesSql.deleteLastChar(",");
          }

          insertSql.append(")");
          valuesSql.append(")");

          return new Sql().append(insertSql).append(valuesSql);
        });
  }

  @Override
  public <TModel> Sql update(Class<TModel> clazz, TModel model, boolean ignoreNullValue) {

    ClassMeta                               classMeta;
    String                                  tableName;
    Attribute                               primaryKey;
    Map<String /* columnName */, Attribute> updateAttributes;

    Sql sql;

    classMeta = Metadata.classMeta(clazz);
    tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    primaryKey = classMeta.checkPrimaryKey();
    updateAttributes = classMeta.getUpdateAttributes();

    sql = Sql.Update(tableName).append(" SET");

    if (!updateAttributes.isEmpty()) {
      updateAttributes.forEach(
          (col, attr) -> {
            final Object fieldValue = attr.getFieldVisitor().getValue(model);
            // 忽略属性为null的字段生成
            if (ignoreNullValue && Objects.isNull(fieldValue)) {
              return;
            }
            sql.append(StrUtil.format(" {} = ?,", attr.getColumnName()), fieldValue);
          });

      sql.deleteLastChar(",");
    }

    sql.append(
        StrUtil.format(" WHERE {} = ?", primaryKey.getColumnName()),
        primaryKey.getFieldVisitor().getValue(model)
    );

    return sql;
  }

  @Override
  public <TModel> Sql delete(Class<TModel> clazz) {

    ClassMeta classMeta = Metadata.entityMeta(clazz);
    String    tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    return Sql.Delete(tableName);
  }

  @Override
  public Sql count(String sql, Collection<?> params) {

    return new Sql(PagerUtils.count(sql, DruidUtil.convert(dbType())), params);
  }

  @Override
  public <TModel> Sql count(Class<TModel> clazz) {

    return new Sql(StrUtil.format("SELECT COUNT(*) FROM {}", getTableName(clazz)));
  }

  @Override
  public <TModel> Sql select(Class<TModel> clazz) {

    return new Sql(StrUtil.format("SELECT * FROM {}", getTableName(clazz)));
  }

  @Override
  public Sql selectPage(boolean enablePage, String sql, Collection<?> params, int currentPage, int pageSize) {
    if (!enablePage) return new Sql(sql, params);
    if (currentPage < 1)
      throw new DBException("当前页码 (currentPage) 参数必须大于等于 1");

    if (pageSize < 1)
      throw new DBException("每页记录数 (pageSize) 参数必须大于等于 1");

    if (maxPageSize > 0L && pageSize > maxPageSize)
      throw new DBException("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize);

    return selectPage(sql, params, currentPage, pageSize);
  }

  protected abstract Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize);
}
