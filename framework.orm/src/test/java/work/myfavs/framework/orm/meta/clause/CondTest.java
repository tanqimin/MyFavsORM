package work.myfavs.framework.orm.meta.clause;

import static org.junit.Assert.*;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.entity.*;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.enumeration.FuzzyMode;
import work.myfavs.framework.orm.meta.enumeration.Operator;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;

public class CondTest {
  ClassMeta identityClassMeta;
  ClassMeta snowflakeClassMeta;
  ClassMeta uuidClassMeta;
  ClassMeta logicDeleteMeta;

  @Before
  public void setUp() {
    identityClassMeta = Metadata.entityMeta(Identity.class);
    snowflakeClassMeta = Metadata.entityMeta(Snowflake.class);
    uuidClassMeta = Metadata.entityMeta(Uuid.class);
    logicDeleteMeta = Metadata.entityMeta(LogicDelete.class);
  }

  @After
  public void tearDown() {}

  @Test
  public void logicalDeleteCond() {
    Cond cond = Cond.logicalDeleteCond(logicDeleteMeta);
    assertEquals(" deleted = ?", cond.toString());
    assertEquals(1, cond.getParams().size());
    assertFalse(cond.getParams().get(0) instanceof Boolean);
  }

  @Test
  public void eq() {
    assertEquals(" id = ?", Cond.eq("id", 0).toString());
  }

  @Test
  public void testEq() {
    assertTrue(StrUtil.equalsIgnoreCase(" id is null", Cond.eq("id", null, false).toString()));
    assertEquals("", Cond.eq("id", null, true).toString());
  }

  @Test
  public void ne() {
    assertEquals(" id <> ?", Cond.ne("id", 0).toString());
  }

  @Test
  public void testNe() {
    assertTrue(StrUtil.equalsIgnoreCase(" id is not null", Cond.ne("id", null, false).toString()));
    assertEquals("", Cond.ne("id", null, true).toString());
  }

  @Test
  public void isNull() {
    assertTrue(StrUtil.equalsIgnoreCase(" id is null", Cond.eq("id", null, false).toString()));
  }

  @Test
  public void isNotNull() {
    assertTrue(StrUtil.equalsIgnoreCase(" id is not null", Cond.ne("id", null, false).toString()));
  }

  @Test
  public void gt() {
    assertTrue(StrUtil.equalsIgnoreCase(" id > ?", Cond.gt("id", 0).toString()));
    assertEquals("", Cond.gt("id", null).toString());
  }

  @Test
  public void ge() {
    assertTrue(StrUtil.equalsIgnoreCase(" id >= ?", Cond.ge("id", 0).toString()));
    assertEquals("", Cond.ge("id", null).toString());
  }

  @Test
  public void lt() {
    assertTrue(StrUtil.equalsIgnoreCase(" id < ?", Cond.lt("id", 0).toString()));
    assertEquals("", Cond.lt("id", null).toString());
  }

  @Test
  public void le() {
    assertTrue(StrUtil.equalsIgnoreCase(" id <= ?", Cond.le("id", 0).toString()));
    assertEquals("", Cond.le("id", null).toString());
  }

  @Test
  public void like() {
    assertEquals("", Cond.like("id", null).toString());
    assertEquals(" id = ?", Cond.like("id", 1).toString());
    assertEquals(" id LIKE ?", Cond.like("id", "1%").toString());

    String paramVal   = "_ABC%";
    Cond   multiLike  = Cond.like("id", paramVal, FuzzyMode.MULTIPLE);
    Cond   singleLike = Cond.like("id", paramVal, FuzzyMode.SINGLE);

    assertEquals(" id = ?", Cond.like("id", 1, FuzzyMode.ALL).toString());
    assertEquals(" id = ?", Cond.like("id", 1, FuzzyMode.MULTIPLE).toString());
    assertEquals(" id = ?", Cond.like("id", 1, FuzzyMode.SINGLE).toString());
    assertEquals(" id LIKE ?", Cond.like("id", paramVal, FuzzyMode.ALL).toString());
    assertEquals(" id LIKE ? ESCAPE '¦'", multiLike.toString());
    assertEquals(" id LIKE ? ESCAPE '¦'", singleLike.toString());
    assertEquals(multiLike.params.get(0), "¦_ABC%");
    assertEquals(singleLike.params.get(0), "_ABC¦%");
  }

  @Test
  public void between() {
    assertEquals("", Cond.between("id", null, null).toString());
    assertEquals(" id >= ?", Cond.between("id", 1, null).toString());
    assertEquals(" id <= ?", Cond.between("id", null, 2).toString());
    assertEquals(" id BETWEEN ? AND ?", Cond.between("id", 1, 2).toString());
  }

  @Test
  public void in() {
    List<Long> list = new ArrayList<>();
    assertEquals("", Cond.in("id", list).toString());
    list.add(1L);
    assertEquals(" id = ?", Cond.in("id", list).toString());
    list.add(2L);
    assertEquals(" id IN (?,?)", Cond.in("id", list).toString());
  }

  @Test
  public void testIn() {
    List<Long> list = new ArrayList<>();
    assertEquals(" 1 > 2", Cond.in("id", list, false).toString());
  }

  @Test
  public void testIn1() {
    Sql sql = Sql.New("SELECT id FROM tb_snowflake");
    assertEquals(StrUtil.format(" id IN ({})", sql), Cond.in("id", sql).toString());
  }

  @Test
  public void notIn() {
    List<Long> list = new ArrayList<>();
    assertEquals("", Cond.notIn("id", list).toString());
    list.add(1L);
    assertEquals(" id <> ?", Cond.notIn("id", list).toString());
    list.add(2L);
    assertEquals(" id NOT IN (?,?)", Cond.notIn("id", list).toString());
  }

  @Test
  public void testNotIn() {
    List<Long> list = new ArrayList<>();
    assertEquals(" 1 > 2", Cond.notIn("id", list, false).toString());
  }

  @Test
  public void testNotIn1() {
    Sql sql = Sql.New("SELECT id FROM tb_snowflake");
    assertEquals(StrUtil.format(" id NOT IN ({})", sql), Cond.notIn("id", sql).toString());
  }

  @Test
  public void exists() {
    Sql sql = Sql.New("SELECT id FROM tb_snowflake");
    assertEquals(StrUtil.format(" EXISTS ({})", sql), Cond.exists(sql).toString());
  }

  @Test
  public void testExists() {
    Sql sql = Sql.New("SELECT id FROM tb_snowflake");
    assertEquals(StrUtil.format(" EXISTS ({})", sql), Cond.exists(() -> sql).toString());
  }

  @Test
  public void notExists() {
    Sql sql = Sql.New("SELECT id FROM tb_snowflake");
    assertEquals(StrUtil.format(" NOT EXISTS ({})", sql), Cond.notExists(sql).toString());
  }

  @Test
  public void testNotExists() {
    Sql sql = Sql.New("SELECT id FROM tb_snowflake");
    assertEquals(StrUtil.format(" NOT EXISTS ({})", sql), Cond.notExists(() -> sql).toString());
  }

  @Test
  public void and() {}

  @Test
  public void or() {}

  @Test
  public void create() {
    Snowflake snowflake = new Snowflake();
    snowflake.setName("S1");
    snowflake.setType(TypeEnum.FOOD);

    Cond cond = Cond.createByCriteria(snowflake);
    assertEquals(" name = ? AND type = ?", cond.toString());
  }

  @Test
  public void testCreate() {
    Snowflake snowflake = new Snowflake();
    snowflake.setName("S1");
    snowflake.setType(TypeEnum.FOOD);

    Cond cond = Cond.createByCriteria(snowflake, BaseEntity.Update.class);
    assertEquals(" name <> ?", cond.toString());

    Person person = new Person();
    person.setName("Person1");
    person.setCars(new String[]{"Car1", "Car2"});
    person.setAlias(Arrays.asList("Alias1", "Alias2"));

    cond = Cond.createByCriteria(person, Person.PersonQuery.class);
    System.out.println(cond.toString());
    assertEquals(" person_alias IN (?,?) AND person_name = ?", cond.toString());

    cond = Cond.createByCriteria(person, Person.PersonList.class);
    assertEquals(" cars NOT IN (?,?)", cond.toString());
  }

  @Test
  public void testToString() {
    assertEquals(" id = ?", Cond.eq("id", 0).toString());
  }
}

class Person {
  interface PersonQuery {}

  interface PersonList {}

  @Criterion(value = "person_name", operator = Operator.EQUALS, order = 2, group = PersonQuery.class)
  private String name;

  @Criterion(operator = Operator.NOT_IN, group = PersonList.class)
  private String[] cars;

  @Criterion(value = "person_alias", operator = Operator.IN, group = PersonQuery.class)
  private List<String> alias;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getCars() {
    return cars;
  }

  public void setCars(String[] cars) {
    this.cars = cars;
  }

  public List<String> getAlias() {
    return alias;
  }

  public void setAlias(List<String> alias) {
    this.alias = alias;
  }
}
