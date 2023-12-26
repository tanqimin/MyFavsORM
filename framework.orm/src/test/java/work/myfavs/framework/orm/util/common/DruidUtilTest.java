package work.myfavs.framework.orm.util.common;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;
import work.myfavs.framework.orm.meta.DbType;

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
}