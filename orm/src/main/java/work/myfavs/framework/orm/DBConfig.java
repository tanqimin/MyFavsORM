package work.myfavs.framework.orm;

import work.myfavs.framework.orm.meta.DbType;

import java.sql.Connection;

/**
 * 数据库配置
 *
 * @author tanqimin
 */
public class DBConfig {

  public static final String DEFAULT_DATASOURCE_NAME = "default";

  /**
   * 数据库类型
   */
  private String dbType = DbType.MYSQL;

  /**
   * 一次批量插入数据的数量
   */
  private int batchSize = 200;

  /**
   * 查询每次抓取数据的数量
   */
  private int fetchSize = 1000;

  /**
   * 是否显示SQL
   */
  private boolean showSql = false;

  /**
   * 是否显示查询结果
   */
  private boolean showResult = false;

  /**
   * 每页最大记录数
   */
  private int maxPageSize = -1;

  /**
   * 默认事务级别
   */
  private int defaultIsolation = Connection.TRANSACTION_READ_COMMITTED;

  /**
   * 终端ID
   */
  private long workerId = 1L;

  /**
   * 数据中心ID
   */
  private long dataCenterId = 1L;

  /**
   * 分页查询结果数据集合字段名称
   */
  private String pageDataField = "data";

  /**
   * 分页查询结果当前页码字段名称
   */
  private String pageCurrentField = "currentPage";

  /**
   * 分页查询结果每页记录数字段名称
   */
  private String pageSizeField = "pageSize";

  /**
   * 分页查询结果总页数字段名称
   */
  private String pageTotalPageField = "totalPages";

  /**
   * 分页查询结果总记录数字段名称
   */
  private String pageTotalRecordField = "totalRecords";

  /**
   * 分页查询结果是否存在下一页字段名称
   */
  private String pageHasNextField = "hasNext";

  public String getDbType() {
    return dbType;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public int getFetchSize() {
    return fetchSize;
  }

  public boolean getShowSql() {
    return showSql;
  }

  public boolean getShowResult() {
    return showResult;
  }

  public int getMaxPageSize() {
    return maxPageSize;
  }

  public int getDefaultIsolation() {
    return defaultIsolation;
  }

  public long getWorkerId() {
    return workerId;
  }

  public long getDataCenterId() {
    return dataCenterId;
  }

  public String getPageDataField() {
    return pageDataField;
  }

  public String getPageCurrentField() {
    return pageCurrentField;
  }

  public String getPageSizeField() {
    return pageSizeField;
  }

  public String getPageTotalPageField() {
    return pageTotalPageField;
  }

  public String getPageTotalRecordField() {
    return pageTotalRecordField;
  }

  public String getPageHasNextField() {
    return pageHasNextField;
  }

  /**
   * 设置数据库类型
   *
   * @param dbType 数据库类型
   * @return 数据库配置
   */
  public DBConfig setDbType(String dbType) {

    this.dbType = dbType;
    return this;
  }

  /**
   * 设置批处理大小
   *
   * @param batchSize 批处理大小
   * @return 数据库配置
   */
  public DBConfig setBatchSize(int batchSize) {

    this.batchSize = batchSize;
    return this;
  }

  /**
   * 设置抓取数据大小
   *
   * @param fetchSize 抓取数据大小
   * @return 数据库配置
   */
  public DBConfig setFetchSize(int fetchSize) {

    this.fetchSize = fetchSize;
    return this;
  }

  /**
   * 设置是否显示SQL（日志级别INFO）
   *
   * @param showSql 是否显示SQL
   * @return 数据库配置
   */
  public DBConfig setShowSql(boolean showSql) {

    this.showSql = showSql;
    return this;
  }

  /**
   * 设置是否显示查询结果（日志级别INFO）
   *
   * @param showResult 是否显示查询结果
   * @return 数据库配置
   */
  public DBConfig setShowResult(boolean showResult) {

    this.showResult = showResult;
    return this;
  }

  /**
   * 设置分页时每页最大记录数(小于 0 为不限制)
   *
   * @param maxPageSize 分页时每页最大记录数
   * @return 数据库配置
   */
  public DBConfig setMaxPageSize(int maxPageSize) {

    this.maxPageSize = maxPageSize;
    return this;
  }

  /**
   * 设置默认事务隔离级别
   *
   * @param defaultIsolation 事务隔离级别
   * @return 数据库配置
   */
  public DBConfig setDefaultIsolation(int defaultIsolation) {

    this.defaultIsolation = defaultIsolation;
    return this;
  }

  /**
   * 设置终端ID
   *
   * @param workerId 终端ID
   * @return 数据库配置
   */
  public DBConfig setWorkerId(long workerId) {

    this.workerId = workerId;
    return this;
  }

  /**
   * 设置数据中心ID
   *
   * @param dataCenterId 数据中心ID
   * @return 数据库配置
   */
  public DBConfig setDataCenterId(long dataCenterId) {

    this.dataCenterId = dataCenterId;
    return this;
  }

  /**
   * 设置分页查询结果数据集合字段名称
   *
   * @param pageDataField 分页查询结果数据集合字段名称
   * @return 数据库配置
   */
  public DBConfig setPageDataField(String pageDataField) {
    this.pageDataField = pageDataField;
    return this;
  }

  /**
   * 设置分页查询结果当前页码字段名称
   *
   * @param pageCurrentField 分页查询结果当前页码字段名称
   * @return 数据库配置
   */
  public DBConfig setPageCurrentField(String pageCurrentField) {
    this.pageCurrentField = pageCurrentField;
    return this;
  }

  /**
   * 设置分页查询结果每页记录数字段名称
   *
   * @param pageSizeField 分页查询结果每页记录数字段名称
   * @return 数据库配置
   */
  public DBConfig setPageSizeField(String pageSizeField) {
    this.pageSizeField = pageSizeField;
    return this;
  }

  /**
   * 设置分页查询结果总页数字段名称
   *
   * @param pageTotalPageField 分页查询结果总页数字段名称
   * @return 数据库配置
   */
  public DBConfig setPageTotalPageField(String pageTotalPageField) {
    this.pageTotalPageField = pageTotalPageField;
    return this;
  }

  /**
   * 设置分页查询结果总记录数字段名称
   *
   * @param pageTotalRecordField 分页查询结果总记录数字段名称
   * @return 数据库配置
   */
  public DBConfig setPageTotalRecordField(String pageTotalRecordField) {
    this.pageTotalRecordField = pageTotalRecordField;
    return this;
  }

  /**
   * 设置分页查询结果是否存在下一页字段名称
   *
   * @param pageHasNextField 分页查询结果是否存在下一页字段名称
   * @return 数据库配置
   */
  public DBConfig setPageHasNextField(String pageHasNextField) {
    this.pageHasNextField = pageHasNextField;
    return this;
  }
}
