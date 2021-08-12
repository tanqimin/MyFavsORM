package work.myfavs.framework.orm.meta.dialect;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.SqlCache.Opt;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.Attributes;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;

/**
 * 默认数据库方言实现
 *
 * @author tanqimin
 */
public abstract class DefaultDialect implements IDialect {

  private static final Logger log = LoggerFactory.getLogger(DefaultDialect.class);

  protected final Pattern P_SELECT = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);
  protected final Pattern P_ORDER =
      Pattern.compile("\\s+ORDER\\s+BY\\s+", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  protected final Pattern P_GROUP =
      Pattern.compile("\\s+GROUP\\s+BY\\s+", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  protected final Pattern P_HAVING =
      Pattern.compile("\\s+HAVING\\s+", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  protected final Pattern P_SELECT_SINGLE =
      Pattern.compile(
          "^\\s*SELECT\\s+((COUNT)\\([\\s\\S]*\\)\\s*,?)+((\\s*)|(\\s+FROM[\\s\\S]*))?$",
          Pattern.CASE_INSENSITIVE);

  protected static <TModel> String getTableName(Class<TModel> clazz) {
    return TableAlias.getOpt().orElse(Metadata.get(clazz).getTableName());
  }

  /**
   * 获取数据库方言名称
   *
   * @return 数据库方言名称
   */
  @Override
  public abstract String getDialectName();

  @Override
  public <TModel> Sql insert(Class<TModel> clazz, TModel model) {

    Sql sql = insert(clazz);

    ClassMeta classMeta = Metadata.get(clazz);
    Attribute primaryKey = classMeta.getPrimaryKey();
    Attributes updateAttributes = classMeta.getUpdateAttributes();

    if (classMeta.getStrategy() != GenerationType.IDENTITY) {
      sql.getParams().add(ReflectUtil.getFieldValue(model, primaryKey.getFieldName()));
    }

    if (updateAttributes.size() > 0) {
      updateAttributes.forEach(
          (col, attr) -> {
            sql.getParams().add(ReflectUtil.getFieldValue(model, attr.getFieldName()));
          });
    }

    return sql;
  }

  @Override
  public <TModel> Sql insert(Class<TModel> clazz) {

    return SqlCache.computeIfAbsent(
        clazz,
        Opt.INSERT,
        (key) -> {
          ClassMeta classMeta = Metadata.get(clazz);
          String tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
          Attribute primaryKey = classMeta.checkPrimaryKey();
          Attributes updateAttributes = classMeta.getUpdateAttributes();

          Sql insertSql = new Sql(StrUtil.format("INSERT INTO {} (", tableName));
          Sql valuesSql = new Sql(StrUtil.format(" VALUES ("));
          if (classMeta.getStrategy() != GenerationType.IDENTITY) {
            insertSql.append(StrUtil.format("{},", primaryKey.getColumnName()));
            valuesSql.append(StrUtil.format("?,"));
          }

          if (updateAttributes.size() > 0) {
            updateAttributes.forEach(
                (col, attr) -> {
                  insertSql.append(StrUtil.format("{},", attr.getColumnName()));
                  valuesSql.append("?,");
                });

            // 自动加入逻辑删除字段
            if (classMeta.needAppendLogicalDeleteField()) {
              insertSql.append(StrUtil.format("{},", classMeta.getLogicalDeleteField()));
              valuesSql.append(StrUtil.format("0,"));
            }
            insertSql.getSql().deleteCharAt(insertSql.getSqlString().lastIndexOf(","));
            valuesSql.getSql().deleteCharAt(valuesSql.getSqlString().lastIndexOf(","));
          }

          return insertSql.append(")").append(valuesSql).append(")");
        });
  }

  @Override
  public <TModel> Sql update(Class<TModel> clazz, TModel model, boolean ignoreNullValue) {

    ClassMeta classMeta;
    String tableName;
    Attribute primaryKey;
    Attributes updateAttributes;

    Sql sql;

    classMeta = Metadata.get(clazz);
    tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    primaryKey = classMeta.checkPrimaryKey();
    updateAttributes = classMeta.getUpdateAttributes();

    sql = Sql.Update(tableName).append(" SET");

    if (updateAttributes.size() > 0) {
      updateAttributes.forEach(
          (col, attr) -> {
            final Object fieldValue = ReflectUtil.getFieldValue(model, attr.getFieldName());
            // 忽略属性为null的字段生成
            if (fieldValue == null && ignoreNullValue) {
              return;
            }
            sql.append(StrUtil.format(" {} = ?,", attr.getColumnName()), fieldValue);
          });

      sql.getSql().deleteCharAt(sql.getSql().lastIndexOf(","));
    }
    sql.append(
        StrUtil.format(" WHERE {} = ?", primaryKey.getColumnName()),
        ReflectUtil.getFieldValue(model, primaryKey.getFieldName()));

    return sql;
  }

  @Override
  public <TModel> Sql delete(Class<TModel> clazz) {

    ClassMeta classMeta = Metadata.get(clazz);
    String tableName = TableAlias.getOpt().orElse(classMeta.getTableName());
    return Sql.Delete(tableName);
  }

  @Override
  public Sql count(String sql, Collection params) {

    Matcher om = P_ORDER.matcher(sql);
    if (om.find()) {
      sql = sql.substring(0, om.start());
    }

    return new Sql(StrUtil.format("SELECT COUNT(*) FROM ({}) count_alias", sql), params);
  }

  @Override
  public <TModel> Sql count(Class<TModel> clazz) {

    return new Sql(StrUtil.format("SELECT COUNT(*) FROM {}", getTableName(clazz)));
  }

  @Override
  public <TModel> Sql select(Class<TModel> clazz) {

    return new Sql(StrUtil.format("SELECT * FROM {}", getTableName(clazz)));
  }
}
