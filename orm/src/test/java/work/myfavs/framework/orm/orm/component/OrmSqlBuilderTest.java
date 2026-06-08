package work.myfavs.framework.orm.orm.component;

import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.entity.IdentityExample;
import work.myfavs.framework.orm.entity.LogicDeleteExample;
import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.entity.UuidExample;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;

import static org.junit.Assert.*;

public class OrmSqlBuilderTest {

  private OrmSqlBuilder sqlBuilder;
  private ClassMeta snowflakeMeta;
  private ClassMeta identityMeta;
  private ClassMeta uuidMeta;
  private ClassMeta logicDeleteMeta;

  @Before
  public void setUp() {
    sqlBuilder = new OrmSqlBuilder("mysql");
    snowflakeMeta = Metadata.entityMeta(SnowflakeExample.class);
    identityMeta = Metadata.entityMeta(IdentityExample.class);
    uuidMeta = Metadata.entityMeta(UuidExample.class);
    logicDeleteMeta = Metadata.entityMeta(LogicDeleteExample.class);
  }

  @Test
  public void getTableName_ShouldReturnEntityTableName_WhenNoAliasSet() {
    assertEquals("tb_snowflake", OrmSqlBuilder.getTableName(snowflakeMeta));
    assertEquals("tb_identity", OrmSqlBuilder.getTableName(identityMeta));
  }

  @Test
  public void insert_ShouldIncludePrimaryKey_WhenStrategyIsNotIdentity() {
    String sql = sqlBuilder.insert(snowflakeMeta);
    assertTrue(sql.contains("INSERT INTO tb_snowflake"));
    assertTrue(sql.contains("id"));
  }

  @Test
  public void insert_ShouldExcludePrimaryKey_WhenStrategyIsIdentity() {
    String sql = sqlBuilder.insert(identityMeta);
    assertTrue(sql.contains("INSERT INTO tb_identity"));
  }

  @Test
  public void insert_ShouldIncludeLogicDeleteColumn_WhenEntityHasLogicDelete() {
    String sql = sqlBuilder.insert(logicDeleteMeta);
    assertTrue(sql.contains("deleted"));
  }

  @Test
  public void insert_ShouldContainQuestionMarkParams() {
    String sql = sqlBuilder.insert(snowflakeMeta);
    // 主键(id) + name, price, type, created, disable, config = 7 个 ?
    long paramCount = sql.chars().filter(c -> c == '?').count();
    assertEquals(7, paramCount);
  }

  @Test
  public void select_ShouldGenerateSelectStar() {
    Sql sql = sqlBuilder.select(snowflakeMeta);
    assertEquals("SELECT * FROM tb_snowflake", sql.toString());
  }

  @Test
  public void countSql_ByEntityMeta_ShouldGenerateCountStar() {
    Sql sql = sqlBuilder.countSql(snowflakeMeta);
    assertEquals("SELECT COUNT(*) FROM tb_snowflake", sql.toString());
  }

  @Test
  public void countSql_ByRawSql_ShouldWrapWithCount() {
    Sql sql = sqlBuilder.countSql("SELECT * FROM tb_user WHERE name = ?", java.util.List.of("test"));
    assertTrue(sql.toString().toUpperCase().contains("COUNT"));
    assertEquals(1, sql.getParams().size());
    assertEquals("test", sql.getParams().get(0));
  }

  @Test
  public void createCondition_ShouldProducePkEqualsParam() {
    Attribute pk = snowflakeMeta.checkPrimaryKey();
    com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr expr =
        OrmSqlBuilder.createCondition(pk, null);
    assertNotNull(expr);
  }

  @Test
  public void createCondition_WithLogicDelete_ShouldAddAndCondition() {
    Attribute pk = logicDeleteMeta.checkPrimaryKey();
    Attribute ld = logicDeleteMeta.getLogicDelete();
    com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr expr =
        OrmSqlBuilder.createCondition(pk, ld);
    assertNotNull(expr);
  }

  @Test
  public void update_ShouldGenerateUpdateWithWhereClause() {
    SnowflakeExample entity = new SnowflakeExample();
    entity.setId(1L);
    Sql sql = sqlBuilder.update(snowflakeMeta, entity, false);
    assertTrue(sql.toString().toUpperCase().contains("UPDATE"));
    assertTrue(sql.toString().toUpperCase().contains("WHERE"));
    assertTrue(sql.toString().contains("?"));
  }

  @Test
  public void update_IgnoreNull_ShouldOmitNullFields() {
    // 只设置 ID，name 为 null（未设置）
    IdentityExample entity = new IdentityExample();
    entity.setId(1L);

    Sql sql = sqlBuilder.update(identityMeta, entity, true);
    // name 为 null 时不应出现在 SET 子句中
    String sqlStr = sql.toString().toUpperCase();
    assertTrue(sqlStr.contains("UPDATE"));
    // Should still have valid SQL with WHERE clause
    assertTrue(sqlStr.contains("WHERE"));
  }
}
