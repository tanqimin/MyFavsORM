package work.myfavs.framework.orm.meta.schema;

import org.junit.Test;
import work.myfavs.framework.orm.entity.SnowflakeExample;

import static org.junit.Assert.*;

public class MetadataTest {

  @Test
  public void shouldCacheAndReturnClassMeta() {
    ClassMeta classMeta = Metadata.classMeta(SnowflakeExample.class);
    assertNotNull(classMeta);
    assertEquals("tb_snowflake", classMeta.getTableName());
    assertNotNull(classMeta.getPrimaryKey());
    assertNotNull(classMeta.getQueryAttributes());
    assertFalse(classMeta.getQueryAttributes().isEmpty());

    // 验证缓存：重复获取同一实体元数据应返回相同实例
    ClassMeta cached = Metadata.classMeta(SnowflakeExample.class);
    assertSame(classMeta, cached);
  }
}
