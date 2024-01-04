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

  private static final String LINE_SEPARATOR = System.lineSeparator();
  private static final String TITLE_SQL      = "---------------------- SQL --------------------------------";
  private static final String TITLE_PAR      = "---------------------- PARAMETERS -------------------------";
  private static final String TITLE_RES      = "---------------------- QUERY RESULTS ----------------------";

  private final boolean showSql;
  private final boolean showResult;

  public SqlLog(boolean showSql, boolean showResult) {
    this.showSql = showSql && log.isDebugEnabled();
    this.showResult = showResult && log.isDebugEnabled();
  }

  public void showSql(String sql) {
    if (!this.showSql) return;

    log.debug(TITLE_SQL.concat(LINE_SEPARATOR).concat(sql));
  }

  public void showParams(BatchParameters batchParameters) {
    if (!this.showSql) return;
    if (Objects.isNull(batchParameters) || batchParameters.isEmpty()) return;

    log.debug(TITLE_PAR);
    if (batchParameters.isBatch()) {
      for (Map.Entry<Integer, Parameters> entry : batchParameters.getBatchParameters().entrySet()) {
        Parameters parameters = entry.getValue();
        if (parameters.isEmpty()) continue;

        log.debug(format(parameters));
      }
    } else {
      Parameters parameters = batchParameters.getCurrentBatchParameters();
      if (parameters.isEmpty()) return;

      log.debug(format(parameters));
    }
  }

  public void showAffectedRows(int result) {
    if (!this.showResult) return;

    if (Math.abs(result) > 1)
      log.debug("Executed successfully, affected {} rows", result);
    else
      log.debug("Executed successfully.");
  }

  public <TView> void showResult(Class<TView> viewClass, List<TView> result) {
    if (!this.showResult) return;

    if (viewClass == Record.class) {
      showRecords(result);
    } else if (viewClass.isPrimitive() || Constant.PRIMITIVE_TYPES.contains(viewClass)) {
      showScalar(result);
    } else {
      showEntities(viewClass, result);
    }
    log.debug(String.format("Query results : %d rows", result.size()));
  }

  public void showResult(String format, Object... arguments) {
    if (!this.showResult) return;
    log.debug(format, arguments);
  }

  private <TView> void showEntities(Class<TView> viewClass, List<TView> result) {
    ClassMeta             classMeta  = Metadata.classMeta(viewClass);
    Collection<Attribute> attributes = classMeta.getQueryAttributes().values();
    log.debug(TITLE_RES);
    log.debug(this.formatAttribuiteName(attributes));
    for (TView tView : result) {
      log.debug(this.formatAttribuiteValue(tView, attributes));
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
    if (Objects.isNull(param)) return "null";
    if (param instanceof Number) return param.toString();
    if (param instanceof Date) return String.format("'%s'", Constant.DATE_FORMATTER.format(param));
    if (param instanceof Parameters) return CollectionUtil.join(((Parameters) param).getParameters().values(), ", ", this::format);
    return String.format("'%s'", param);
  }

  private String formatAttribuiteName(Collection<Attribute> attributes) {
    return CollectionUtil.join(attributes, ", ", Attribute::getColumnName);
  }

  private <TView> String formatAttribuiteValue(TView tView, Collection<Attribute> attributes) {
    return CollectionUtil.join(attributes, ", ", attribute -> format(attribute.getValue(tView)));
  }

  private String formatRecordKeySet(Record record) {
    return CollectionUtil.join(record.keySet(), ", ", str -> str);
  }

  private String formatRecordValues(Record record) {
    return CollectionUtil.join(record.values(), ", ", this::format);
  }
}
