package work.myfavs.framework.orm.meta;

import static org.junit.Assert.*;

import cn.hutool.core.util.IdUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.AbstractTest;
import work.myfavs.framework.orm.entity.Snowflake;
import work.myfavs.framework.orm.entity.Uuid;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

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
    String id = IdUtil.randomUUID();
    Uuid uuid = new Uuid();
    uuid.setId(id);
    record.set("uuid", uuid);
    assertEquals(record.<Uuid>getBean("uuid").getId(), id);
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
    long id = IdUtil.getSnowflakeNextId();
    record.set("long", id);
    assertEquals(record.getLong("long"), Long.valueOf(id));
  }

  @Test
  public void getChar() {
    record.clear();
    record.set("char", 'a');
    assertEquals(record.getChar("char"), Character.valueOf('a'));
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
  public void getBigInteger() {
    record.clear();
    BigInteger bigInteger = BigInteger.valueOf(1L);
    record.set("bigInteger", bigInteger);
    assertEquals(record.getBigInteger("bigInteger"), bigInteger);
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
  public void getLocalDateTime() {
    record.clear();
    LocalDateTime value = LocalDateTime.now();
    record.set("localDateTime", value);
    assertEquals(record.getLocalDateTime("localDateTime"), value);
  }

  @Test
  public void getBytes() {
    record.clear();
    byte[] bytes = new byte[0];
    record.set("bytes", bytes);
    assertEquals(record.getBytes("bytes"), bytes);
  }

  @Test
  public void getLocalDate() {
    record.clear();
    LocalDate value = LocalDate.now();
    record.set("localDate", value);
    assertEquals(record.getLocalDate("localDate"), value);
  }

  @Test
  public void getTime() {
    record.clear();
    Time value = Time.valueOf("12:00:00");
    record.set("time", value);
    assertEquals(record.getTime("time"), value);
  }

  @Test
  public void getTimestamp() {
    record.clear();
    Timestamp value = Timestamp.valueOf(LocalDateTime.now());
    record.set("timestamp", value);
    assertEquals(record.getTimestamp("timestamp"), value);
  }

  @Test
  public void getNumber() {
    record.clear();
    Number value = 1;
    record.set("number", value);
    assertEquals(record.getNumber("number"), value);
  }

  @Test
  public void toBean() {
    record.clear();
    record.set("id", IdUtil.randomUUID());
    Uuid uuid = record.toBean(new Uuid());
    assertNotNull(uuid);
    assertEquals(uuid.getId(), record.get("id"));
    Uuid uuid2 = record.toBean(Uuid.class);
    assertNotNull(uuid2);
    assertEquals(uuid2.getId(), record.get("id"));
    record.set("NaMe", "Hello");
    Uuid uuid3 = record.toBeanIgnoreCase(Uuid.class);
    assertNotNull(uuid3);
    assertEquals(uuid3.getName(), record.get("NaMe"));
  }

  @Test
  public void toRecord() {
    record.clear();
    Snowflake snowflake = new Snowflake();
    snowflake.setId(IdUtil.getSnowflakeNextId());
    snowflake.setName("Hello");
    snowflake.setPrice(BigDecimal.ONE);
    Record record1 = record.toRecord(snowflake);
    assertNotNull(record1);
    assertEquals(snowflake.getId(), record1.get("id"));
    assertEquals(snowflake.getName(), record1.get("name"));
    assertEquals(snowflake.getPrice(), record1.get("price"));
  }

  @Test
  public void testClone() {
    record.clear();
    record.set("id", IdUtil.getSnowflakeNextId());
    Record clone = record.clone();
    assertEquals(clone.get("id"), record.get("id"));
  }
}
