package work.myfavs.framework.orm.meta.clause;

import org.junit.Test;

public class SqlTest {

  @Test
  public void and() {

    Sql sql = Sql.New("SELECT * FROM table")
                 .where()
                 .and(Cond.le("c0", null))
                 .and(Cond.eq("c1", null))
                 .and(() -> Cond.eq("c2", 1)
                                .or(Cond.eq("c3", 2)));

    System.out.println(sql);
  }

  @Test
  public void or() {

  }

  @Test
  public void selectAll() {

    Sql sql1 = Sql.SelectAll()
                  .from("TABLE");

    Sql sql2 = new Sql();
    sql2.append("SELECT * FROM TABLE T" + "LEFT JOIN TABLE2 T2 ON T.t2_id = T2.id" + "WHERE T.id = ?", 1);

  }

}