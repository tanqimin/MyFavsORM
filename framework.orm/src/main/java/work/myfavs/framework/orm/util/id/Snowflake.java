package work.myfavs.framework.orm.util.id;

import work.myfavs.framework.orm.util.exception.DBException;

import java.io.Serializable;

/**
 * Twitter的Snowflake 算法<br>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 *
 * <p>
 * snowflake的结构如下(每部分用-分开):<br>
 *
 * <pre>
 * 符号位（1bit）- 时间戳相对值（41bit）- 数据中心标志（5bit）- 机器标志（5bit）- 递增序号（12bit）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 * <p>
 * 第一位为未使用(符号位表示正数)，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
 * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
 * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * <p>
 * 并且可以通过生成的id反推出生成时间,datacenterId和workerId
 * <p>
 * 参考：http://www.cnblogs.com/relucent/p/4955340.html<br>
 * 关于长度是18还是19的问题见：https://blog.csdn.net/unifirst/article/details/80408050
 *
 * @author Looly
 * @since 3.0.1
 */
public class Snowflake implements Serializable {
  private static final long serialVersionUID = 1L;


  /**
   * 默认的起始时间，为Thu, 04 Nov 2010 01:42:54 GMT
   */
  public static long DEFAULT_TWEPOCH     = 1288834974657L;
  /**
   * 默认回拨时间，2S
   */
  public static long DEFAULT_TIME_OFFSET = 2000L;

  private static final long WORKER_ID_BITS       = 5L;
  // 最大支持机器节点数0~31，一共32个
  @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
  private static final long MAX_WORKER_ID        = -1L ^ (-1L << WORKER_ID_BITS);
  private static final long DATA_CENTER_ID_BITS  = 5L;
  // 最大支持数据中心节点数0~31，一共32个
  @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
  private static final long MAX_DATA_CENTER_ID   = -1L ^ (-1L << DATA_CENTER_ID_BITS);
  // 序列号12位（表示只允许workId的范围为：0-4095）
  private static final long SEQUENCE_BITS        = 12L;
  // 机器节点左移12位
  private static final long WORKER_ID_SHIFT      = SEQUENCE_BITS;
  // 数据中心节点左移17位
  private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
  // 时间毫秒数左移22位
  private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
  // 序列掩码，用于限定序列最大值不能超过4095
  private static final long SEQUENCE_MASK        = ~(-1L << SEQUENCE_BITS);// 4095

  /**
   * 初始化时间点
   */
  private final long    twepoch;
  private final long    workerId;
  private final long    dataCenterId;
  /**
   * 允许的时钟回拨毫秒数
   */
  private final long    timeOffset;


  /**
   * 自增序号，当高频模式下时，同一毫秒内生成N个ID，则这个序号在同一毫秒下，自增以避免ID重复。
   */
  private long sequence      = 0L;
  private long lastTimestamp = -1L;

  /**
   * 构造
   *
   * @param workerId     终端ID
   * @param dataCenterId 数据中心ID
   */
  public Snowflake(long workerId, long dataCenterId) {
    this.twepoch = DEFAULT_TWEPOCH;

    this.workerId = checkBetween(workerId, 0L, MAX_WORKER_ID);
    this.dataCenterId = checkBetween(dataCenterId, 0L, MAX_DATA_CENTER_ID);
    this.timeOffset = DEFAULT_TIME_OFFSET;
  }



  private static long checkBetween(long value, long min, long max) {
    if (value > min && value < max) return value;
    throw new DBException("Value must between %d and %d .", min, max);
  }

  /**
   * 根据Snowflake的ID，获取机器id
   *
   * @param id snowflake算法生成的id
   * @return 所属机器的id
   */
  public long getWorkerId(long id) {
    return id >> WORKER_ID_SHIFT & ~(-1L << WORKER_ID_BITS);
  }

  /**
   * 根据Snowflake的ID，获取数据中心id
   *
   * @param id snowflake算法生成的id
   * @return 所属数据中心
   */
  public long getDataCenterId(long id) {
    return id >> DATA_CENTER_ID_SHIFT & ~(-1L << DATA_CENTER_ID_BITS);
  }

  /**
   * 根据Snowflake的ID，获取生成时间
   *
   * @param id snowflake算法生成的id
   * @return 生成的时间
   */
  public long getGenerateDateTime(long id) {
    return (id >> TIMESTAMP_LEFT_SHIFT & ~(-1L << 41L)) + twepoch;
  }

  /**
   * 下一个ID
   *
   * @return ID
   */
  public synchronized long nextId() {
    long timestamp = genTime();
    if (timestamp < this.lastTimestamp) {
      if (this.lastTimestamp - timestamp < timeOffset) {
        // 容忍指定的回拨，避免NTP校时造成的异常
        timestamp = lastTimestamp;
      } else {
        // 如果服务器时间有问题(时钟后退) 报错。
        throw new IllegalStateException(String.format("Clock moved backwards. Refusing to generate id for %d ms", lastTimestamp - timestamp));
      }
    }

    if (timestamp == this.lastTimestamp) {
      final long sequence = (this.sequence + 1) & SEQUENCE_MASK;
      if (sequence == 0) {
        timestamp = tilNextMillis(lastTimestamp);
      }
      this.sequence = sequence;
    } else {
      sequence = 0L;
    }

    lastTimestamp = timestamp;

    return ((timestamp - twepoch) << TIMESTAMP_LEFT_SHIFT)
        | (dataCenterId << DATA_CENTER_ID_SHIFT)
        | (workerId << WORKER_ID_SHIFT)
        | sequence;
  }

  /**
   * 下一个ID（字符串形式）
   *
   * @return ID 字符串形式
   */
  public String nextIdStr() {
    return Long.toString(nextId());
  }

  // ------------------------------------------------------------------------------------------------------------------------------------ Private method start

  /**
   * 循环等待下一个时间
   *
   * @param lastTimestamp 上次记录的时间
   * @return 下一个时间
   */
  private long tilNextMillis(long lastTimestamp) {
    long timestamp = genTime();
    // 循环直到操作系统时间戳变化
    while (timestamp == lastTimestamp) {
      timestamp = genTime();
    }
    if (timestamp < lastTimestamp) {
      // 如果发现新的时间戳比上次记录的时间戳数值小，说明操作系统时间发生了倒退，报错
      throw new IllegalStateException(
          String.format("Clock moved backwards. Refusing to generate id for %d ms", lastTimestamp - timestamp));
    }
    return timestamp;
  }

  /**
   * 生成时间戳
   *
   * @return 时间戳
   */
  private long genTime() {
    return System.currentTimeMillis();
  }
  // ------------------------------------------------------------------------------------------------------------------------------------ Private method end
}
