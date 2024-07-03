package work.myfavs.framework.orm.meta.schema;

import org.junit.Test;
import work.myfavs.framework.orm.entity.SnowflakeExample;

public class MetadataTest {

  @Test
  public void get() {
    ClassMeta classMeta = Metadata.classMeta(SnowflakeExample.class);
    for (int i = 0; i < 10; i++) {
      //
      classMeta = Metadata.classMeta(SnowflakeExample.class);
    }

    System.out.println(classMeta);
  }
}
