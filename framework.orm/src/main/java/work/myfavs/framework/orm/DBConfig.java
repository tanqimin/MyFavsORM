package work.myfavs.framework.orm;

import lombok.Getter;
import work.myfavs.framework.orm.meta.DbType;

import java.sql.Connection;

/**
 * 数据库配置
 *
 * @author tanqimin
 */
public class DBConfig {

  public static final String  DEFAULT_DATASOURCE_NAME = "default";
  /**
   * 数据库类型
   */
  @Getter
  private             String  dbType                  = DbType.MYSQL;
  /**
   * 一次批量插入数据的数量
   */
  @Getter
  private             int     batchSize               = 200;
  /**
   * 查询每次抓取数据的数量
   * -- GETTER --
   * 获取抓取数据大小
   *
   */
  @Getter
  private             int     fetchSize               = 1000;
  /**
   * 是否显示SQL
   */
  private             boolean showSql                 = false;
  /**
   * 是否显示查询结果
   */
  private             boolean showResult              = false;
  /**
   * 每页最大记录数
   * -- GETTER --
   * 获取分页时每页最大记录数
   *
   */
  @Getter
  private             int     maxPageSize             = -1;
  /**
   * 默认事务级别
   * -- GETTER --
   * 获取默认事务隔离级别
   *
   */
  @Getter
  private             int     defaultIsolation        = Connection.TRANSACTION_READ_COMMITTED;
  /**
   * 终端ID
   * -- GETTER --
   * 获取终端ID
   *
   */
  @Getter
  private             long    workerId                = 1L;
  /**
   * 数据中心ID
   * -- GETTER --
   * 获取数据中心ID
   *
   */
  @Getter
  private             long    dataCenterId            = 1L;

  /**
   * 分页查询结果数据集合字段名称
   * -- GETTER --
   * 获取分页查询结果数据集合字段名称
   *
   */
  @Getter
  private String pageDataField = "data";

  /**
   * 分页查询结果当前页码字段名称
   * -- GETTER --
   * 获取分页查询结果当前页码字段名称
   *
   */
  @Getter
  private String pageCurrentField = "currentPage";

  /**
   * 分页查询结果每页记录数字段名称
   * -- GETTER --
   * 获取分页查询结果每页记录数字段名称
   *
   */
  @Getter
  private String pageSizeField = "pageSize";

  /**
   * 分页查询结果总页数字段名称
   * -- GETTER --
   * 获取分页查询结果总页数字段名称
   *
   */
  @Getter
  private String pageTotalPageField = "totalPages";

  /**
   * 分页查询结果总记录数字段名称
   * -- GETTER --
   * 获取分页查询结果总记录数字段名称
   *
   */
  @Getter
  private String pageTotalRecordField = "totalRecords";

  /**
   * 分页查询结果是否存在下一页字段名称
   * -- GETTER --
   * 获取分页查询结果是否存在下一页字段名称
   *
   */
  @Getter
  private String pageHasNextField = "hasNext";

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
