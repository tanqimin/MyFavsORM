package work.myfavs.framework.orm.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.Constant;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        log.debug("{}{}{}", TITLE_SQL, Constant.LINE_SEPARATOR, sql);
    }

    /**
     * 输出批量参数
     *
     * @param batchParameters {@link BatchParameters} 实例
     */
    public void showParams(BatchParameters batchParameters) {
        if (!this.showSql) return;
        if (null == batchParameters || batchParameters.isEmpty()) return;

        final StringBuilder sb = new StringBuilder(256);
        sb.append(TITLE_PAR).append(Constant.LINE_SEPARATOR);

        if (batchParameters.isBatch()) {
            for (Map.Entry<Integer, Parameters> entry : batchParameters.getBatchParameters().entrySet()) {
                Parameters parameters = entry.getValue();
                if (parameters.isEmpty()) continue;
                sb.append(format(parameters)).append(Constant.LINE_SEPARATOR);
            }
        } else {
            Parameters parameters = batchParameters.getCurrentBatchParameters();
            if (!parameters.isEmpty()) {
                sb.append(format(parameters));
            }
        }

        log.debug(sb.toString());
    }

    /**
     * 输出参数内联的完整 SQL，将 {@code ?} 占位符替换为参数实际值.
     * 在 Query 执行阶段替代 {@link #showSql(String)} 和 {@link #showParams(BatchParameters)} 两个独立调用.
     *
     * @param sql             含 {@code ?} 占位符的 SQL 语句
     * @param batchParameters 参数集合
     */
    public void showCompleteSql(String sql, BatchParameters batchParameters) {
        if (!this.showSql) return;

        if (null == batchParameters || batchParameters.isEmpty()) {
            log.debug(sql);
            return;
        }

        if (batchParameters.isBatch()) {
            final StringBuilder sb = new StringBuilder(256);
            for (Map.Entry<Integer, Parameters> entry : batchParameters.getBatchParameters().entrySet()) {
                Parameters parameters = entry.getValue();
                if (parameters.isEmpty()) {
                    sb.append(sql).append(Constant.LINE_SEPARATOR);
                } else {
                    sb.append(inlineParams(sql, parameters)).append(Constant.LINE_SEPARATOR);
                }
            }
            log.debug(sb.toString());
        } else {
            Parameters parameters = batchParameters.getCurrentBatchParameters();
            if (parameters.isEmpty()) {
                log.debug(sql);
            } else {
                log.debug(inlineParams(sql, parameters));
            }
        }
    }

    /**
     * 输出受影响行数及执行耗时
     *
     * @param result  受影响行数
     * @param elapsed 执行耗时(ms)
     */
    public void showAffectedRows(int result, long elapsed) {
        if (!this.showResult) return;

        if (Math.abs(result) > 1) {
            log.debug("语句执行成功, {} 行受影响. (耗时: {} ms)", result, elapsed);
            return;
        }
        log.debug("语句执行成功. (耗时: {} ms)", elapsed);
    }

    /**
     * 以 CSV 格式输出查询结果列表，包含执行耗时和转换耗时
     *
     * @param viewClass      视图类型
     * @param result         查询结果列表
     * @param queryElapsed   查询执行耗时(ms)
     * @param convertElapsed ResultSet 转换耗时(ms)
     * @param <TView>        视图类型
     */
    public <TView> void showResult(Class<TView> viewClass, List<TView> result, long queryElapsed, long convertElapsed) {
        if (!this.showResult) return;

        if (isRecord(viewClass)) {
            showRecords(result);
        } else if (isPrimitive(viewClass)) {
            showScalar(result);
        } else {
            showEntities(viewClass, result);
        }
        log.debug("查询执行成功, 返回 {} 条记录. (执行耗时: {} ms, 转换耗时: {} ms)",
                result.size(), queryElapsed, convertElapsed);
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
     * @param format    格式化字符串
     * @param arguments 格式化参数
     */
    public void showResult(String format, Object... arguments) {
        if (!this.showResult) return;
        log.debug(format, arguments);
    }

    private <TView> void showEntities(Class<TView> viewClass, List<TView> result) {
        if (result.isEmpty()) return;

        ClassMeta classMeta = Metadata.classMeta(viewClass);
        Collection<Attribute> attributes = classMeta.getQueryAttributes().values();

        final StringBuilder sb = new StringBuilder(256);
        sb.append(TITLE_RES).append(Constant.LINE_SEPARATOR);
        sb.append(this.formatAttrName(attributes)).append(Constant.LINE_SEPARATOR);
        for (TView tView : result) {
            sb.append(this.formatAttrValue(tView, attributes)).append(Constant.LINE_SEPARATOR);
        }
        log.debug(sb.toString());
    }

    private <TView> void showScalar(List<TView> result) {
        if (result.isEmpty()) return;

        final StringBuilder sb = new StringBuilder(128);
        sb.append(TITLE_RES).append(Constant.LINE_SEPARATOR);
        for (TView tView : result) {
            sb.append(formatCsvValue(tView)).append(Constant.LINE_SEPARATOR);
        }
        log.debug(sb.toString());
    }

    private <TView> void showRecords(List<TView> result) {
        if (result.isEmpty()) return;

        final StringBuilder sb = new StringBuilder(128);
        sb.append(TITLE_RES).append(Constant.LINE_SEPARATOR);

        boolean firstRow = true;
        for (TView tView : result) {
            Record record = (Record) tView;
            if (firstRow) {
                firstRow = false;
                sb.append(this.formatRecordKeySet(record)).append(Constant.LINE_SEPARATOR);
            }
            sb.append(this.formatRecordValues(record)).append(Constant.LINE_SEPARATOR);
        }
        log.debug(sb.toString());
    }

    /**
     * 将参数值内联到 SQL 的 ? 占位符中，生成可直接执行的 SQL 字符串
     *
     * @param sql    SQL 语句（含 ? 占位符）
     * @param params {@link Parameters} 实例
     * @return 参数内联后的 SQL
     */
    private String inlineParams(String sql, Parameters params) {
        final Map<Integer, Object> paramMap = params.getParameters();
        if (paramMap.isEmpty()) return sql;

        final StringBuilder sb = new StringBuilder(sql.length() + 64);
        int paramIdx = 1;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '?') {
                Object value = paramMap.get(paramIdx);
                sb.append(format(value));
                paramIdx++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String format(Object param) {
        if (null == param) return "null";
        if (param instanceof Number) return param.toString();
        if (param instanceof Date) return "'" + Constant.DATE_FORMATTER.get().format(param) + "'";
        if (param instanceof Parameters)
            return CollectionUtil.join(((Parameters) param).getParameters().values(), Constant.SYMBOL_COMMA, this::format);
        return "'" + param + "'";
    }

    /**
     * 将值格式化为 CSV 单元格格式（对含逗号、双引号、换行符的值进行双引号转义）
     *
     * @param value 值
     * @return CSV 单元格字符串
     */
    private String formatCsvValue(Object value) {
        if (null == value) return "";
        String str;
        if (value instanceof Number) {
            str = value.toString();
        } else if (value instanceof Date) {
            str = Constant.DATE_FORMATTER.get().format(value);
        } else {
            str = value.toString();
        }
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    private String formatAttrName(Collection<Attribute> attributes) {
        return CollectionUtil.join(attributes, Constant.SYMBOL_COMMA, Attribute::getColumnName);
    }

    private <TView> String formatAttrValue(TView tView, Collection<Attribute> attributes) {
        return CollectionUtil.join(attributes, Constant.SYMBOL_COMMA, attribute -> formatCsvValue(attribute.getValue(tView)));
    }

    private String formatRecordKeySet(Record record) {
        return CollectionUtil.join(record.keySet(), Constant.SYMBOL_COMMA, str -> str);
    }

    private String formatRecordValues(Record record) {
        return CollectionUtil.join(record.values(), Constant.SYMBOL_COMMA, this::formatCsvValue);
    }
}
