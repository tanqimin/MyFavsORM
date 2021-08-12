package work.myfavs.framework.orm.monitor;

import java.io.Serializable;

/** SQL 分析 */
public class SqlAnalysis implements Serializable {

  // region Attributes
  /** 执行消耗时间 */
  private long elapsed = -1L;
  /** 影响行数 */
  private int affectedRows = 0;
  /** 查询结果序列化消耗时间 */
  private long mappingElapsed = -1L;
  /** 是否出错？ */
  private boolean hasError = false;
  /** 错误/异常 */
  private Throwable throwable = null;
  // endregion

  // region Getter && Setter
  public long getElapsed() {

    return elapsed;
  }

  public void setElapsed(long elapsed) {

    this.elapsed = elapsed;
  }

  public int getAffectedRows() {

    return affectedRows;
  }

  public void setAffectedRows(int affectedRows) {

    this.affectedRows = affectedRows;
  }

  public long getMappingElapsed() {

    return mappingElapsed;
  }

  public void setMappingElapsed(long mappingElapsed) {

    this.mappingElapsed = mappingElapsed;
  }

  public boolean isHasError() {

    return hasError;
  }

  public void setHasError(boolean hasError) {

    this.hasError = hasError;
  }

  public Throwable getThrowable() {

    return throwable;
  }

  public void setThrowable(Throwable throwable) {

    this.throwable = throwable;
  }
  // endregion

}
