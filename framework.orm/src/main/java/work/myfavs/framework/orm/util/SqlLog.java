package work.myfavs.framework.orm.util;

import cn.hutool.core.util.StrUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlLog {

  private boolean showSql;
  private boolean showResult;

  public SqlLog(boolean showSql, boolean showResult) {

    this.showSql = showSql;
    this.showResult = showResult;
  }

  public void showSql(String sql, List params) {

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
      stringBuilder.append(StrUtil.toString(param)).append(", ");
    }
    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
    return stringBuilder.toString();
  }

  public void showResult(ResultSet rs)
      throws SQLException {

    if (showResult && log.isDebugEnabled()) {
      ResultSetMetaData metaData;
      StringBuilder     logStr;
      int               columnCount;
      String            columnLabel;
      Object            columnVal;
      int               rows = 0;

      metaData = rs.getMetaData();
      columnCount = metaData.getColumnCount();

      logStr = new StringBuilder(System.lineSeparator());
      logStr.append(" QUERY RESULT:");
      logStr.append(System.lineSeparator());

      rs.beforeFirst();
      while (rs.next()) {
        rows++;
        logStr.append(StrUtil.format("ROW[{}]: {", rs.getRow()));
        for (int i = 1;
             i <= columnCount;
             i++) {
          columnLabel = metaData.getColumnLabel(i);
          columnVal = rs.getObject(i);
          if (rs.wasNull()) {
            logStr.append(StrUtil.format("\"{}\":null, ", columnLabel));
          } else {
            logStr.append(StrUtil.format("\"{}\":\"{}\", ", columnLabel, columnVal));
          }
        }
        logStr.deleteCharAt(logStr.lastIndexOf(", ")).append("}").append(System.lineSeparator());
      }
      logStr.append(StrUtil.format("TOTAL RECORDS: {}", rows));
      logStr.append(System.lineSeparator());
      log.info(logStr.toString());
    }
  }

  public void showBatchSql(String sql, List<List> paramsList) {

    if (showSql && log.isInfoEnabled()) {
      StringBuilder logStr = new StringBuilder(System.lineSeparator());
      logStr.append("          SQL: ").append(sql);
      if (paramsList != null && paramsList.size() > 0) {
        logStr.append(System.lineSeparator()).append("   PARAMETERS: ").append(System.lineSeparator());
        int i = 0;
        for (List params : paramsList) {
          logStr.append("PARAM[").append(i++).append("]: ");
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

}
