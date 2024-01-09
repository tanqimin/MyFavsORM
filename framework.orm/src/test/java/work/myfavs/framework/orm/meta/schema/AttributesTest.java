package work.myfavs.framework.orm.meta.schema;

import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.entity.SnowflakeExample;

import java.util.Map;

public class AttributesTest {

  Map<String /* columnName */, Attribute> attributes;

  @Before
  public void setUp() {
    attributes = Metadata.classMeta(SnowflakeExample.class).getQueryAttributes();
  }

  @Test
  public void get() {}

  @Test
  public void columns() {
    for (String column : attributes.keySet()) {
      System.out.println(column);
    }
  }

  @Test
  public void put() {}

  @Test
  public void forEach() {}

  @Test
  public void computeIfAbsent() {}
}
