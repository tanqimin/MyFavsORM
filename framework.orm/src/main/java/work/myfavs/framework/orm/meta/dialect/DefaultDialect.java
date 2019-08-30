package work.myfavs.framework.orm.meta.dialect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.AttributeMeta;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.ReflectUtil;
import work.myfavs.framework.orm.util.StringUtil;


/**
 * 默认数据库方言实现
 *
 * @author tanqimin
 */
@Slf4j
public abstract class DefaultDialect
    implements IDialect {

  protected final static Map<String, String> CLAUSE_CACHE        = new HashMap<>();
  protected final        Pattern             selectPattern       = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);
  protected final        Pattern             orderPattern        = Pattern.compile("\\s+ORDER\\s+BY\\s+",
                                                                                   Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  protected final        Pattern             groupPattern        = Pattern.compile("\\s+GROUP\\s+BY\\s+",
                                                                                   Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  protected final        Pattern             havingPattern       = Pattern.compile("\\s+HAVING\\s+",
                                                                                   Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  protected final        Pattern             selectSinglePattern = Pattern.compile(
      "^\\s*SELECT\\s+((COUNT)\\([\\s\\S]*\\)\\s*,?)+((\\s*)|(\\s+FROM[\\s\\S]*))?$", Pattern.CASE_INSENSITIVE);

  private static <TModel> String getTableName(Class<TModel> clazz) {

    return Metadata.get(clazz).getTableName();
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

    ClassMeta           classMeta;
    String              tableName;
    AttributeMeta       primaryKey;
    List<AttributeMeta> updateAttributes;

    Sql insertSql;
    Sql valuesSql;

    classMeta = Metadata.get(clazz);
    tableName = classMeta.getTableName();
    primaryKey = classMeta.getPrimaryKey();
    updateAttributes = classMeta.getUpdateAttributes();

    insertSql = new Sql(StringUtil.format("INSERT INTO {} (", tableName));
    valuesSql = new Sql(StringUtil.format(" VALUES ("));
    if (classMeta.getStrategy() != GenerationType.IDENTITY) {
      insertSql.append(StringUtil.format("{},", primaryKey.getColumnName()));
      valuesSql.append(StringUtil.format("?,"), ReflectUtil.getFieldValue(model, primaryKey.getFieldName()));
    }

    if (updateAttributes.size() > 0) {
      for (AttributeMeta attributeMeta : updateAttributes) {
        insertSql.append(StringUtil.format("{},", attributeMeta.getColumnName()));
        valuesSql.append(StringUtil.format("?,"), ReflectUtil.getFieldValue(model, attributeMeta.getFieldName()));
      }
      insertSql.getSql().deleteCharAt(insertSql.getSql().lastIndexOf(","));
      valuesSql.getSql().deleteCharAt(valuesSql.getSql().lastIndexOf(","));
    }

    return insertSql.append(")").append(valuesSql).append(")");
  }

  @Override
  public <TModel> Sql insert(Class<TModel> clazz) {

    ClassMeta           classMeta;
    String              tableName;
    AttributeMeta       primaryKey;
    List<AttributeMeta> updateAttributes;

    Sql insertSql;
    Sql valuesSql;

    classMeta = Metadata.get(clazz);
    tableName = classMeta.getTableName();
    primaryKey = classMeta.checkPrimaryKey();
    updateAttributes = classMeta.getUpdateAttributes();

    insertSql = new Sql(StringUtil.format("INSERT INTO {} (", tableName));
    valuesSql = new Sql(StringUtil.format(" VALUES ("));
    if (classMeta.getStrategy() != GenerationType.IDENTITY) {
      insertSql.append(StringUtil.format("{},", primaryKey.getColumnName()));
      valuesSql.append(StringUtil.format("?,"));
    }

    if (updateAttributes.size() > 0) {
      for (AttributeMeta attributeMeta : updateAttributes) {
        insertSql.append(StringUtil.format("{},", attributeMeta.getColumnName()));
        valuesSql.append(StringUtil.format("?,"));
      }
      insertSql.getSql().deleteCharAt(insertSql.getSql().lastIndexOf(","));
      valuesSql.getSql().deleteCharAt(valuesSql.getSql().lastIndexOf(","));
    }

    return insertSql.append(")").append(valuesSql).append(")");
  }

  @Override
  public <TModel> Sql update(Class<TModel> clazz, TModel model) {

    ClassMeta           classMeta;
    String              tableName;
    AttributeMeta       primaryKey;
    List<AttributeMeta> updateAttributes;

    Sql sql;

    classMeta = Metadata.get(clazz);
    tableName = classMeta.getTableName();
    primaryKey = classMeta.checkPrimaryKey();
    updateAttributes = classMeta.getUpdateAttributes();

    sql = Sql.Update(tableName).append(" SET");

    if (updateAttributes.size() > 0) {
      for (AttributeMeta attributeMeta : updateAttributes) {
        sql.append(StringUtil.format(" {} = ?,", attributeMeta.getColumnName()),
                   ReflectUtil.getFieldValue(model, attributeMeta.getFieldName()));
      }
      sql.getSql().deleteCharAt(sql.getSql().lastIndexOf(","));
    }
    sql.append(StringUtil.format(" WHERE {} = ?", primaryKey.getColumnName()), ReflectUtil.getFieldValue(model, primaryKey.getFieldName()));

    return sql;
  }

  @Override
  public <TModel> Sql delete(Class<TModel> clazz) {

    ClassMeta classMeta = Metadata.get(clazz);
    return Sql.Delete(classMeta.getTableName());
  }

  @Override
  public Sql count(String sql, List<Object> params) {

    Matcher om = orderPattern.matcher(sql);
    if (om.find()) {
      sql = sql.substring(0, om.start());
    }

    return new Sql(StringUtil.format("SELECT COUNT(1) FROM ({}) count_alias", sql), params);
  }

  @Override
  public <TModel> Sql select(Class<TModel> clazz) {

    return new Sql(StringUtil.format("SELECT * FROM {}", getTableName(clazz)));
  }

}
