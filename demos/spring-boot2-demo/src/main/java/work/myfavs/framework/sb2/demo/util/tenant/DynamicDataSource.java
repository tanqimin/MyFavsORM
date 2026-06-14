package work.myfavs.framework.sb2.demo.util.tenant;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.sb2.demo.domain.entity.Tenant;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

  private static final Logger              logger            = LoggerFactory.getLogger(DynamicDataSource.class);
  private static       Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();
  private static       Properties          connectProperties = null;

  /**
   * 获取共享连接属性.
   *
   * @return 连接属性
   */
  public static Properties getConnectProperties() {
    return connectProperties;
  }

  /**
   * 设置共享连接属性，用于将主数据源的连接属性复制给动态创建的数据源.
   *
   * @param properties 连接属性
   */
  public static void setConnectProperties(Properties properties) {
    connectProperties = properties;
  }

  /**
   * 确定当前数据源查找键.
   *
   * @return 当前数据源名称
   */
  @Override
  protected Object determineCurrentLookupKey() {
    return DynamicDataSourceContextHolder.getDataSource();
  }

  /**
   * 设置目标数据源映射.
   *
   * @param targetDataSources 数据源映射
   */
  @Override
  public void setTargetDataSources(Map<Object, Object> targetDataSources) {
    super.setTargetDataSources(targetDataSources);
    DynamicDataSource.targetDataSources = targetDataSources;
  }

  /**
   * 是否存在当前key的 DataSource
   *
   * @param key datasourceKey
   * @return 存在返回 true, 不存在返回 false
   */
  public static boolean isExistDataSource(String key) {
    return targetDataSources.containsKey(key);
  }

  /**
   * 动态增加数据源
   *
   * @param tenant 租户
   * @return 成功返回 true, 失败返回 false
   */
  public boolean addDataSource(Tenant tenant) {
    String name = tenant.getTenant();
    if (StringUtil.isBlank(name)) return false;
    if (isExistDataSource(name)) return true;

    DruidDataSource ds = new DruidDataSource();
    if (null != DynamicDataSource.getConnectProperties())
      ds.setConnectProperties(DynamicDataSource.getConnectProperties());
    ds.setDriverClassName(tenant.getJdbcClass());
    ds.setUrl(tenant.getJdbcUrl());
    ds.setUsername(tenant.getJdbcUser());
    ds.setPassword(tenant.getJdbcPassword());

    try {
      ds.init();
    } catch (SQLException e) {
      logger.error(e.getMessage());
      return false;
    }

    Map<Object, Object> targetMap = DynamicDataSource.targetDataSources;
    targetMap.put(name, ds);
    this.afterPropertiesSet();
    logger.info("DataSource {} has been added.", name);
    return true;
  }
}
