package work.myfavs.framework.example.util.tenant;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import work.myfavs.framework.example.domain.entity.Tenant;
import work.myfavs.framework.orm.util.common.StringUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

  private static Logger              logger            = LoggerFactory.getLogger(DynamicDataSource.class);
  private static Map<Object, Object> targetDataSources = new HashMap<>();
  public static  Properties          connectProperties = null;

  @Override
  protected Object determineCurrentLookupKey() {
    return DynamicDataSourceContextHolder.getDataSource();
  }

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
  public synchronized boolean addDataSource(Tenant tenant) {
    String name = tenant.getTenant();
    if (StringUtil.isBlank(name)) return false;
    if (isExistDataSource(name)) return true;

    DruidDataSource ds = new DruidDataSource();
    if (Objects.nonNull(DynamicDataSource.connectProperties))
      ds.setConnectProperties(DynamicDataSource.connectProperties);
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
