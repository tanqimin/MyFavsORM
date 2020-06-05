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


  public PKGenerator(long workerId, long dataCenterId) {
    snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
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
   * @param workerId     终端ID(雪花算法生成主键用)
   * @param dataCenterId 数据中心ID(雪花算法生成主键用)
   * @return 雪花主键值
   */
  public long nextSnowFakeId() {
    return snowflake.nextId();
  }

}
