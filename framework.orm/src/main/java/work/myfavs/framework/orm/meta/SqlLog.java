package work.myfavs.framework.orm.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.Constant;

import java.util.*;

public class SqlLog {

  private static final Logger log = LoggerFactory.getLogger(SqlLog.class);

  private final boolean showSql;
  private final boolean showResult;

  public SqlLog(boolean showSql, boolean showResult) {
    this.showSql = showSql;
    this.showResult = showResult;
  }

  public void showSql(String sql) {
    if (!this.showSql) return;
    if (!log.isDebugEnabled()) return;
    log.debug("---------------------- SQL --------------------------------");
    log.debug(System.lineSeparator().concat(sql));
  }

  public void showParams(BatchParameters batchParameters) {
    if (!this.showSql) return;
    if (!log.isDebugEnabled()) return;
    if (Objects.isNull(batchParameters) || batchParameters.isEmpty()) return;


    if (batchParameters.isBatch()) {
      log.debug("---------------------- PARAMETERS -------------------------");
      for (Map.Entry<Integer, Parameters> entry : batchParameters.getBatchParameters().entrySet()) {
        Parameters parameters = entry.getValue();
        if (parameters.isEmpty()) continue;

        log.debug(CollectionUtil.join(parameters.getParameters().values(), ", ", this::format));
      }
    } else {
      Parameters parameters = batchParameters.getCurrentBatchParameters();
      if (parameters.isEmpty()) return;
      log.debug("---------------------- PARAMETERS -------------------------");
      log.debug(CollectionUtil.join(parameters.getParameters().values(), ", ", this::format));
    }
  }

  public int showAffectedRows(int result) {

    if (showResult && log.isDebugEnabled()) {
      if (Math.abs(result) > 1)
        log.debug("Executed successfully, affected {} rows", result);
      else
        log.debug("Executed successfully.");
    }
    return result;
  }

  public <TView> List<TView> showResult(Class<TView> viewClass, List<TView> result) {
    if (showResult && log.isDebugEnabled()) {

      log.debug("---------------------- QUERY RESULTS ----------------------");
      if (viewClass == Record.class) {
        showRecords(result);
      } else if (viewClass.isPrimitive() || Constant.PRIMITIVE_TYPES.contains(viewClass)) {
        showScalar(result);
      } else {
        showEntities(viewClass, result);
      }
      log.debug(String.format("Query results : %d rows", result.size()));
    }
    return result;
  }

  private <TView> void showEntities(Class<TView> viewClass, List<TView> result) {
    ClassMeta             classMeta  = Metadata.classMeta(viewClass);
    Collection<Attribute> attributes = classMeta.getQueryAttributes().values();
    log.debug(CollectionUtil.join(attributes, ", ", Attribute::getColumnName));
    for (TView tView : result) {
      log.debug(CollectionUtil.join(attributes, ", ", attribute -> getResultValue(tView, attribute)));
    }
  }

  private <TView> String getResultValue(TView entity, Attribute attribute) {
    return format(attribute.getFieldVisitor().getValue(entity));
  }

  private <TView> void showScalar(List<TView> result) {
    for (TView tView : result) {
      log.debug(format(tView));
    }
  }

  private <TView> void showRecords(List<TView> result) {
    for (int i = 0; i < result.size(); i++) {
      Record record = (Record) result.get(i);
      if (i == 0) {
        log.debug(CollectionUtil.join(record.keySet(), ", ", str -> str));
      }
      log.debug(CollectionUtil.join(record.values(), ", ", this::format));
    }
  }

  private String format(Object param) {
    if (Objects.isNull(param)) return "null";
    if (param instanceof Number) return param.toString();
    if (param instanceof Date) return String.format("'%s'", Constant.DATE_FORMATTER.format(param));
    return String.format("'%s'", param);
  }
}
