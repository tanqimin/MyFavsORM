package work.myfavs.framework.orm.util.id;

import work.myfavs.framework.orm.util.lang.Snowflake;

import java.util.UUID;

/**
 * 主键生成器
 *
 * @author tanqimin
 */
public class PKGenerator {

  private final Snowflake snowflake;

  /**
   * 主键生成器
   *
   * @param workerId     终端ID
   * @param dataCenterId 数据中心ID
   */
  public PKGenerator(long workerId, long dataCenterId) {
    snowflake = new Snowflake(workerId, dataCenterId);
  }

  /**
   * 获取 UUID 主键值
   *
   * @return UUID 主键值
   */
  public String nextUUID() {

    return UUID.randomUUID().toString();
  }

  /**
   * 获取 雪花 主键值
   *
   * @return 雪花主键值
   */
  public long nextSnowFakeId() {
    return snowflake.nextId();
  }
}
