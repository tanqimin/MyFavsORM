package work.myfavs.framework.orm.repository.monitor;

import java.io.Serializable;
import lombok.Data;

/**
 * SQL 分析
 */
@Data
public class SqlAnalysis
    implements Serializable {

  /**
   * 执行消耗时间
   */
  private long      elapsed        = -1L;
  /**
   * 影响行数
   */
  private int       affectedRows   = 0;
  /**
   * 查询结果序列化消耗时间
   */
  private long      mappingElapsed = -1L;
  /**
   * 是否出错？
   */
  private boolean   hasError       = false;
  /**
   * 错误/异常
   */
  private Throwable throwable      = null;

}
