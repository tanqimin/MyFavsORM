package work.myfavs.framework.orm.util;


import java.util.UUID;

public class IdUtil {

  /**
   * 创建Twitter的Snowflake 算法生成器<br>
   * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
   *
   * <p>
   * snowflake的结构如下(每部分用-分开):<br>
   *
   * <pre>
   * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
   * </pre>
   * <p>
   * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
   * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
   * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
   *
   * <p>
   * 参考：http://www.cnblogs.com/relucent/p/4955340.html
   *
   * @param workerId     终端ID
   * @param datacenterId 数据中心ID
   *
   * @return {@link Snowflake}
   */
  public static Snowflake createSnowflake(long workerId, long datacenterId) {

    return new Snowflake(workerId, datacenterId);
  }

  public static String randomUUID() {

    return UUID.randomUUID().toString();
  }

}
