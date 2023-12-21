package work.myfavs.framework.orm.meta.clause;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SqlTest {

  @Test
  public void newTest() {
    Sql sql = new Sql();
    assertNotNull(sql);
    assertEquals("", sql.toString());
    assertEquals(0, sql.params.size());
  }

  @Test
  public void addParam() {
    Sql sql = new Sql().addParam("test");
    assertEquals(1, sql.params.size());
  }

  @Test
  public void addParams() {
    Sql sql = new Sql().addParams(List.of("1", "2", "3"));
    assertEquals(3, sql.params.size());
  }

  @Test
  public void append() {
    Sql sql = new Sql().append(new Sql("SELECT ?", 1));
    assertEquals("SELECT ?", sql.toString());
    assertEquals(1, sql.params.size());
  }

  @Test
  public void testAppend() {
    Sql sql = new Sql().append(Cond.eq("id", 1));
    assertEquals(" id = ?", sql.toString());
    assertEquals(1, sql.params.size());
  }

  @Test
  public void testAppend1() {
    Sql sql = new Sql().append("SELECT 1");
    assertEquals("SELECT 1", sql.toString());
  }

  @Test
  public void testAppend2() {
    Sql sql = new Sql().append("SELECT ?,?,?", 1, 2, 3);
    assertEquals("SELECT ?,?,?", sql.toString());
    assertEquals(3, sql.params.size());
  }

  @Test
  public void testAppend3() {
    Sql sql = new Sql().append("SELECT ?,?,?", List.of(1, 2, 3));
    assertEquals("SELECT ?,?,?", sql.toString());
    assertEquals(3, sql.params.size());
  }

  @Test
  public void appendLine() {
    Sql sql = new Sql().appendLine("SELECT ?");
    assertEquals("SELECT ?".concat(System.lineSeparator()), sql.toString());
  }

  @Test
  public void testAppendLine() {
    Sql sql = new Sql().appendLine(Cond.eq("id", 1));
    assertEquals(" id = ?".concat(System.lineSeparator()), sql.toString());
    assertEquals(1, sql.params.size());
  }

  @Test
  public void testAppendLine1() {
    Sql sql = new Sql().appendLine(new Sql("SELECT ?", 1));
    assertEquals("SELECT ?".concat(System.lineSeparator()), sql.toString());
    assertEquals(1, sql.params.size());
  }

  @Test
  public void testAppendLine2() {
    Sql sql = new Sql().appendLine("SELECT ?,?,?", 1, 2, 3);
    assertEquals("SELECT ?,?,?".concat(System.lineSeparator()), sql.toString());
    assertEquals(3, sql.params.size());
  }

  @Test
  public void testAppendLine3() {
    Sql sql = new Sql().appendLine("SELECT ?,?,?", List.of(1, 2, 3));
    assertEquals("SELECT ?,?,?".concat(System.lineSeparator()), sql.toString());
    assertEquals(3, sql.params.size());
  }

  @Test
  public void selectAll() {
    assertEquals("SELECT *", Sql.SelectAll().toString());
    assertEquals(" SELECT *", new Sql().selectAll().toString());
  }

  @Test
  public void select() {
    assertEquals("SELECT id,name", Sql.Select("id", "name").toString());
    assertEquals(" SELECT id,name", new Sql().select("id", "name").toString());
  }

  @Test
  public void from() {
    Sql sql = Sql.SelectAll().from("user");
    assertEquals("SELECT * FROM user", sql.toString());
  }

  @Test
  public void testFrom() {
    Sql sql = Sql.SelectAll().from("user", "u");
    assertEquals("SELECT * FROM user u", sql.toString());
  }

  @Test
  public void testFrom1() {
    Sql sql = Sql.SelectAll().from(Sql.SelectAll().from("user"), "u");
    assertEquals("SELECT * FROM (SELECT * FROM user) u", sql.toString());
  }

  @Test
  public void testFrom2() {
    Sql sql = Sql.SelectAll().from(() -> Sql.SelectAll().from("user"), "u");
    assertEquals("SELECT * FROM (SELECT * FROM user) u", sql.toString());
  }

  @Test
  public void leftJoin() {
    Sql sql = new Sql().leftJoin("user", "u", "u.id = id");
    assertEquals(" LEFT JOIN user u ON u.id = id", sql.toString());
  }

  @Test
  public void testLeftJoin() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().leftJoin(user, "u", "u.id = id");
    assertEquals(String.format(" LEFT JOIN (%s) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void testLeftJoin1() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().leftJoin(() -> user, "u", "u.id = id");
    assertEquals(String.format(" LEFT JOIN (%s) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void rightJoin() {
    Sql sql = new Sql().rightJoin("user", "u", "u.id = id");
    assertEquals(" RIGHT JOIN user u ON u.id = id", sql.toString());
  }

  @Test
  public void testRightJoin() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().rightJoin(user, "u", "u.id = id");
    assertEquals(
        String.format(" RIGHT JOIN (%s) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void testRightJoin1() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().rightJoin(() -> user, "u", "u.id = id");
    assertEquals(
        String.format(" RIGHT JOIN (%s) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void innerJoin() {
    Sql sql = new Sql().innerJoin("user", "u", "u.id = id");
    assertEquals(" INNER JOIN user u ON u.id = id", sql.toString());
  }

  @Test
  public void testInnerJoin() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().innerJoin(user, "u", "u.id = id");
    assertEquals(
        String.format(" INNER JOIN (%s) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void testInnerJoin1() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().innerJoin(() -> user, "u", "u.id = id");
    assertEquals(
        String.format(" INNER JOIN (%s) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void fullJoin() {
    Sql sql = new Sql().fullJoin("user", "u", "u.id = id");
    assertEquals(" FULL JOIN user u ON u.id = id", sql.toString());
  }

  @Test
  public void testFullJoin() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().fullJoin(user, "u", "u.id = id");
    assertEquals(String.format(" FULL JOIN (%s) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void testFullJoin1() {
    Sql user = Sql.SelectAll().from("user");
    Sql sql = new Sql().fullJoin(() -> user, "u", "u.id = id");
    assertEquals(String.format(" FULL JOIN ({}) u ON u.id = id", user.toString()), sql.toString());
  }

  @Test
  public void where() {
    assertEquals(" WHERE 1 = 1", new Sql().where().toString());
  }

  @Test
  public void testWhere() {
    assertEquals(" WHERE id = ?", new Sql().where(Cond.eq("id", 1)).toString());
  }

  @Test
  public void testWhere1() {
    assertEquals(" WHERE 1 = 1", new Sql().where("1 = 1").toString());
  }

  @Test
  public void testWhere2() {
    assertEquals(
        " WHERE id = ?, name = ?",
        new Sql().where("id = ?, name = ?", List.of(1, "test")).toString());
  }

  @Test
  public void and() {
    Sql sql = new Sql().and(Cond.eq("id", 1));
    assertEquals(" AND id = ?", sql.toString());
  }

  @Test
  public void testAnd() {
    Sql sql = new Sql().and(() -> Cond.eq("id", 1));
    assertEquals(" AND (id = ?)", sql.toString());
  }

  @Test
  public void or() {
    Sql sql = new Sql().or(Cond.eq("id", 1));
    assertEquals(" OR id = ?", sql.toString());
  }

  @Test
  public void testOr() {
    Sql sql = new Sql().or(() -> Cond.eq("id", 1));
    assertEquals(" OR (id = ?)", sql.toString());
  }

  @Test
  public void asSubQuery() {
    Sql sql = Sql.SelectAll().from("user").asSubQuery("u");
    assertEquals("SELECT * FROM (SELECT * FROM user) u", sql.toString());
  }

  @Test
  public void union() {
    Sql sql = new Sql("SELECT 1").union().append("SELECT 2");
    assertEquals("SELECT 1 UNION SELECT 2", sql.toString());
  }

  @Test
  public void unionAll() {
    Sql sql = new Sql("SELECT 1").unionAll().append("SELECT 2");
    assertEquals("SELECT 1 UNION ALL SELECT 2", sql.toString());
  }

  @Test
  public void groupBy() {
    Sql sql = new Sql("SELECT name, COUNT(*) FROM user").groupBy("name");
    assertEquals("SELECT name, COUNT(*) FROM user GROUP BY name", sql.toString());
  }

  @Test
  public void having() {
    Sql sql1 =
        new Sql("SELECT name, COUNT(*) FROM user")
            .groupBy("name")
            .having()
            .and(Cond.gt("COUNT(*)", 1));
    Sql sql2 = new Sql("SELECT name, COUNT(*) FROM user").groupBy("name").having("COUNT(*) > 1");
    assertEquals(
        "SELECT name, COUNT(*) FROM user GROUP BY name HAVING 1 = 1 AND COUNT(*) > ?",
        sql1.toString());
    assertEquals(
        "SELECT name, COUNT(*) FROM user GROUP BY name HAVING COUNT(*) > 1", sql2.toString());
  }

  @Test
  public void testHaving() {
    Sql sql =
        new Sql("SELECT name, COUNT(*) FROM user")
            .groupBy("name")
            .having("COUNT(*) BETWEEN ? AND ?", 1, 3);
    assertEquals(
        "SELECT name, COUNT(*) FROM user GROUP BY name HAVING COUNT(*) BETWEEN ? AND ?",
        sql.toString());
  }

  @Test
  public void testHaving1() {
    Sql sql =
        new Sql("SELECT name, COUNT(*) FROM user")
            .groupBy("name")
            .having(Cond.between("COUNT(*)", 1, 3));
    assertEquals(
        "SELECT name, COUNT(*) FROM user GROUP BY name HAVING COUNT(*) BETWEEN ? AND ?",
        sql.toString());
  }

  @Test
  public void orderBy() {
    Sql sql = Sql.SelectAll().from("user").orderBy("id");
    assertEquals("SELECT * FROM user ORDER BY id", sql.toString());
  }

  @Test
  public void testOrderBy() {
    Sql sql = Sql.SelectAll().from("user").orderBy("id", "name DESC");
    assertEquals("SELECT * FROM user ORDER BY id, name DESC", sql.toString());
  }

  @Test
  public void limit() {
    Sql sql = Sql.SelectAll().from("user").limit(1);
    assertEquals("SELECT * FROM user LIMIT 1", sql.toString());
  }

  @Test
  public void testLimit() {
    Sql sql = Sql.SelectAll().from("user").limit(1, 2);
    assertEquals("SELECT * FROM user LIMIT 1, 2", sql.toString());
  }

  @Test
  public void insert() {
    Sql sql = Sql.Insert("user", "id", "name");
    assertEquals("INSERT INTO user (id, name)", sql.toString());
  }

  @Test
  public void values() {
    Sql sql = Sql.Insert("user", "id", "name").values(1, "test");
    assertEquals("INSERT INTO user (id, name) VALUES (?, ?)", sql.toString());
  }

  @Test
  public void update() {
    Sql sql = Sql.Update("user").set("name", "test");
    assertEquals("UPDATE user SET name = ?", sql.toString());
  }

  @Test
  public void set() {
    Sql sql = Sql.Update("user").set("name = 'test'");
    assertEquals("UPDATE user SET name = 'test'", sql.toString());
  }

  @Test
  public void delete() {
    Sql sql = Sql.Delete("user");
    assertEquals("DELETE FROM user", sql.toString());
  }

  @Test
  public void testDelete() {
    Sql sql = Sql.Delete("user", "u");
    assertEquals("DELETE u FROM user u", sql.toString());
  }

  @Test
  public void testToString() {
    Sql sql = Sql.SelectAll().from("user");
    assertEquals("SELECT * FROM user", sql.toString());
  }

  @Test
  public void deleteLastChar() {
    Sql sql = Sql.SelectAll().from("user").append(",");
    assertEquals("SELECT * FROM user", sql.deleteLastChar(",").toString());
  }
}
