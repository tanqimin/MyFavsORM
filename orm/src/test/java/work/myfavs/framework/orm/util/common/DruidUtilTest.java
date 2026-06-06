package work.myfavs.framework.orm.util.common;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;
import work.myfavs.framework.orm.meta.DbType;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DruidUtilTest {

  private final static String SQL = "SELECT * FROM TB_USER";

  @Test
  public void convert() {
    assertEquals(JdbcConstants.SQL_SERVER, DruidUtil.convert(DbType.SQL_SERVER));
    assertEquals(JdbcConstants.SQL_SERVER, DruidUtil.convert(DbType.SQL_SERVER_2012));
    assertEquals(JdbcConstants.MYSQL, DruidUtil.convert(DbType.MYSQL));
    assertEquals(JdbcConstants.POSTGRESQL, DruidUtil.convert(DbType.POSTGRE_SQL));
    assertEquals(JdbcConstants.ORACLE, DruidUtil.convert(DbType.ORACLE));
    assertEquals(JdbcConstants.H2, DruidUtil.convert(DbType.H2));
  }

  @Test
  public void createSQLSelectStatement() {
    SQLSelectStatement sqlSelectStatement = DruidUtil.createSQLSelectStatement(DbType.MYSQL, SQL);
    assertEquals(com.alibaba.druid.DbType.mysql, sqlSelectStatement.getDbType());
  }

  @Test
  public void testCreateSQLSelectStatement() {
    SQLSelectStatement sqlSelectStatement = DruidUtil.createSQLSelectStatement(com.alibaba.druid.DbType.mysql, SQL);
    assertEquals(com.alibaba.druid.DbType.mysql, sqlSelectStatement.getDbType());
  }

  @Test
  public void createSQLInsertStatement() {
    SQLInsertStatement statement = DruidUtil.createSQLInsertStatement("TB_USER");
    assertEquals(statement.getTableSource(), new SQLExprTableSource("TB_USER"));
  }

  @Test
  public void createSQLUpdateStatement() {
    SQLUpdateStatement statement = DruidUtil.createSQLUpdateStatement("TB_USER");
    assertEquals(statement.getTableSource(), new SQLExprTableSource("TB_USER"));
  }

  @Test
  public void createParam() {
    SQLVariantRefExpr param = DruidUtil.createParam();
    assertEquals(param.getName(), "?");
  }

  @Test
  public void createColumn() {
    SQLIdentifierExpr username = DruidUtil.createColumn("username");
    assertEquals(username.getName(), "username");
  }

  @Test
  public void createUpdateSetItem() {
    SQLUpdateSetItem username = DruidUtil.createUpdateSetItem("username");
    assertEquals(username.getColumn(), DruidUtil.createColumn("username"));
  }

  @Test
  public void createTableSource() {
    SQLExprTableSource tableSource = DruidUtil.createTableSource("TB_USER");
    assertEquals(tableSource.getName().getSimpleName(), "TB_USER");
  }

  @Test
  public void formatSql() {
    String sql = "SELECT t1.id AS sku_id, t2.code AS product_code, t2.name AS product_name, t4.code AS brand_code, t4.name AS brand_name\n" +
        "FROM sku t1 LEFT JOIN PRODUCT t2 ON t1.product_id=t2.id JOIN(SELECT DISTINCT TOP 100 sku_id FROM rfid WHERE 1=1 /* and created = 创建日期 */) t3 ON t1.id=t3.sku_id LEFT JOIN BRAND t4 ON t4.id=t2.brand_id\n" +
        "WHERE 1=1 /* and t1.created = 创建日期 */ /* or t2.code = 单号 */";

    List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, com.alibaba.druid.DbType.sqlserver);
    for (SQLStatement sqlStatement : sqlStatements) {
      System.out.println(sqlStatement.toParameterizedString());
    }

    SQLSelectStatement sqlSelectStatement = DruidUtil.createSQLSelectStatement(com.alibaba.druid.DbType.sqlserver, sql);
    String             formatSql          = sqlSelectStatement.toString();
    System.out.println(formatSql);
  }
}