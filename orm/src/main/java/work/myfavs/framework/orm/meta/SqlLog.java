package work.myfavs.framework.orm.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.Constant;

import java.util.*;

/**
 * SQL 日志记录器，用于输出 SQL 语句、参数和执行结果
 */
public class SqlLog {

  private static final Logger log = LoggerFactory.getLogger(SqlLog.class);

  private static final String TITLE_SQL = "---------------------- SQL语句 ----------------------";
  private static final String TITLE_PAR = "---------------------- SQL参数 ----------------------";
  private static final String TITLE_RES = "---------------------- 查询结果 ----------------------";

  private final boolean showSql;
  private final boolean showResult;

  /**
   * 构造 SQL 日志记录器
   *
   * @param showSql    是否输出 SQL 语句和参数
   * @param showResult 是否输出查询结果
   */
  public SqlLog(boolean showSql, boolean showResult) {
    this.showSql = showSql && log.isDebugEnabled();
    this.showResult = showResult && log.isDebugEnabled();
  }

  /**
   * 输出 SQL 语句
   *
   * @param sql SQL 语句
   */
  public void showSql(String sql) {
    if (!this.showSql) return;

    log.debug(TITLE_SQL.concat(Constant.LINE_SEPARATOR).concat(sql));
  }

  /**
   * 输出批量参数
   *
   * @param batchParameters {@link BatchParameters} 实例
   */
  public void showParams(BatchParameters batchParameters) {
    if (!this.showSql) return;
    if (null == batchParameters || batchParameters.isEmpty()) return;

    log.debug(TITLE_PAR);
    if (batchParameters.isBatch()) {
      for (Map.Entry<Integer, Parameters> entry : batchParameters.getBatchParameters().entrySet()) {
        Parameters parameters = entry.getValue();
        if (parameters.isEmpty()) continue;

        log.debug(format(parameters));
      }
      return;
    }

    Parameters parameters = batchParameters.getCurrentBatchParameters();
    if (parameters.isEmpty()) return;

    log.debug(format(parameters));
  }

  /**
   * 输出受影响行数
   *
   * @param result 受影响行数
   */
  public void showAffectedRows(int result) {
    if (!this.showResult) return;

    if (Math.abs(result) > 1) {
      log.debug("语句执行成功, {} 行受影响. ", result);
      return;
    }
    log.debug("语句执行成功. ");
  }

  /**
   * 输出查询结果列表
   *
   * @param viewClass 视图类型
   * @param result    查询结果列表
   * @param <TView>   视图类型
   */
  public <TView> void showResult(Class<TView> viewClass, List<TView> result) {
    if (!this.showResult) return;

    if (isRecord(viewClass)) {
      showRecords(result);
    } else if (isPrimitive(viewClass)) {
      showScalar(result);
    } else {
      showEntities(viewClass, result);
    }
    log.debug(String.format("查询执行成功, %d 行受影响. ", result.size()));
  }

  private static <TView> boolean isRecord(Class<TView> viewClass) {
    return viewClass == Record.class;
  }

  private static <TView> boolean isPrimitive(Class<TView> viewClass) {
    return viewClass.isPrimitive() || Constant.PRIMITIVE_TYPES.contains(viewClass);
  }

  /**
   * 输出格式化结果信息
   *
   * @param format   格式化字符串
   * @param arguments 格式化参数
   */
  public void showResult(String format, Object... arguments) {
    if (!this.showResult) return;
    log.debug(format, arguments);
  }

  private <TView> void showEntities(Class<TView> viewClass, List<TView> result) {
    ClassMeta             classMeta  = Metadata.classMeta(viewClass);
    Collection<Attribute> attributes = classMeta.getQueryAttributes().values();
    log.debug(TITLE_RES);
    log.debug(this.formatAttrName(attributes));
    for (TView tView : result) {
      log.debug(this.formatAttrValue(tView, attributes));
    }
  }

  private <TView> void showScalar(List<TView> result) {
    log.debug(TITLE_RES);
    for (TView tView : result) {
      log.debug(format(tView));
    }
  }

  private <TView> void showRecords(List<TView> result) {
    Iterator<TView> iterator = result.iterator();
    log.debug(TITLE_RES);
    if (iterator.hasNext()) {
      Record record = (Record) iterator.next();
      log.debug(this.formatRecordKeySet(record));
      log.debug(this.formatRecordValues(record));

      while (iterator.hasNext()) {
        record = (Record) iterator.next();
        log.debug(this.formatRecordValues(record));
      }
    }
  }

  private String format(Object param) {
    if (null == param) return "null";
    if (param instanceof Number) return param.toString();
    if (param instanceof Date) return String.format("'%s'", Constant.DATE_FORMATTER.format(param));
    if (param instanceof Parameters) return CollectionUtil.join(((Parameters) param).getParameters().values(), Constant.SYMBOL_COMMA, this::format);
    return String.format("'%s'", param);
  }

  private String formatAttrName(Collection<Attribute> attributes) {
    return CollectionUtil.join(attributes, Constant.SYMBOL_COMMA, Attribute::getColumnName);
  }

  private <TView> String formatAttrValue(TView tView, Collection<Attribute> attributes) {
    return CollectionUtil.join(attributes, Constant.SYMBOL_COMMA, attribute -> format(attribute.getValue(tView)));
  }

  private String formatRecordKeySet(Record record) {
    return CollectionUtil.join(record.keySet(), Constant.SYMBOL_COMMA, str -> str);
  }

  private String formatRecordValues(Record record) {
    return CollectionUtil.join(record.values(), Constant.SYMBOL_COMMA, this::format);
  }
}
