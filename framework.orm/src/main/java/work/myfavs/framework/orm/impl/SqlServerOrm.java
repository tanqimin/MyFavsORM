package work.myfavs.framework.orm.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.DruidUtil;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SqlServerOrm extends AbstractOrm {

  private static final String COL_ROW_NUM = "_rn";
  private static final String TABLE_ALIAS = "_paginate";

  public SqlServerOrm(Database database) {
    super(database);
  }

  @Override
  protected String dbType() {
    return DbType.SQL_SERVER;
  }

  @Override
  public <TModel> int update(Class<TModel> modelClass, Collection<TModel> entities, String[] columns) {

    ClassMeta             classMeta = Metadata.classMeta(modelClass);
    Attribute             pk        = classMeta.checkPrimaryKey();
    Collection<Attribute> updAttrs  = classMeta.getUpdateAttributes(columns);

    String sql = this.update(classMeta, columns);

    Collection<Collection<?>> paramsList;
    Collection<Object>        params;

    paramsList = new ArrayList<>();

    for (TModel entity : entities) {
      params = new ArrayList<>();

      for (Attribute attributeMeta : updAttrs) {
        params.add(attributeMeta.getFieldVisitor().getValue(entity));
      }

      params.add(pk.getFieldVisitor().getValue(entity));
      paramsList.add(params);
    }

    try (Query query = this.database.createQuery(sql)) {
      for (Collection<?> batchParams : paramsList) {
        query.addParameters(batchParams).addBatch();
      }
      return query.executeBatch().length;
    }
  }

  @Override
  public <TModel> int deleteByIds(Class<TModel> modelClass, Collection<?> ids) {
    if (CollectionUtil.isEmpty(ids)) {
      return 0;
    }

    final ClassMeta classMeta    = Metadata.classMeta(modelClass);
    final Attribute primaryKey   = classMeta.checkPrimaryKey();
    final String    pkColumnName = primaryKey.getColumnName();

    int                     ret         = 0;
    List<? extends List<?>> splitParams = CollectionUtil.split(ids, Constant.MAX_PARAM_SIZE_FOR_MSSQL);
    for (List<?> splitParam : splitParams) {
      Cond deleteCond = Cond.in(pkColumnName, splitParam, false);
      ret += deleteByCond(classMeta, deleteCond);
    }
    return ret;
  }

  protected String update(ClassMeta classMeta, String[] columns) {

    Attribute primaryKey  = classMeta.checkPrimaryKey();
    Attribute logicDelete = classMeta.getLogicDelete();

    Collection<Attribute> updateAttributes = classMeta.getUpdateAttributes(columns);

    if (updateAttributes.isEmpty())
      throw new DBException("Could not match update attributes.");

    String tableName = getTableName(classMeta);

    SQLUpdateStatement updateStatement = DruidUtil.createSQLUpdateStatement(tableName);

    for (Attribute attr : updateAttributes) {
      updateStatement.addItem(DruidUtil.createUpdateSetItem(attr.getColumnName()));
    }

    updateStatement.addWhere(createCondition(primaryKey, logicDelete));
    return updateStatement.toUnformattedString();
  }

  @Override
  protected Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLSelect          select     = selectStmt.getSelect();

    SQLServerSelectQueryBlock queryBlock = (SQLServerSelectQueryBlock) select.getQuery();

    // 构建 ROW_NUMBER() OVER (ORDER BY ...) AS _rn 列
    SQLSelectItem rowNumSelectItem = createRowNumberSQLSelectItem(queryBlock);
    queryBlock.getSelectList().add(rowNumSelectItem);

    // 构建外层查询语句
    SQLServerSelectQueryBlock countQueryBlock = new SQLServerSelectQueryBlock();
    countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
    countQueryBlock.setFrom(new SQLSubqueryTableSource(queryBlock.clone(), TABLE_ALIAS));
    countQueryBlock.setWhere(createBetweenExpr(offset, count));

    select.setQuery(countQueryBlock);
    return selectStmt;
  }

  private SQLSelectItem createRowNumberSQLSelectItem(SQLServerSelectQueryBlock queryBlock) {
    SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
    aggregateExpr.setOver(createSQLOver(queryBlock));
    queryBlock.setOrderBy(null);
    return new SQLSelectItem(aggregateExpr, COL_ROW_NUM);
  }

  private SQLOver createSQLOver(SQLServerSelectQueryBlock queryBlock) {
    if (Objects.isNull(queryBlock.getOrderBy()))
      return new SQLOver(new SQLOrderBy(SQLUtils.toSQLExpr("CURRENT_TIMESTAMP")));
    return new SQLOver(queryBlock.getOrderBy());
  }

  private static SQLBetweenExpr createBetweenExpr(int offset, int count) {
    return new SQLBetweenExpr(
        new SQLIdentifierExpr(COL_ROW_NUM),
        new SQLNumberExpr(offset + 1),
        new SQLNumberExpr(count + offset));
  }
}
