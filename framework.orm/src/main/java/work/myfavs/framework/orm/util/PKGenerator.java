package work.myfavs.framework.orm.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 主键生成器
 *
 * @author tanqimin
 */
public class PKGenerator {

  private static Snowflake snowflake;

  /**
   * 主键生成器
   *
   * @param workerId 终端ID
   * @param dataCenterId 数据中心ID
   */
  public PKGenerator(long workerId, long dataCenterId) {
    snowflake = IdUtil.getSnowflake(workerId, dataCenterId);
  }

  /**
   * 获取 UUID 主键值
   *
   * @return UUID 主键值
   */
  public String nextUUID() {

    return IdUtil.randomUUID();
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
