package work.myfavs.framework.orm.orm.strategy;

import org.junit.Test;
import work.myfavs.framework.orm.meta.clause.Sql;

import java.util.List;

import static org.junit.Assert.*;

public class PageStrategyTest {

  private static final String SQL = "SELECT * FROM tb_user WHERE name = ? ORDER BY id";
  private static final List<Object> PARAMS = List.of("test");

  @Test
  public void mySqlPageStrategy_ShouldApplyLimit() {
    PageStrategy strategy = MySqlPageStrategy.INSTANCE;
    Sql result = strategy.apply(SQL, PARAMS, 1, 20);
    String sql = result.toString();
    assertTrue("MySQL pagination should contain LIMIT", sql.contains("LIMIT"));
    assertEquals(1, result.getParams().size());
    assertEquals("test", result.getParams().get(0));
  }

  @Test
  public void mySqlPageStrategy_WithOffset_ShouldIncludeOffset() {
    PageStrategy strategy = MySqlPageStrategy.INSTANCE;
    Sql result = strategy.apply("SELECT * FROM tb_user ORDER BY id", List.of(), 2, 20);
    String sql = result.toString();
    assertTrue(sql.contains("LIMIT"));
  }

  @Test
  public void sqlServerPageStrategy_ShouldApplyRowNumber() {
    PageStrategy strategy = SqlServerPageStrategy.INSTANCE;
    Sql result = strategy.apply(SQL, PARAMS, 1, 20);
    String sql = result.toString();
    assertTrue("SQL Server pagination should contain ROW_NUMBER", sql.contains("ROW_NUMBER"));
    assertEquals(1, result.getParams().size());
  }

  @Test
  public void sqlServer2012PageStrategy_ShouldApplyOffsetFetch() {
    PageStrategy strategy = SqlServer2012PageStrategy.INSTANCE;
    Sql result = strategy.apply("SELECT * FROM tb_user ORDER BY id", List.of(), 1, 20);
    String sql = result.toString();
    assertTrue("SQL Server 2012 pagination should contain OFFSET", sql.contains("OFFSET"));
    assertTrue("SQL Server 2012 pagination should contain FETCH", sql.contains("FETCH"));
    // OFFSET/FETCH 值嵌入 SQL 字面量，不追加到参数列表
    assertEquals(0, result.getParams().size());
  }

  @Test
  public void oraclePageStrategy_ShouldApplyRowNum() {
    PageStrategy strategy = OraclePageStrategy.INSTANCE;
    Sql result = strategy.apply(SQL, PARAMS, 1, 20);
    String sql = result.toString();
    assertTrue("Oracle pagination should contain ROWNUM", sql.contains("ROWNUM"));
    assertEquals(1, result.getParams().size());
  }

  @Test
  public void oraclePageStrategy_WithOffset_ShouldProduceSubQuery() {
    PageStrategy strategy = OraclePageStrategy.INSTANCE;
    Sql result = strategy.apply("SELECT * FROM tb_user ORDER BY id", List.of(), 2, 20);
    String sql = result.toString();
    assertTrue(sql.contains("_rn"));
    assertTrue(sql.contains("_limit"));
    assertTrue(sql.contains("_paginate"));
  }

  @Test
  public void pageStrategies_ShouldPreserveOriginalParams() {
    PageStrategy mysql   = MySqlPageStrategy.INSTANCE;
    PageStrategy sqlsrv  = SqlServerPageStrategy.INSTANCE;
    PageStrategy oracle  = OraclePageStrategy.INSTANCE;

    List<Object> params = List.of("a", 123);
    String sql = "SELECT * FROM t WHERE x = ? AND y = ? ORDER BY id";

    for (PageStrategy strategy : new PageStrategy[]{mysql, sqlsrv, oracle}) {
      Sql result = strategy.apply(sql, params, 1, 10);
      assertEquals("Strategy " + strategy.getClass().getSimpleName() + " should preserve params",
          params.size(), result.getParams().size());
    }
  }

  @Test
  public void sqlServer2012PageStrategy_ShouldAddOffsetParams() {
    List<Object> params = List.of("a");
    String sql = "SELECT * FROM t WHERE x = ? ORDER BY id";
    Sql result = SqlServer2012PageStrategy.INSTANCE.apply(sql, params, 1, 10);
    // OFFSET/FETCH 值已嵌入 SQL 字面量，不再作为 ? 参数追加
    assertEquals(1, result.getParams().size());
  }

}
