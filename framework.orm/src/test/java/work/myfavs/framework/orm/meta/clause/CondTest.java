package work.myfavs.framework.orm.meta.clause;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CondTest {

  @Test
  public void eq() {

    Cond cond = Cond.eq("code", null, true);
    Assert.assertEquals(cond.sql.toString(), "");

    cond = Cond.eq("code", null, false);
    Assert.assertEquals(cond.sql.toString(), " code IS NULL");

    cond = Cond.eq("code", 1, true);
    Assert.assertEquals(cond.sql.toString(), " code = ?");
    Assert.assertTrue(cond.params.size() > 0);
  }

  @Test
  public void ne() {

    Cond cond = Cond.ne("code", null, true);
    Assert.assertEquals(cond.sql.toString(), "");

    cond = Cond.ne("code", null, false);
    Assert.assertEquals(cond.sql.toString(), " code IS NOT NULL");

    cond = Cond.ne("code", 1, true);
    Assert.assertEquals(cond.sql.toString(), " code <> ?");
    Assert.assertTrue(cond.params.size() > 0);
  }

  @Test
  public void isNull() {

    Cond cond = Cond.isNull("code");
    Assert.assertEquals(cond.sql.toString(), " code IS NULL");
  }

  @Test
  public void isNotNull() {

    Cond cond = Cond.isNotNull("code");
    Assert.assertEquals(cond.sql.toString(), " code IS NOT NULL");
  }

  @Test
  public void gt() {}

  @Test
  public void ge() {}

  @Test
  public void lt() {}

  @Test
  public void le() {}

  @Test
  public void between() {}

  @Test
  public void in() {

    List list = new ArrayList();
    list.add("");
    Cond cond = Cond.in("code", list);
    Assert.assertEquals(cond.sql.toString(), "");

    cond = Cond.in("code", list, false);
    Assert.assertEquals(cond.sql.toString(), " 1 > 2");

    list.add("A");
    cond = Cond.in("code", list);
    Assert.assertEquals(cond.sql.toString(), " code = ?");

    list.add("B");
    cond = Cond.in("code", list);
    Assert.assertEquals(cond.sql.toString(), " code IN (?,?)");
    Assert.assertEquals(2, cond.params.size());

    Sql sql = Sql.Select("*").from("dept");
    cond = Cond.in("code", sql);
    Assert.assertEquals(cond.sql.toString(), " code IN (SELECT * FROM dept)");
  }

  @Test
  public void notIn() {

    List list = new ArrayList();
    Cond cond = Cond.notIn("code", list);
    Assert.assertEquals(cond.sql.toString(), "");

    cond = Cond.notIn("code", list, false);
    Assert.assertEquals(cond.sql.toString(), " 1 > 2");

    list.add("A");
    cond = Cond.notIn("code", list);
    Assert.assertEquals(cond.sql.toString(), " code <> ?");

    list.add("B");
    cond = Cond.notIn("code", list);
    Assert.assertEquals(cond.sql.toString(), " code NOT IN (?,?)");
    Assert.assertEquals(2, cond.params.size());

    Sql sql = Sql.Select("*").from("dept");
    cond = Cond.notIn("code", sql);
    Assert.assertEquals(cond.sql.toString(), " code NOT IN (SELECT * FROM dept)");
  }
}
