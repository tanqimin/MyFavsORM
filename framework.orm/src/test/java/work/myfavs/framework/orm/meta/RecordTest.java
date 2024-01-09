package work.myfavs.framework.orm.meta;

import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.AbstractTest;
import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.entity.UuidExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class RecordTest extends AbstractTest {

  private Record record;

  @Before
  public void setUp() {
    record = Record.create();
  }

  @Test
  public void create() {
    assertNotNull(record);
  }

  @Test
  public void set() {
    record.clear();
    record.set("id", 1);
    assertEquals(record.get("id"), 1);
  }

  @Test
  public void get() {
    record.clear();
    record.set("id", 1);
    assertEquals(record.get("id"), 1);
  }

  @Test
  public void getBean() {
    record.clear();
    String      id   = UUID.randomUUID().toString();
    UuidExample uuid = new UuidExample();
    uuid.setId(id);
    record.set("uuid", uuid);
    assertEquals(record.<UuidExample>getBean("uuid").getId(), id);
  }

  @Test
  public void getObj() {
    record.clear();
    record.set("id", 1);
    assertEquals(record.getObj("id"), 1);
  }

  @Test
  public void getStr() {
    record.clear();
    record.set("str", "hello");
    assertEquals(record.getStr("str"), "hello");
  }

  @Test
  public void getInt() {
    record.clear();
    record.set("int", 1);
    assertEquals(record.getInt("int"), Integer.valueOf(1));
  }

  @Test
  public void getShort() {
    record.clear();
    record.set("short", 1);
    assertEquals(record.getShort("short"), Short.valueOf("1"));
  }

  @Test
  public void getBool() {
    record.clear();
    record.set("bool", Boolean.TRUE);
    assertEquals(record.getBool("bool"), Boolean.TRUE);
  }

  @Test
  public void getLong() {
    record.clear();
    long id = 121L;
    record.set("long", id);
    assertEquals(record.getLong("long"), Long.valueOf(id));
  }

  @Test
  public void getFloat() {
    record.clear();
    float value = 1.0f;
    record.set("float", value);
    assertEquals(record.getFloat("float"), Float.valueOf(value));
  }

  @Test
  public void getDouble() {
    record.clear();
    double value = 1.0d;
    record.set("double", value);
    assertEquals(record.getDouble("double"), Double.valueOf(value));
  }

  @Test
  public void getByte() {
    record.clear();
    Byte value = Byte.valueOf("1");
    record.set("byte", value);
    assertEquals(record.getByte("byte"), value);
  }

  @Test
  public void getBigDecimal() {
    record.clear();
    BigDecimal value = BigDecimal.ONE;
    record.set("bigDecimal", value);
    assertEquals(record.getBigDecimal("bigDecimal"), value);
  }

  @Test
  public void getEnum() {
    record.clear();
    TypeEnum value = TypeEnum.DRINK;
    record.set("enum", value);
    assertEquals(record.getEnum(TypeEnum.class, "enum"), value);
  }

  @Test
  public void getDate() {
    record.clear();
    Date value = new Date();
    record.set("date", value);
    assertEquals(record.getDate("date"), value);
  }

  @Test
  public void getBytes() {
    record.clear();
    byte[] bytes = new byte[0];
    record.set("bytes", bytes);
    assertEquals(record.getBytes("bytes"), bytes);
  }

  @Test
  public void toBean() {
    record.clear();
    record.set("id", UUID.randomUUID().toString());
    UuidExample uuid = record.toBean(new UuidExample());
    assertNotNull(uuid);
    assertEquals(uuid.getId(), record.get("id"));
    UuidExample uuid2 = record.toBean(UuidExample.class);
    assertNotNull(uuid2);
    assertEquals(uuid2.getId(), record.get("id"));
    record.set("NaMe", "Hello");
    UuidExample uuid3 = record.toBeanIgnoreCase(UuidExample.class);
    assertNotNull(uuid3);
    assertEquals(uuid3.getName(), record.get("NaMe"));
  }

  @Test
  public void toRecord() {
    record.clear();
    SnowflakeExample snowflake = new SnowflakeExample();
    snowflake.setId(123L);
    snowflake.setName("Hello");
    snowflake.setPrice(BigDecimal.ONE);
    Record record1 = Record.toRecord(snowflake);
    assertNotNull(record1);
    assertEquals(snowflake.getId(), record1.get("id"));
    assertEquals(snowflake.getName(), record1.get("name"));
    assertEquals(snowflake.getPrice(), record1.get("price"));
  }

  @Test
  public void testClone() {
    record.clear();
    record.set("id", 123L);
    Record clone = record.clone();
    assertEquals(clone.get("id"), record.get("id"));
  }
}
