package work.myfavs.framework.orm;

import java.sql.Connection;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.dialect.DialectFactory;
import work.myfavs.framework.orm.meta.dialect.IDialect;

/**
 * 数据库配置
 *
 * @author tanqimin
 */
public class DBConfig {

  public static final String DEFAULT_DATASOURCE_NAME = "default";
  /** 数据库方言 */
  private IDialect dialect;
  /** 数据库类型 */
  private String dbType = DbType.MYSQL;
  /** 一次批量插入数据的数量 */
  private int batchSize = 200;
  /** 查询每次抓取数据的数量 */
  private int fetchSize = 1000;
  /** 查询超时时间，单位：秒 */
  private int queryTimeout = 60;
  /** 是否显示SQL */
  private boolean showSql = false;
  /** 是否显示查询结果 */
  private boolean showResult = false;
  /** 每页最大记录数 */
  private int maxPageSize = -1;
  /** 默认事务级别 */
  private int defaultIsolation = Connection.TRANSACTION_READ_COMMITTED;
  /** 终端ID */
  private long workerId = 1L;
  /** 数据中心ID */
  private long dataCenterId = 1L;

  /** 分页查询结果数据集合字段名称 */
  private String pageDataField = "data";

  /** 分页查询结果当前页码字段名称 */
  private String pageCurrentField = "currentPage";

  /** 分页查询结果每页记录数字段名称 */
  private String pageSizeField = "pageSize";

  /** 分页查询结果总页数字段名称 */
  private String pageTotalPageField = "totalPages";

  /** 分页查询结果总记录数字段名称 */
  private String pageTotalRecordField = "totalRecords";

  /** 分页查询结果是否存在下一页字段名称 */
  private String pageHasNextField = "hasNext";

  /**
   * 获取数据库方言
   *
   * @return 数据库方言
   */
  public IDialect getDialect() {

    if (this.dialect == null) {
      this.dialect = DialectFactory.getInstance(this.dbType);
    }
    return this.dialect;
  }

  /**
   * 获取数据库类型
   *
   * @return 数据库类型
   */
  public String getDbType() {

    return dbType;
  }

  /**
   * 设置数据库类型
   *
   * @param dbType 数据库类型
   * @return Configuration
   */
  public DBConfig setDbType(String dbType) {

    this.dbType = dbType;
    return this;
  }

  /**
   * 获取批处理大小
   *
   * @return 批处理大小
   */
  public int getBatchSize() {

    return batchSize;
  }

  /**
   * 设置批处理大小
   *
   * @param batchSize 批处理大小
   * @return Configuration
   */
  public DBConfig setBatchSize(int batchSize) {

    this.batchSize = batchSize;
    return this;
  }

  /**
   * 获取抓取数据大小
   *
   * @return 抓取数据大小
   */
  public int getFetchSize() {

    return fetchSize;
  }

  /**
   * 设置抓取数据大小
   *
   * @param fetchSize 抓取数据大小
   * @return Configuration
   */
  public DBConfig setFetchSize(int fetchSize) {

    this.fetchSize = fetchSize;
    return this;
  }

  /**
   * 获取查询超时时间
   *
   * @return 查询超时时间
   */
  public int getQueryTimeout() {

    return queryTimeout;
  }

  /**
   * 设置查询超时时间
   *
   * @param queryTimeout 查询超时时间
   * @return Configuration
   */
  public DBConfig setQueryTimeout(int queryTimeout) {

    this.queryTimeout = queryTimeout;
    return this;
  }

  /**
   * 获取是否显示SQL
   *
   * @return 是否显示SQL
   */
  public boolean getShowSql() {

    return showSql;
  }

  /**
   * 设置是否显示SQL（日志级别INFO）
   *
   * @param showSql 是否显示SQL
   * @return Configuration
   */
  public DBConfig setShowSql(boolean showSql) {

    this.showSql = showSql;
    return this;
  }

  /**
   * 获取是否显示查询结果
   *
   * @return 是否显示查询结果
   */
  public boolean getShowResult() {

    return showResult;
  }

  /**
   * 设置是否显示查询结果（日志级别INFO）
   *
   * @param showResult 是否显示查询结果
   * @return Configuration
   */
  public DBConfig setShowResult(boolean showResult) {

    this.showResult = showResult;
    return this;
  }

  /**
   * 获取分页时每页最大记录数
   *
   * @return 分页时每页最大记录数
   */
  public long getMaxPageSize() {

    return maxPageSize;
  }

  /**
   * 设置分页时每页最大记录数(小于 0 为不限制)
   *
   * @param maxPageSize 分页时每页最大记录数
   * @return Configuration
   */
  public DBConfig setMaxPageSize(int maxPageSize) {

    this.maxPageSize = maxPageSize;
    return this;
  }

  /**
   * 获取默认事务隔离级别
   *
   * @return int
   */
  public int getDefaultIsolation() {

    return this.defaultIsolation;
  }

  /**
   * 设置默认事务隔离级别
   *
   * @param defaultIsolation 事务隔离级别
   * @return Configuration
   */
  public DBConfig setDefaultIsolation(int defaultIsolation) {

    this.defaultIsolation = defaultIsolation;
    return this;
  }

  /**
   * 获取终端ID
   *
   * @return 终端ID
   */
  public long getWorkerId() {

    return workerId;
  }

  /**
   * 设置终端ID
   *
   * @param workerId 终端ID
   * @return Configuration
   */
  public DBConfig setWorkerId(long workerId) {

    this.workerId = workerId;
    return this;
  }

  /**
   * 获取数据中心ID
   *
   * @return 数据中心ID
   */
  public long getDataCenterId() {

    return dataCenterId;
  }

  /**
   * 设置数据中心ID
   *
   * @param dataCenterId 数据中心ID
   * @return Configuration
   */
  public DBConfig setDataCenterId(long dataCenterId) {

    this.dataCenterId = dataCenterId;
    return this;
  }

  /**
   * 获取分页查询结果数据集合字段名称
   *
   * @return 分页查询结果数据集合字段名称
   */
  public String getPageDataField() {
    return pageDataField;
  }

  /**
   * 设置分页查询结果数据集合字段名称
   *
   * @param pageDataField 分页查询结果数据集合字段名称
   * @return Configuration
   */
  public DBConfig setPageDataField(String pageDataField) {
    this.pageDataField = pageDataField;
    return this;
  }

  /**
   * 获取分页查询结果当前页码字段名称
   *
   * @return 分页查询结果当前页码字段名称
   */
  public String getPageCurrentField() {
    return pageCurrentField;
  }

  /**
   * 设置分页查询结果当前页码字段名称
   *
   * @param pageCurrentField 分页查询结果当前页码字段名称
   * @return Configuration
   */
  public DBConfig setPageCurrentField(String pageCurrentField) {
    this.pageCurrentField = pageCurrentField;
    return this;
  }

  /**
   * 获取分页查询结果每页记录数字段名称
   *
   * @return 分页查询结果每页记录数字段名称
   */
  public String getPageSizeField() {
    return pageSizeField;
  }

  /**
   * 设置分页查询结果每页记录数字段名称
   *
   * @param pageSizeField 分页查询结果每页记录数字段名称
   * @return Configuration
   */
  public DBConfig setPageSizeField(String pageSizeField) {
    this.pageSizeField = pageSizeField;
    return this;
  }

  /**
   * 获取分页查询结果总页数字段名称
   *
   * @return 分页查询结果总页数字段名称
   */
  public String getPageTotalPageField() {
    return pageTotalPageField;
  }

  /**
   * 设置分页查询结果总页数字段名称
   *
   * @param pageTotalPageField 分页查询结果总页数字段名称
   * @return Configuration
   */
  public DBConfig setPageTotalPageField(String pageTotalPageField) {
    this.pageTotalPageField = pageTotalPageField;
    return this;
  }

  /**
   * 获取分页查询结果总记录数字段名称
   *
   * @return 分页查询结果总记录数字段名称
   */
  public String getPageTotalRecordField() {
    return pageTotalRecordField;
  }

  /**
   * 设置分页查询结果总记录数字段名称
   *
   * @param pageTotalRecordField 分页查询结果总记录数字段名称
   * @return Configuration
   */
  public DBConfig setPageTotalRecordField(String pageTotalRecordField) {
    this.pageTotalRecordField = pageTotalRecordField;
    return this;
  }

  /**
   * 获取分页查询结果是否存在下一页字段名称
   *
   * @return 分页查询结果是否存在下一页字段名称
   */
  public String getPageHasNextField() {
    return pageHasNextField;
  }

  /**
   * 设置分页查询结果是否存在下一页字段名称
   *
   * @param pageHasNextField 分页查询结果是否存在下一页字段名称
   * @return Configuration
   */
  public DBConfig setPageHasNextField(String pageHasNextField) {
    this.pageHasNextField = pageHasNextField;
    return this;
  }
}
