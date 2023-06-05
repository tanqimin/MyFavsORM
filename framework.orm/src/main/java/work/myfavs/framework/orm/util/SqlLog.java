package work.myfavs.framework.orm.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.DbType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.DBTemplate;

public class SqlLog {

  private static final Logger log = LoggerFactory.getLogger(SqlLog.class);

  private final boolean showSql;
  private final boolean showResult;

  private final DbType dbType;

  public SqlLog(DBTemplate dbTemplate) {
    showSql = dbTemplate.getDbConfig().getShowSql();
    showResult = dbTemplate.getDbConfig().getShowResult();
    dbType = DruidUtil.convert(dbTemplate.getDbConfig().getDbType());
  }

  public void showSql(String sql, Collection params) {

    if (showSql && log.isDebugEnabled()) {
      String logStr =
          System.lineSeparator()
              + "----------------- MYFAVS ORM SHOW SQL -----------------"
              + System.lineSeparator()
              + System.lineSeparator()
              + StrUtil.format("          SQL: {}", sql)
              + System.lineSeparator()
              + System.lineSeparator()
              + StrUtil.format("   PARAMETERS: {}", showParams(params))
              + System.lineSeparator()
              + System.lineSeparator()
              + "-------------------------------------------------------";
      log.debug(logStr);
    }
  }

  private String showParams(Collection params) {

    if (CollectionUtil.isEmpty(params)) {
      return "";
    }

    StringBuilder logStr = new StringBuilder();
    for (Object param : params) {
      logStr.append(StrUtil.format("{}, ", param));
    }
    logStr.deleteCharAt(logStr.lastIndexOf(","));
    return logStr.toString();
  }

  public void showBatchSql(String sql, Collection<Collection> paramsList) {

    if (showSql && log.isDebugEnabled()) {
      StringBuilder logStr = new StringBuilder();
      logStr.append(System.lineSeparator());
      logStr.append("----------------- MYFAVS ORM SHOW BATCH SQL -----------------");
      logStr.append(System.lineSeparator());
      logStr.append(System.lineSeparator());
      logStr.append(StrUtil.format("          SQL: {}", sql));
      logStr.append(System.lineSeparator());
      logStr.append(System.lineSeparator());
      if (paramsList != null && paramsList.size() > 0) {
        logStr.append("   PARAMETERS: ");
        logStr.append(System.lineSeparator());
        int i = 1;
        for (Iterator<Collection> iterator = paramsList.iterator(); iterator.hasNext(); ) {
          Collection params = iterator.next();
          logStr.append(StrUtil.format("PARAMETERS [{}]: {}", i++, showParams(params)));
          logStr.append(System.lineSeparator());
        }
        logStr.append(System.lineSeparator());
      }
      logStr.append("-------------------------------------------------------------");
      log.debug(logStr.toString());
    }
  }

  public void showAffectedRows(int result) {

    if (showResult && log.isDebugEnabled()) {
      String logStr =
          "----------------- MYFAVS ORM AFFECTED ROWS -----------------"
              + System.lineSeparator()
              + System.lineSeparator()
              + StrUtil.format("AFFECTED ROWS: {}", result)
              + System.lineSeparator()
              + System.lineSeparator()
              + "------------------------------------------------------------";
      log.debug(logStr);
    }
  }

  public <TView> void showResult(List<TView> result) {
    if (!showResult) {
      return;
    }
    if (!log.isDebugEnabled()) {
      return;
    }

    StringBuilder logStr = new StringBuilder();
    logStr.append(System.lineSeparator());
    logStr.append("----------------- MYFAVS ORM SHOW RESULT -----------------");
    logStr.append(System.lineSeparator());
    logStr.append(System.lineSeparator());
    logStr.append(" QUERY RESULT:");
    logStr.append(System.lineSeparator());
    logStr.append(System.lineSeparator());
    for (Iterator<TView> iterator = result.iterator(); iterator.hasNext(); ) {
      TView next = iterator.next();
      logStr.append(JSONUtil.toJsonStr(next));
      logStr.append(System.lineSeparator());
    }
    logStr.append(System.lineSeparator());
    logStr.append(StrUtil.format("TOTAL RECORDS: {}", result.size()));
    logStr.append(System.lineSeparator());
    logStr.append(System.lineSeparator());
    logStr.append("----------------------------------------------------------");
    log.debug(logStr.toString());
  }

  public <TView> void showResult(TView result) {
    if (!showResult) {
      return;
    }
    if (log.isDebugEnabled()) {
      return;
    }

    String logStr =
        System.lineSeparator()
            + "----------------- MYFAVS ORM SHOW RESULT -----------------"
            + System.lineSeparator()
            + System.lineSeparator()
            + " QUERY RESULT:"
            + JSONUtil.toJsonStr(result)
            + System.lineSeparator()
            + System.lineSeparator()
            + "----------------------------------------------------------";
    log.debug(logStr);
  }
}
