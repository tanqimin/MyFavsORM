package work.myfavs.framework.orm.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.Constant;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SqlLog {

  private static final Logger log = LoggerFactory.getLogger(SqlLog.class);

  private static final int TITLE_LENGTH = 55;

  private final boolean showSql;
  private final boolean showResult;

  public SqlLog(DBTemplate dbTemplate) {
    showSql = dbTemplate.getDbConfig().getShowSql();
    showResult = dbTemplate.getDbConfig().getShowResult();
  }

  public void showSql(String sql, Collection<?> params) {

    if (showSql && log.isDebugEnabled()) {
      showSql(sql);
      showParams(params);
    }
  }

  private void showSql(String sql) {
    log.debug(title("SQL").concat(System.lineSeparator()).concat(sql));
  }

  private String title(String title) {
    return StrUtil.padAfter(StrUtil.format("----- {} ", title), TITLE_LENGTH, "-");
  }

  private void showParams(Collection<?> params) {
    if (CollUtil.isNotEmpty(params)) {
      log.debug(title("PARAMETERS"));
      log.debug(CollUtil.join(params, ", ", this::format));
    }
  }

  private void showBatchParams(Collection<Collection<?>> batchParams) {
    if (CollUtil.isNotEmpty(batchParams)) {
      log.debug(title(StrUtil.format("TOTAL {} PARAMETERS", batchParams.size())));
      for (Collection<?> params : batchParams) {
        log.debug(CollUtil.join(params, ", ", this::format));
      }
    }
  }

  public void showBatchSql(String sql, Collection<Collection<?>> paramsList) {

    if (showSql && log.isDebugEnabled()) {
      showSql(sql);
      showBatchParams(paramsList);
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
      log.debug(title("RESULTS"));
      if (viewClass == Record.class) {
        showRecords(result);
      } else if (viewClass.isPrimitive() || Constant.PRIMITIVE_TYPES.contains(viewClass)) {
        showScalar(result);
      } else {
        showEntities(viewClass, result);
      }
      log.debug(StrUtil.format("Query results : {} rows", result.size()));
    }
    return result;
  }

  private <TView> void showEntities(Class<TView> viewClass, List<TView> result) {
    ClassMeta             classMeta  = Metadata.classMeta(viewClass);
    Collection<Attribute> attributes = classMeta.getQueryAttributes().values();
    log.debug(CollUtil.join(attributes, ", ", Attribute::getColumnName));
    for (TView tView : result) {
      log.debug(CollUtil.join(attributes, ", ", attribute -> getResultValue(tView, attribute)));
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
        log.debug(CollUtil.join(record.keySet(), ", "));
      }
      log.debug(CollUtil.join(record.values(), ", ", this::format));
    }
  }

  private String format(Object param) {
    if (Objects.isNull(param)) return "null";
    if (param instanceof Number) return param.toString();
    if (param instanceof Date) return StrUtil.format("'{}'", Constant.DATE_FORMATTER.format(param));
    return StrUtil.format("'{}'", param);
  }
}
