package work.myfavs.framework.orm.meta.dialect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.SqlCache.Opt;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DruidUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.*;

/**
 * 默认数据库方言实现
 *
 * @author tanqimin
 */
public abstract class DefaultDialect extends AbstractDialect implements IDialect {
  protected DBConfig dbConfig;

  public DefaultDialect(DBConfig dbConfig) {
    this.dbConfig = dbConfig;
  }

  protected static <TModel> String getTableName(Class<TModel> clazz) {
    return TableAlias.getOpt().orElse(Metadata.classMeta(clazz).getTableName());
  }


  @Override
  public String insert(ClassMeta entityMeta) {

    Class<?> clazz = entityMeta.getClazz();
    Opt      opt   = Opt.INSERT;
    return SqlCache.computeIfAbsent(
        clazz,
        opt,
        () -> {
          GenerationType                          strategy         = entityMeta.getStrategy();
          Attribute                               primaryKey       = entityMeta.checkPrimaryKey();
          Map<String /* columnName */, Attribute> updateAttributes = entityMeta.getUpdateAttributes();

          SQLInsertStatement insertStatement = new SQLInsertStatement();
          List<SQLExpr>      columns         = new ArrayList<>();
          List<SQLExpr>      values          = new ArrayList<>();
          //设置表名
          insertStatement.setTableSource(createTableSource(getTableName(clazz)));
          if (strategy != GenerationType.IDENTITY) {
            columns.add(createColumn(primaryKey.getColumnName()));
            values.add(createParam());
          }

          for (Map.Entry<String, Attribute> entry : updateAttributes.entrySet()) {
            columns.add(createColumn(entry.getValue().getColumnName()));
            values.add(createParam());
          }

          Attribute logicDelete = entityMeta.getLogicDelete();
          if (Objects.nonNull(logicDelete)) {
            columns.add(createColumn(logicDelete.getColumnName()));
            values.add(new SQLIntegerExpr(0));
          }

          insertStatement.getColumns().addAll(columns);
          insertStatement.setValues(new SQLInsertStatement.ValuesClause(values));

          return insertStatement.toUnformattedString();
        });
  }

  @Override
  public <TModel> Sql update(Class<TModel> clazz, TModel model, boolean ignoreNullValue) {


    Sql sql = new Sql();

    ClassMeta classMeta   = Metadata.classMeta(clazz);
    Attribute primaryKey  = classMeta.checkPrimaryKey();
    Attribute logicDelete = classMeta.getLogicDelete();

    Map<String /* columnName */, Attribute> updateAttributes = classMeta.getUpdateAttributes();

    SQLUpdateStatement updateStatement = new SQLUpdateStatement();
    updateStatement.setTableSource(createTableSource(getTableName(clazz)));

    for (Map.Entry<String, Attribute> entry : updateAttributes.entrySet()) {
      Object fieldValue = entry.getValue().getValue(model);
      if (ignoreNullValue && Objects.isNull(fieldValue)) continue;

      SQLUpdateSetItem sqlUpdateSetItem = createUpdateSetItem(entry.getValue().getColumnName());
      updateStatement.addItem(sqlUpdateSetItem);

      sql.getParams().add(fieldValue);
    }

    updateStatement.addWhere(createCondition(primaryKey, logicDelete));

    sql.append(updateStatement.toUnformattedString());
    sql.getParams().add(primaryKey.getValue(model));

    return sql;
  }

  private static SQLBinaryOpExpr createCondition(Attribute primaryKey, Attribute logicDelete) {
    SQLBinaryOpExpr condition = new SQLBinaryOpExpr(
        createColumn(primaryKey.getColumnName()),
        SQLBinaryOperator.Equality,
        createParam());

    if (Objects.nonNull(logicDelete)) {
      condition = new SQLBinaryOpExpr(
          condition,
          SQLBinaryOperator.BooleanAnd,
          new SQLBinaryOpExpr(
              createColumn(logicDelete.getColumnName()),
              SQLBinaryOperator.Equality,
              new SQLIntegerExpr(0)
          )
      );
    }
    return condition;
  }

  @Override
  public String update(ClassMeta classMeta, String[] columns) {

    Attribute primaryKey  = classMeta.checkPrimaryKey();
    Attribute logicDelete = classMeta.getLogicDelete();

    Collection<Attribute> updateAttributes = classMeta.getUpdateAttributes(columns);

    if (updateAttributes.isEmpty())
      throw new DBException("Could not match update attributes.");

    String tableName = getTableName(classMeta.getClazz());

    SQLUpdateStatement updateStatement = new SQLUpdateStatement();
    updateStatement.setTableSource(createTableSource(tableName));

    for (Attribute attr : updateAttributes) {
      updateStatement.addItem(createUpdateSetItem(attr.getColumnName()));
    }

    updateStatement.addWhere(createCondition(primaryKey, logicDelete));
    return updateStatement.toUnformattedString();
  }

  private static SQLExprTableSource createTableSource(String tableName) {
    return new SQLExprTableSource(tableName);
  }

  protected static SQLUpdateSetItem createUpdateSetItem(String columnName) {
    SQLUpdateSetItem sqlUpdateSetItem = new SQLUpdateSetItem();
    sqlUpdateSetItem.setColumn(createColumn(columnName));
    sqlUpdateSetItem.setValue(createParam());
    return sqlUpdateSetItem;
  }

  protected static SQLVariantRefExpr createParam() {
    return new SQLVariantRefExpr("?");
  }

  protected static SQLIdentifierExpr createColumn(String columnName) {
    return new SQLIdentifierExpr(columnName);
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

    long maxPageSize = this.dbConfig.getMaxPageSize();
    if (maxPageSize > 0L && pageSize > maxPageSize)
      throw new DBException("每页记录数不能超出系统设置的最大记录数 {}", maxPageSize);

    return selectPage(sql, params, currentPage, pageSize);
  }

  protected abstract Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize);
}
