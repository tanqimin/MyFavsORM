package work.myfavs.framework.orm.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlLog {

  private final static Logger log = LoggerFactory.getLogger(SqlLog.class);

  private boolean showSql;
  private boolean showResult;

  public SqlLog(boolean showSql,
                boolean showResult) {

    this.showSql = showSql;
    this.showResult = showResult;
  }

  public void showSql(String sql,
                      List params) {

    if (showSql && log.isDebugEnabled()) {
      StringBuilder logStr = new StringBuilder();
      logStr.append(System.lineSeparator());
      logStr.append(StrUtil.format("          SQL: {}", sql));
      logStr.append(System.lineSeparator());
      logStr.append(StrUtil.format("   PARAMETERS: {}", showParams(params)));
      logStr.append(System.lineSeparator());
      log.info(logStr.toString());
    }
  }

  private String showParams(List params) {

    StringBuilder stringBuilder;
    stringBuilder = new StringBuilder();
    if (params == null || params.size() == 0) {
      return stringBuilder.toString();
    }
    for (Object param : params) {
      stringBuilder.append(StrUtil.toString(param))
                   .append(", ");
    }
    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
    return stringBuilder.toString();
  }

  public void showBatchSql(String sql,
                           List<List> paramsList) {

    if (showSql && log.isInfoEnabled()) {
      StringBuilder logStr = new StringBuilder(System.lineSeparator());
      logStr.append("          SQL: ")
            .append(sql);
      if (paramsList != null && paramsList.size() > 0) {
        logStr.append(System.lineSeparator())
              .append("   PARAMETERS: ")
              .append(System.lineSeparator());
        int i = 0;
        for (List params : paramsList) {
          logStr.append("PARAM[")
                .append(i++)
                .append("]: ");
          logStr.append(StrUtil.format("{}", showParams(params)));
          logStr.append(System.lineSeparator());
        }
      }
      logStr.append(System.lineSeparator());
      log.info(logStr.toString());
    }
  }

  public void showAffectedRows(int result) {

    if (showResult && log.isInfoEnabled()) {
      log.info("AFFECTED ROWS: {}", result);
    }
  }

  public <TView> void showResult(List<TView> result) {

    if (showSql && log.isInfoEnabled()) {
      if (result != null && result.size() > 0) {
        StringBuilder logStr = new StringBuilder();
        logStr.append(" QUERY RESULT:");
        logStr.append(System.lineSeparator());
        for (Iterator<TView> iterator = result.iterator();
             iterator.hasNext(); ) {
          logStr.append(JSONUtil.parse(iterator.next())
                                .toString())
                .append(System.lineSeparator());
        }
        logStr.append(StrUtil.format("TOTAL RECORDS: {}", result.size()));
        logStr.append(System.lineSeparator());
        log.info(logStr.toString());
      }
    }

  }

}
