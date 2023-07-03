package work.myfavs.framework.orm.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.DBTemplate;

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
      log.debug(JsonUtil.toJsonStr(params));
      log.debug(System.lineSeparator());
    }
  }

  private void showBatchParams(Collection<Collection<?>> batchParams) {
    if (CollUtil.isNotEmpty(batchParams)) {
      log.debug(title(StrUtil.format("TOTAL {} PARAMETERS", batchParams.size())));
      int index = 1;
      for (Collection<?> batchParam : batchParams) {
        log.debug("P[{}]: {}", index++, JsonUtil.toJsonStr(batchParam));
      }
      log.debug(System.lineSeparator());
    }
  }

  public void showBatchSql(String sql, Collection<Collection<?>> paramsList) {

    if (showSql && log.isDebugEnabled()) {
      showSql(sql);
      showBatchParams(paramsList);
    }
  }

  public void showAffectedRows(int result) {

    if (showResult && log.isDebugEnabled()) {
      log.debug(title(StrUtil.format("AFFECTED ROWS: {}", result)).concat(System.lineSeparator()));
    }
  }

  public <TView> void showResult(List<TView> result) {
    if (showResult && log.isDebugEnabled()) {
      log.debug(title(StrUtil.format("QUERY RESULT: {} ROWS", result.size())));
      for (TView tView : result) {
        log.debug(JsonUtil.toJsonStr(tView));
      }
      log.debug(System.lineSeparator());
    }
  }
}
