package work.myfavs.framework.orm.meta.schema;

import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.entity.SnowflakeExample;

import java.util.Map;

import static org.junit.Assert.*;

public class AttributesTest {

  Map<String /* columnName */, Attribute> attributes;

  @Before
  public void setUp() {
    attributes = Metadata.classMeta(SnowflakeExample.class).getQueryAttributes();
  }

  @Test
  public void shouldContainAllEntityColumns() {
    assertNotNull(attributes);
    assertFalse(attributes.isEmpty());
    // ClassMeta 中以 columnName.toUpperCase() 作为 key 存储
    assertTrue(attributes.containsKey("ID"));
    assertTrue(attributes.containsKey("CREATED"));
    assertTrue(attributes.containsKey("NAME"));
    assertTrue(attributes.containsKey("DISABLE"));
    assertTrue(attributes.containsKey("PRICE"));
    assertTrue(attributes.containsKey("TYPE"));
    assertTrue(attributes.containsKey("CONFIG"));
    assertTrue(attributes.size() >= 7);
  }

  @Test
  public void shouldContainValidAttributeProperties() {
    Attribute idAttr = attributes.get("ID");
    assertNotNull(idAttr);
    assertEquals("id", idAttr.getColumnName());
    assertNotNull(idAttr.getFieldVisitor());
    assertEquals("id", idAttr.getFieldVisitor().getName());

    Attribute nameAttr = attributes.get("NAME");
    assertNotNull(nameAttr);
    assertEquals("name", nameAttr.getColumnName());
    assertNotNull(nameAttr.getFieldVisitor());
  }
}
