package work.myfavs.framework.sb2.demo.util.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * 动态数据源上下文持有者，通过 {@link ThreadLocal} 维护当前线程的数据源名称.
 */
public class DynamicDataSourceContextHolder {
  private final static Logger logger = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

  /**
   * 存放当前线程使用的数据源类型信息
   */
  private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

  /**
   * 数据源使用顺序标识（仅首次设置时记录，用于 trace/调试）
   */
  private static final ThreadLocal<LinkedList<String>> dataSourceIds =
      ThreadLocal.withInitial(LinkedList::new);

  /**
   * 设置数据源
   *
   * @param dataSourceName 数据源名称
   */
  public static void setDataSource(String dataSourceName) {
    contextHolder.set(dataSourceName);
    dataSourceIds.get().add(dataSourceName);
  }

  /**
   * 获取数据源
   */
  public static String getDataSource() {
    if (null == contextHolder.get()) {
      logger.debug("数据源标识为空，使用默认的数据源");
    } else {
      logger.debug("使用数据源: {} 如果数据源不存在将使用默认数据源.", contextHolder.get());
    }
    return contextHolder.get();
  }

  /**
   * 清除数据源
   */
  public static void clearDataSource() {
    contextHolder.remove();
    dataSourceIds.remove();
  }

  /**
   * 返回上一次使用的数据源.
   * <p>若当前数据源层级为空，则清除上下文，由 {@link AbstractRoutingDataSource} 使用默认数据源。</p>
   */
  public static void returnDataSource() {
    LinkedList<String> ids = dataSourceIds.get();
    if (ids.size() <= 1) {
      clearDataSource();
      return;
    }
    ids.removeLast();
    setDataSource(ids.getLast());
  }
}
