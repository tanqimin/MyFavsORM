package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;
import work.myfavs.framework.orm.util.exception.DBException;

/** SQL 语句构建 注意：此处为了解决静态方法与普通方法不能重名的问题，所有静态方法均以大写字母开头 */
public class Sql extends Clause implements Serializable {

  // region Constructor

  /** 构造方法 */
  public Sql() {}

  /**
   * 构造方法
   *
   * @param sql SQL 语句
   */
  public Sql(String sql) {

    super(sql);
  }

  public Sql(String sql, Object param1, Object... params) {
    super(sql, toCollection(param1, params));
  }

  private static Collection<Object> toCollection(Object param1, Object... params) {
    Collection<Object> pars = new ArrayList<>();
    pars.add(param1);
    if (params != null) {
      Collections.addAll(pars, params);
    }
    return pars;
  }

  /**
   * 构造方法
   *
   * @param sql SQL 语句
   * @param params 参数
   */
  public Sql(String sql, Collection<?> params) {

    super(sql, params);
  }

  public Sql(Sql sql) {
    super(sql.toString(), sql.getParams());
  }
  // endregion

  public static Sql New(String sql) {

    return new Sql(sql);
  }

  public Sql addParam(Object param) {
    this.params.add(param);
    return this;
  }

  public Sql addParams(Collection<?> params) {
    this.params.addAll(params);
    return this;
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 对象
   * @return 拼接后的 SQL
   */
  public Sql append(Sql sql) {

    this.sql.append(sql.sql);
    this.params.addAll(sql.params);
    return this;
  }

  /**
   * 追加拼接 条件
   *
   * @param cond Cond对象
   * @return 拼接后的 SQL
   */
  public Sql append(Cond cond) {
    this.sql.append(cond.sql);
    this.params.addAll(cond.params);
    return this;
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 语句
   * @return 拼接后的 SQL
   */
  public Sql append(String sql) {

    this.sql.append(sql);
    return this;
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 语句
   * @param param 参数
   * @param params 更多的参数
   * @return 拼接后的 SQL
   */
  public Sql append(String sql, Object param, Object... params) {

    this.sql.append(sql);
    this.params.add(param);
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 语句
   * @param params 参数集合
   * @return 拼接后的 SQL
   */
  public Sql append(String sql, Collection<?> params) {

    this.sql.append(sql);
    this.params.addAll(params);
    return this;
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 对象
   * @return 拼接后的 SQL
   */
  public Sql appendLine(Sql sql) {

    return this.append(sql).append(System.lineSeparator());
  }

  /**
   * 追加拼接 条件
   *
   * @param cond Cond对象
   * @return 拼接后的 SQL
   */
  public Sql appendLine(Cond cond) {
    return this.append(cond).append(System.lineSeparator());
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 语句
   * @return 拼接后的 SQL
   */
  public Sql appendLine(String sql) {

    return this.append(sql).append(System.lineSeparator());
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 语句
   * @param param 参数
   * @param params 更多的参数
   * @return 拼接后的 SQL
   */
  public Sql appendLine(String sql, Object param, Object... params) {

    return this.append(sql, param, params).append(System.lineSeparator());
  }

  /**
   * 追加拼接 SQL
   *
   * @param sql SQL 语句
   * @param params 参数集合
   * @return 拼接后的 SQL
   */
  public Sql appendLine(String sql, Collection<?> params) {

    return this.append(sql, params).append(System.lineSeparator());
  }

  /**
   * 创建 SELECT * 语句
   *
   * @return SQL
   */
  public static Sql SelectAll() {

    return new Sql("SELECT *");
  }

  /**
   * 创建 SELECT {field}, {fields[1]}... 语句
   *
   * @param field 字段
   * @param fields 更多的字段
   * @return SQL
   */
  public static Sql Select(String field, String... fields) {

    Sql sql = new Sql(StrUtil.format("SELECT {}", field));
    if (fields != null) {
      for (String s : fields) {
        sql.append(StrUtil.format(",{}", s));
      }
    }
    return sql;
  }

  /**
   * 拼接 SELECT *
   *
   * @return SQL
   */
  public Sql selectAll() {

    this.append(" ").append(SelectAll());
    return this;
  }

  /**
   * 拼接 SELECT {field}, {fields[1]}... 语句
   *
   * @param field 字段
   * @param fields 更多字段
   * @return SQL
   */
  public Sql select(String field, String... fields) {

    this.append(StrUtil.format(" SELECT {}", field));
    if (fields != null) {
      for (String s : fields) {
        this.append(StrUtil.format(",{}", s));
      }
    }
    return this;
  }

  /**
   * 拼接 FROM 语句
   *
   * @param tableName 表名
   * @return SQL
   */
  public Sql from(String tableName) {

    return this.append(StrUtil.format(" FROM {}", tableName));
  }

  /**
   * 拼接 FROM 语句
   *
   * @param tableName 表名
   * @param alias 表别名
   * @return SQL
   */
  public Sql from(String tableName, String alias) {

    return this.append(StrUtil.format(" FROM {} {}", tableName, alias));
  }

  /**
   * 拼接 FROM 语句
   *
   * @param sql SQL
   * @param alias 表别名
   * @return SQL
   */
  public Sql from(Sql sql, String alias) {

    return this.append(StrUtil.format(" FROM ({}) {}", sql.sql, alias), sql.params);
  }

  /**
   * 拼接 FROM 语句
   *
   * @param supplier Supplier
   * @param alias 表别名
   * @return SQL
   */
  public Sql from(Supplier<Sql> supplier, String alias) {

    return this.from(supplier.get(), alias);
  }

  private Sql join(String tableName, String alias, String onClause) {

    return this.append(StrUtil.format(" JOIN {} {} ON {}", tableName, alias, onClause));
  }

  private Sql join(Sql sql, String alias, String onClause) {

    return this.append(StrUtil.format(" JOIN ({}) {} ON {}", sql.sql, alias, onClause), sql.params);
  }

  /**
   * 拼接 LEFT JOIN 语句
   *
   * @param tableName 表名
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql leftJoin(String tableName, String alias, String onClause) {

    return this.append(" LEFT").join(tableName, alias, onClause);
  }

  /**
   * 拼接 LEFT JOIN 语句
   *
   * @param sql SQL
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql leftJoin(Sql sql, String alias, String onClause) {

    return this.append(" LEFT").join(sql, alias, onClause);
  }

  /**
   * 拼接 LEFT JOIN 语句
   *
   * @param supplier Supplier
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql leftJoin(Supplier<Sql> supplier, String alias, String onClause) {

    return this.append(" LEFT").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 RIGHT JOIN 语句
   *
   * @param tableName 表名
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql rightJoin(String tableName, String alias, String onClause) {

    return this.append(" RIGHT").join(tableName, alias, onClause);
  }

  /**
   * 拼接 RIGHT JOIN 语句
   *
   * @param sql SQL
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql rightJoin(Sql sql, String alias, String onClause) {

    return this.append(" RIGHT").join(sql, alias, onClause);
  }

  /**
   * 拼接 RIGHT JOIN 语句
   *
   * @param supplier Supplier
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql rightJoin(Supplier<Sql> supplier, String alias, String onClause) {

    return this.append(" RIGHT").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 INNER JOIN 语句
   *
   * @param tableName 表名
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql innerJoin(String tableName, String alias, String onClause) {

    return this.append(" INNER").join(tableName, alias, onClause);
  }

  /**
   * 拼接 INNER JOIN 语句
   *
   * @param sql SQL
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql innerJoin(Sql sql, String alias, String onClause) {

    return this.append(" INNER").join(sql, alias, onClause);
  }

  /**
   * 拼接 INNER JOIN 语句
   *
   * @param supplier Supplier
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql innerJoin(Supplier<Sql> supplier, String alias, String onClause) {

    return this.append(" INNER").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 FULL JOIN 语句
   *
   * @param tableName 表名
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql fullJoin(String tableName, String alias, String onClause) {

    return this.append(" FULL").join(tableName, alias, onClause);
  }

  /**
   * 拼接 FULL JOIN 语句
   *
   * @param sql SQL
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql fullJoin(Sql sql, String alias, String onClause) {

    return this.append(" FULL").join(sql, alias, onClause);
  }

  /**
   * 拼接 FULL JOIN 语句
   *
   * @param supplier Supplier
   * @param alias 表别名
   * @param onClause ON语句
   * @return SQL
   */
  public Sql fullJoin(Supplier<Sql> supplier, String alias, String onClause) {

    return this.append(" FULL").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 WHERE 1 = 1 语句
   *
   * @return SQL
   */
  public Sql where() {

    return this.append(" WHERE 1 = 1");
  }

  /**
   * 拼接 WHERE {cond} 语句
   *
   * @param cond Cond
   * @return SQL
   */
  public Sql where(Cond cond) {

    return this.append(StrUtil.format(" WHERE{}", cond.sql), cond.params);
  }

  /**
   * 拼接 WHERE {sql} 语句
   *
   * @param sql SQL语句
   * @return SQL
   */
  public Sql where(String sql) {

    return this.append(StrUtil.format(" WHERE {}", sql));
  }

  /**
   * 拼接 WHERE {sql} 语句
   *
   * @param sql SQL 语句
   * @param params 参数
   * @return SQL
   */
  public Sql where(String sql, Collection<?> params) {

    return this.append(StrUtil.format(" WHERE {}", sql), params);
  }

  /**
   * 拼接 AND {cond} 语句
   *
   * @param cond Cond
   * @return SQL
   */
  public Sql and(Cond cond) {

    if (StrUtil.isBlankIfStr(cond.sql)) {
      return this;
    }
    this.append(StrUtil.format(" AND{}", cond.sql), cond.params);
    return this;
  }

  /**
   * 拼接 AND ({supplier}) 语句
   *
   * @param supplier Supplier
   * @return SQL
   */
  public Sql and(Supplier<Cond> supplier) {

    Cond cond = supplier.get();
    if (StrUtil.isBlankIfStr(cond.sql)) {
      return this;
    }
    this.append(StrUtil.format(" AND ({})", StrUtil.trimStart(cond.sql)), cond.params);
    return this;
  }

  /**
   * 拼接 OR {cond} 语句
   *
   * @param cond Cond
   * @return SQL
   */
  public Sql or(Cond cond) {

    if (StrUtil.isBlankIfStr(cond.sql)) {
      return this;
    }
    this.append(StrUtil.format(" OR{}", cond.sql), cond.params);
    return this;
  }

  /**
   * 拼接 OR ({supplier}) 语句
   *
   * @param supplier Supplier
   * @return SQL
   */
  public Sql or(Supplier<Cond> supplier) {

    Cond cond = supplier.get();
    if (StrUtil.isBlankIfStr(cond.sql)) {
      return this;
    }
    this.append(StrUtil.format(" OR ({})", StrUtil.trimStart(cond.sql)), cond.params);
    return this;
  }

  /**
   * 把当前Sql放入 SELECT * FROM (sql) alias 的子查询中
   *
   * @param alias 别名
   * @return SQL
   */
  public Sql asSubQuery(String alias) {
    this.sql.insert(0, "SELECT * FROM (").append(") ").append(alias);
    return this;
  }

  /**
   * 拼接 UNION 语句
   *
   * @return SQL
   */
  public Sql union() {

    return this.append(" UNION ");
  }

  /**
   * 拼接 UNION ALL 语句
   *
   * @return SQL
   */
  public Sql unionAll() {

    return this.append(" UNION ALL ");
  }

  /**
   * 拼接 GROUP BY {field}, {fields[1]}... 语句
   *
   * @param field 字段
   * @param fields 更多字段
   * @return SQL
   */
  public Sql groupBy(String field, String... fields) {

    this.append(StrUtil.format(" GROUP BY {}", field));
    if (fields != null) {
      for (String s : fields) {
        this.append(StrUtil.format(", {}", s));
      }
    }
    return this;
  }

  /**
   * 拼接 HAVING 1 = 1 语句
   *
   * @return SQL
   */
  public Sql having() {

    return this.append(" HAVING 1 = 1");
  }

  /**
   * 拼接 HAVING {sql} 语句
   *
   * @param sql SQL
   * @return SQL
   */
  public Sql having(String sql) {

    return this.append(StrUtil.format(" HAVING {}", sql));
  }

  /**
   * 拼接 HAVING {sql} 语句
   *
   * @param sql SQL语句
   * @param param 参数
   * @param params 参数数组
   * @return SQL
   */
  public Sql having(String sql, Object param, Object... params) {

    return this.append(StrUtil.format(" HAVING {}", sql), param, params);
  }

  public Sql having(Cond cond) {

    return this.append(" HAVING").append(cond);
  }

  /**
   * 拼接 ORDER BY {field}
   *
   * @param field 字段，可包含排序方法，如 code DESC
   * @return SQL
   */
  public Sql orderBy(String field) {
    final String orderByField = checkInjection(field);
    if (StrUtil.isNotEmpty(orderByField)) {
      this.append(StrUtil.format(" ORDER BY {}", orderByField));
    }
    return this;
  }

  /**
   * 拼接 ORDER BY {field}, {fields[1]}... 语句
   *
   * @param field 字段，可包含排序方法，如 code DESC
   * @param fields 更多字段
   * @return SQL
   */
  public Sql orderBy(String field, String... fields) {

    if (StrUtil.isEmpty(field)) throw new DBException("参数 field 不能为空");

    final String orderByField = checkInjection(field);

    this.append(StrUtil.format(" ORDER BY {}", orderByField));
    if (fields != null) {
      for (String s : fields) {
        this.append(StrUtil.format(", {}", checkInjection(s)));
      }
    }
    return this;
  }

  /**
   * 拼接 LIMIT {row} 语句
   *
   * @param row 返回行数
   * @return SQL
   */
  public Sql limit(int row) {
    final String rowStr = checkInjection(StrUtil.toString(row));
    return this.append(StrUtil.format(" LIMIT {}", rowStr));
  }

  /**
   * 拼接 LIMIT {offset} {row} 语句
   *
   * @param offset 起始记录偏移量
   * @param row 返回行数
   * @return SQL
   */
  public Sql limit(int offset, int row) {
    final String offsetStr = checkInjection(StrUtil.toString(offset));
    final String rowStr = checkInjection(StrUtil.toString(row));
    return this.append(StrUtil.format(" LIMIT {}, {}", offsetStr, rowStr));
  }

  /**
   * 创建 INSERT INT {table} ({field}, {fields[1]...}) 语句
   *
   * @param table 表名
   * @param field 字段
   * @param fields 更多字段
   * @return SQL
   */
  public static Sql Insert(String table, String field, String... fields) {

    Sql sql = new Sql(StrUtil.format("INSERT INTO {} ({}", table, field));
    if (fields != null) {
      for (String s : fields) {
        sql.append(StrUtil.format(", {}", s));
      }
    }
    return sql.append(")");
  }

  /**
   * 拼接 VALUES ({param}, {param[1]}...) 语句
   *
   * @param param 参数
   * @param params 更多参数
   * @return SQL
   */
  public Sql values(Object param, Object... params) {

    this.append(StrUtil.format(" VALUES (?"), param);
    if (params != null) {
      for (Object o : params) {
        this.append(StrUtil.format(", ?"), o);
      }
    }
    return this.append(")");
  }

  /**
   * 创建 UPDATE {table} 语句
   *
   * @param table 表名
   * @return SQL
   */
  public static Sql Update(String table) {

    return new Sql(StrUtil.format("UPDATE {}", table));
  }

  /**
   * 拼接 SET {表达式} 语句
   *
   * @param expression 表达式
   * @return SQL
   */
  public Sql set(String expression) {

    return this.append(StrUtil.format(" SET {}", expression));
  }

  /**
   * 拼接 SET {field} = ? 语句
   *
   * @param field 字段名
   * @param param 参数
   * @return SQL
   */
  public Sql set(String field, Object param) {

    return this.append(StrUtil.format(" SET {} = ?", field), param);
  }

  /**
   * 创建 DELETE {table} 语句
   *
   * @param table 表名称
   * @return SQL
   */
  public static Sql Delete(String table) {

    return new Sql(StrUtil.format("DELETE FROM {}", table));
  }

  /**
   * 创建 DELETE {alias} FROM {table} {alias} 语句
   *
   * @param table 表名称
   * @param alias 表别名
   * @return SQL
   */
  public static Sql Delete(String table, String alias) {

    return new Sql(StrUtil.format("DELETE {} FROM {} {}", alias, table, alias));
  }

  @Override
  public String toString() {

    return this.sql.toString();
  }
}
