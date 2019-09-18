package work.myfavs.framework.orm.util;

import cn.hutool.core.util.IdUtil;

public class PKGenerator {

  /**
   * 获取 UUID 主键值
   *
   * @return UUID 主键值
   */
  public static String nextUUID() {

    return IdUtil.randomUUID();
  }

  /**
   * 获取 雪花 主键值
   *
   * @param workerId     终端ID(雪花算法生成主键用)
   * @param dataCenterId 数据中心ID(雪花算法生成主键用)
   *
   * @return 雪花主键值
   */
  public static long nextSnowFakeId(long workerId, long dataCenterId) {

    return IdUtil.createSnowflake(workerId, dataCenterId).nextId();
  }

}
