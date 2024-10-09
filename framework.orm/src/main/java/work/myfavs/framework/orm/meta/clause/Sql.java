package work.myfavs.framework.orm.meta.clause;


import lombok.NonNull;
import work.myfavs.framework.orm.meta.pagination.ISortable;
import work.myfavs.framework.orm.meta.pagination.Order;
import work.myfavs.framework.orm.util.common.*;
import work.myfavs.framework.orm.util.exception.DBException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import static work.myfavs.framework.orm.util.common.StringUtil.isBlank;

/**
 * SQL 语句构建 注意：此处为了解决静态方法与普通方法不能重名的问题，所有静态方法均以大写字母开头
 */
public class Sql extends Clause implements Serializable {
  public Sql() {
  }

  public Sql(@NonNull CharSequence sql) {
    super(sql);
  }

  public Sql(@NonNull CharSequence sql, Object param) {
    super(sql, param);
  }

  public Sql(@NonNull CharSequence sql, Collection<?> params) {
    super(sql, params);
  }

  /**
   * 构造方法
   *
   * @param sql {@link Sql}
   */
  public Sql(Sql sql) {
    this(sql.sql, sql.params);
  }

  /**
   * 构造方法
   *
   * @param sql    SQL 语句
   * @param param1 参数
   * @param params 更多参数
   */
  public Sql(CharSequence sql, Object param1, Object... params) {
    this(sql, CollectionUtil.toCollection(param1, params));
  }

  public static Sql New(String sql) {
    return new Sql(sql);
  }

  /**
   * 静态构造方法
   *
   * @param sql SQL 语句
   * @return {@link Sql}
   */
  public static Sql create(CharSequence sql) {
    return new Sql(sql);
  }

  /**
   * 静态构造方法
   *
   * @param sql    SQL 语句
   * @param params 参数
   */
  public static Sql create(CharSequence sql, Collection<?> params) {
    return new Sql(sql, params);
  }

  /**
   * 构造方法
   *
   * @param sql {@link Sql}
   */
  public static Sql create(Sql sql) {
    return new Sql(sql);
  }

  /**
   * 增加参数
   *
   * @param param 参数
   * @return {@link Sql}
   */
  public Sql addParam(Object param) {
    super.param(param);
    return this;
  }

  /**
   * 增加参数集合
   *
   * @param params {@link Collection} 参数集合
   * @return {@link Sql}
   */
  public Sql addParams(Collection<?> params) {
    super.params(params);
    return this;
  }

  /**
   * 拼接 SQL 语句
   *
   * @param sql SQL 语句
   * @return {@link Sql}
   */
  public Sql append(CharSequence sql) {
    super.concatWithSpace(sql);
    return this;
  }

  /**
   * 拼接 {@link Sql}
   *
   * @param sql {@link Sql}
   * @return {@link Sql}
   */
  public Sql append(Sql sql) {
    return this.append(sql.sql).addParams(sql.params);
  }

  /**
   * 拼接 {@link Cond}
   *
   * @param cond {@link Cond}
   * @return {@link Sql}
   */
  public Sql append(Cond cond) {
    return this.append(cond.sql).addParams(cond.params);
  }

  public Sql append(CharSequence sql, Object param) {
    return this.append(sql).addParam(param);
  }

  /**
   * 拼接 SQL 语句
   *
   * @param sql    SQL 语句
   * @param param1 参数
   * @param params 更多的参数
   * @return {@link Sql}
   */
  public Sql append(CharSequence sql, Object param1, Object... params) {
    return this.append(sql).addParam(param1).addParams(Arrays.asList(params));
  }

  /**
   * 拼接 SQL 语句
   *
   * @param sql    SQL 语句
   * @param params 参数集合
   * @return {@link Sql}
   */
  public Sql append(CharSequence sql, Collection<?> params) {
    return this.append(sql).addParams(params);
  }

  /**
   * 拼接换行符 {@link System#lineSeparator()}
   *
   * @return {@link Sql}
   */
  public Sql appendLine() {
    this.sql.append(Constant.LINE_SEPARATOR);
    return this;
  }

  /**
   * 拼接 {@link Sql} 并换行
   *
   * @param sql {@link Sql}
   * @return {@link Sql}
   */
  public Sql appendLine(Sql sql) {
    return this.append(sql).appendLine();
  }

  /**
   * 拼接 {@link Cond} 并换行
   *
   * @param cond {@link Cond}
   * @return {@link Sql}
   */
  public Sql appendLine(Cond cond) {
    return this.append(cond).appendLine();
  }

  /**
   * 追加拼接 SQL 语句并换行
   *
   * @param sql SQL 语句
   * @return {@link Sql}
   */
  public Sql appendLine(CharSequence sql) {
    return this.append(sql).appendLine();
  }

  /**
   * 拼接 SQL 语句并换行
   *
   * @param sql    SQL 语句
   * @param param1 参数
   * @param params 更多参数
   * @return {@link Sql}
   */
  public Sql appendLine(CharSequence sql, Object param1, Object... params) {
    return this.append(sql, param1, params).appendLine();
  }

  /**
   * 拼接 SQL 语句并换行
   *
   * @param sql    SQL 语句
   * @param params 参数集合
   * @return {@link Sql}
   */
  public Sql appendLine(CharSequence sql, Collection<?> params) {
    return this.append(sql, params).appendLine();
  }

  /**
   * 拼接 {@link Clause} 子查询
   *
   * @param sql {@link Clause} 子查询
   * @return {@link Sql}
   */
  public Sql appendSubQuery(Clause sql) {
    return this.append("(").append(sql.sql, sql.params).append(")");
  }

  /**
   * 拼接 {@link Sql} 子查询
   *
   * @param sql   {@link Sql} 子查询
   * @param alias 子查询别名
   * @return {@link Sql}
   */
  public Sql appendSubQuery(Clause sql, CharSequence alias) {
    return this.appendSubQuery(sql).append(alias);
  }

  /**
   * 拼接 {@link Sql} 子查询
   *
   * @param supplier {@link Supplier#get()} 子查询
   * @return {@link Sql}
   */
  public Sql appendSubQuery(Supplier<? extends Clause> supplier) {
    return this.appendSubQuery(supplier.get());
  }

  /**
   * 创建 SELECT * 语句
   *
   * @return {@link Sql}
   */
  public static Sql SelectAll() {
    return new Sql().selectAll();
  }

  /**
   * 创建 SELECT {field}, {fields[1]}... 语句
   *
   * @param field1 字段
   * @param fields 更多的字段
   * @return {@link Sql}
   */
  public static Sql Select(String field1, String... fields) {
    Sql sql = Sql.create("SELECT").append(field1);
    if (ArrayUtil.isEmpty(fields)) return sql;

    for (String field : fields) {
      sql.append(Constant.SYMBOL_COMMA).append(field);
    }
    return sql;
  }

  /**
   * 拼接 SELECT *
   *
   * @return {@link Sql}
   */
  public Sql selectAll() {
    return this.append("SELECT *");
  }

  /**
   * 拼接 SELECT {field}, {fields[1]}... 语句
   *
   * @param field1 字段
   * @param fields 更多字段
   * @return {@link Sql}
   */
  public Sql select(String field1, String... fields) {
    this.append("SELECT").append(field1);
    if (ArrayUtil.isEmpty(fields)) return this;

    for (String field : fields) {
      this.append(Constant.SYMBOL_COMMA).append(field);
    }
    return this;
  }

  /**
   * 拼接 FROM 语句
   *
   * @param tableName 表名
   * @return {@link Sql}
   */
  public Sql from(CharSequence tableName) {
    return this.append("FROM").append(tableName);
  }

  /**
   * 拼接 FROM 语句
   *
   * @param tableName 表名
   * @param alias     表别名
   * @return {@link Sql}
   */
  public Sql from(CharSequence tableName, CharSequence alias) {
    return this.from(tableName).append(alias);
  }

  /**
   * 拼接 FROM 语句
   *
   * @param sql   {@link Sql} 子查询
   * @param alias 表别名
   * @return {@link Sql}
   */
  public Sql from(Sql sql, CharSequence alias) {
    return this.append("FROM").appendSubQuery(sql, alias);
  }

  /**
   * 拼接 FROM 语句
   *
   * @param supplier {@link Supplier#get()} 子查询
   * @param alias    表别名
   * @return {@link Sql}
   */
  public Sql from(Supplier<Sql> supplier, CharSequence alias) {
    return this.from(supplier.get(), alias);
  }

  /**
   * 拼接 JOIN 语句
   *
   * @param tableName 表名
   * @param alias     表别名
   * @param onClause  ON子句
   * @return {@link Sql}
   */
  private Sql join(CharSequence tableName, CharSequence alias, CharSequence onClause) {
    return this.append("JOIN").append(tableName).append(alias).append("ON").append(onClause);
  }

  /**
   * 拼接 JOIN 语句
   *
   * @param sql      {@link Sql}子查询
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  private Sql join(Sql sql, CharSequence alias, CharSequence onClause) {
    return this.append("JOIN").appendSubQuery(sql, alias).append("ON").append(onClause);
  }

  /**
   * 拼接 LEFT JOIN 语句
   *
   * @param tableName 表名
   * @param alias     表别名
   * @param onClause  ON子句
   * @return {@link Sql}
   */
  public Sql leftJoin(CharSequence tableName, CharSequence alias, CharSequence onClause) {
    return this.append("LEFT").join(tableName, alias, onClause);
  }

  /**
   * 拼接 LEFT JOIN 语句
   *
   * @param sql      SQL
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql leftJoin(Sql sql, CharSequence alias, CharSequence onClause) {
    return this.append("LEFT").join(sql, alias, onClause);
  }

  /**
   * 拼接 LEFT JOIN 语句
   *
   * @param supplier Supplier
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql leftJoin(Supplier<Sql> supplier, CharSequence alias, CharSequence onClause) {
    return this.append("LEFT").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 RIGHT JOIN 语句
   *
   * @param tableName 表名
   * @param alias     表别名
   * @param onClause  ON子句
   * @return {@link Sql}
   */
  public Sql rightJoin(CharSequence tableName, CharSequence alias, CharSequence onClause) {
    return this.append("RIGHT").join(tableName, alias, onClause);
  }

  /**
   * 拼接 RIGHT JOIN 语句
   *
   * @param sql      SQL
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql rightJoin(Sql sql, CharSequence alias, CharSequence onClause) {
    return this.append("RIGHT").join(sql, alias, onClause);
  }

  /**
   * 拼接 RIGHT JOIN 语句
   *
   * @param supplier Supplier
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql rightJoin(Supplier<Sql> supplier, CharSequence alias, CharSequence onClause) {
    return this.append("RIGHT").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 INNER JOIN 语句
   *
   * @param tableName 表名
   * @param alias     表别名
   * @param onClause  ON子句
   * @return {@link Sql}
   */
  public Sql innerJoin(CharSequence tableName, CharSequence alias, CharSequence onClause) {
    return this.append("INNER").join(tableName, alias, onClause);
  }

  /**
   * 拼接 INNER JOIN 语句
   *
   * @param sql      SQL
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql innerJoin(Sql sql, CharSequence alias, CharSequence onClause) {
    return this.append("INNER").join(sql, alias, onClause);
  }

  /**
   * 拼接 INNER JOIN 语句
   *
   * @param supplier Supplier
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql innerJoin(Supplier<Sql> supplier, CharSequence alias, CharSequence onClause) {
    return this.append("INNER").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 FULL JOIN 语句
   *
   * @param tableName 表名
   * @param alias     表别名
   * @param onClause  ON子句
   * @return {@link Sql}
   */
  public Sql fullJoin(CharSequence tableName, CharSequence alias, CharSequence onClause) {
    return this.append("FULL").join(tableName, alias, onClause);
  }

  /**
   * 拼接 FULL JOIN 语句
   *
   * @param sql      SQL
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql fullJoin(Sql sql, CharSequence alias, CharSequence onClause) {
    return this.append("FULL").join(sql, alias, onClause);
  }

  /**
   * 拼接 FULL JOIN 语句
   *
   * @param supplier Supplier
   * @param alias    表别名
   * @param onClause ON子句
   * @return {@link Sql}
   */
  public Sql fullJoin(Supplier<Sql> supplier, CharSequence alias, CharSequence onClause) {
    return this.append("FULL").join(supplier.get(), alias, onClause);
  }

  /**
   * 拼接 WHERE 1 = 1 语句
   *
   * @return {@link Sql}
   */
  public Sql where() {
    return this.append("WHERE 1 = 1");
  }

  /**
   * 拼接 WHERE {cond} 语句
   *
   * @param cond Cond
   * @return {@link Sql}
   */
  public Sql where(Cond cond) {
    return this.append("WHERE").append(cond);
  }

  /**
   * 拼接 WHERE {condSql} 语句
   *
   * @param condSql SQL语句
   * @return {@link Sql}
   */
  public Sql where(CharSequence condSql) {
    return this.append("WHERE").append(condSql);
  }

  /**
   * 拼接 WHERE {sql} 语句
   *
   * @param sql    SQL 语句
   * @param params 参数
   * @return {@link Sql}
   */
  public Sql where(CharSequence sql, Collection<?> params) {
    return this.append("WHERE").append(sql, params);
  }

  /**
   * 拼接 AND {cond} 语句
   *
   * @param cond Cond
   * @return {@link Sql}
   */
  public Sql and(Cond cond) {
    if (cond.notBlank()) {
      this.append("AND").append(cond);
    }
    return this;
  }

  /**
   * 拼接 AND ({supplier}) 语句
   *
   * @param supplier Supplier
   * @return {@link Sql}
   */
  public Sql and(Supplier<Cond> supplier) {
    Cond cond = supplier.get();
    if (cond.notBlank()) {
      this.append("AND").appendSubQuery(supplier);
    }
    return this;
  }

  /**
   * 拼接 OR {cond} 语句
   *
   * @param cond Cond
   * @return {@link Sql}
   */
  public Sql or(Cond cond) {
    if (cond.notBlank()) {
      this.append("OR").append(cond);
    }
    return this;
  }

  /**
   * 拼接 OR ({supplier}) 语句
   *
   * @param supplier Supplier
   * @return {@link Sql}
   */
  public Sql or(Supplier<Cond> supplier) {
    Cond cond = supplier.get();
    if (cond.notBlank()) {
      this.append("OR").appendSubQuery(supplier);
    }
    return this;
  }

  /**
   * 把当前Sql放入 SELECT * FROM (sql) alias 的子查询中
   *
   * @param alias 别名
   * @return {@link Sql}
   */
  public Sql asSubQuery(CharSequence alias) {
    return Sql.SelectAll().from(this, alias);
  }

  /**
   * 拼接 UNION 语句
   *
   * @return {@link Sql}
   */
  public Sql union() {
    return this.append("UNION");
  }

  /**
   * 拼接 UNION ALL 语句
   *
   * @return {@link Sql}
   */
  public Sql unionAll() {

    return this.union().append("ALL");
  }

  /**
   * 拼接 GROUP BY {field1}, {fields[0]}... 语句
   *
   * @param field1 字段
   * @param fields 更多字段
   * @return {@link Sql}
   */
  public Sql groupBy(String field1, String... fields) {
    this.append("GROUP BY").append(field1);
    if (ArrayUtil.isEmpty(fields))
      return this;

    for (String field : fields) {
      this.append(Constant.SYMBOL_COMMA).append(field);
    }
    return this;
  }

  /**
   * 拼接 HAVING 1 = 1 语句
   *
   * @return {@link Sql}
   */
  public Sql having() {
    return this.append("HAVING 1 = 1");
  }

  /**
   * 拼接 HAVING {sql} 语句
   *
   * @param sql SQL
   * @return {@link Sql}
   */
  public Sql having(CharSequence sql) {
    return this.append("HAVING").append(sql);
  }

  /**
   * 拼接 HAVING {sql} 语句
   *
   * @param sql    SQL语句
   * @param param  参数
   * @param params 参数数组
   * @return {@link Sql}
   */
  public Sql having(CharSequence sql, Object param, Object... params) {
    return this.having(sql).addParam(param).addParams(Arrays.asList(params));
  }

  /**
   * 拼接 HAVING {cond} 语句
   *
   * @param cond {@link Cond}条件
   * @return {@link Sql}
   */
  public Sql having(Cond cond) {
    return this.having(cond.sql).addParams(cond.params);
  }

  /**
   * 拼接 ORDER BY {field}
   *
   * @param field 字段，可包含排序方法，如 code DESC
   * @return {@link Sql}
   */
  public Sql orderBy(String field) {
    if (StringUtil.isBlank(field))
      return this;

    return this.orderBy(Order.parse(field));
  }

  public Sql orderBy(Order order) {
    if (null == order)
      return this;
    return this.append("ORDER BY").append(order.getClause());
  }

  /**
   * 拼接 ORDER BY {field}, {fields[1]}... 语句
   *
   * @param sortable {@link ISortable} 排序条件基类
   * @return {@link Sql}
   */
  public Sql orderBy(ISortable sortable) {
    if (null == sortable)
      return this;

    List<Order> orders = sortable.getOrderBy();
    if (CollectionUtil.isEmpty(orders))
      return this;

    Iterator<Order> iterator = orders.iterator();
    this.orderBy(iterator.next());
    while (iterator.hasNext()) {
      this.append(Constant.SYMBOL_COMMA).append(iterator.next().getClause());
    }
    return this;
  }

  /**
   * 拼接 ORDER BY {field}, {fields[1]}... 语句
   *
   * @param field1 字段，可包含排序方法，如 code DESC
   * @param fields 更多字段
   * @return {@link Sql}
   */
  public Sql orderBy(String field1, String... fields) {

    this.append("ORDER BY").append(Order.parse(field1).getClause());

    if (ArrayUtil.isEmpty(fields))
      return this;

    for (String field : fields) {
      this.append(Constant.SYMBOL_COMMA).append(Order.parse(field).getClause());
    }
    return this;
  }

  /**
   * 拼接 LIMIT {row} 语句
   *
   * @param row 返回行数
   * @return {@link Sql}
   */
  public Sql limit(int row) {
    return this.append("LIMIT").append(StringUtil.toStr(row));
  }

  /**
   * 拼接 LIMIT {offset} {row} 语句
   *
   * @param offset 起始记录偏移量
   * @param row    返回行数
   * @return {@link Sql}
   */
  public Sql limit(int offset, int row) {
    return this.append("LIMIT").append(StringUtil.toStr(offset)).append(Constant.SYMBOL_COMMA).append(StringUtil.toStr(row));
  }

  /**
   * 创建 INSERT INT {table} ({field}, {fields[1]...}) 语句
   *
   * @param table  表名
   * @param field1 字段
   * @param fields 更多字段
   * @return {@link Sql}
   */
  public static Sql Insert(CharSequence table, String field1, String... fields) {
    return Sql.create("INSERT INTO").append(table).appendSubQuery(() -> {
      Sql fieldSql = Sql.create(field1);
      if (ArrayUtil.isNotEmpty(fields)) {
        for (String field : fields) {
          fieldSql.append(Constant.SYMBOL_COMMA).append(field);
        }
      }
      return fieldSql;
    });
  }

  /**
   * 拼接 VALUES ({param}, {param[1]}...) 语句
   *
   * @param param1 参数
   * @param params 更多参数
   * @return {@link Sql}
   */
  public Sql values(Object param1, Object... params) {
    return this.append("VALUES").appendSubQuery(() -> {
      Sql valuesSql = new Sql().append("?", param1);
      if (ArrayUtil.isNotEmpty(params)) {
        for (Object param : params) {
          valuesSql.append(Constant.SYMBOL_COMMA).append("?", param);
        }
      }
      return valuesSql;
    });
  }

  /**
   * 创建 UPDATE {table} 语句
   *
   * @param table 表名
   * @return {@link Sql}
   */
  public static Sql Update(CharSequence table) {
    return Sql.create("UPDATE").append(table);
  }

  /**
   * 拼接 SET {表达式} 语句
   *
   * @param expression 表达式
   * @return {@link Sql}
   */
  public Sql set(CharSequence expression) {
    return this.append("SET").append(expression);
  }

  /**
   * 拼接 SET {field} = ? 语句
   *
   * @param field 字段名
   * @param param 参数
   * @return {@link Sql}
   */
  public Sql set(CharSequence field, Object param) {
    return this.set(field).append("= ?", param);
  }

  /**
   * 创建 DELETE {table} 语句
   *
   * @param table 表名称
   * @return {@link Sql}
   */
  public static Sql Delete(CharSequence table) {
    return Sql.create("DELETE FROM").append(table);
  }

  /**
   * 创建 DELETE {alias} FROM {table} {alias} 语句
   *
   * @param table 表名称
   * @param alias 表别名
   * @return {@link Sql}
   */
  public static Sql Delete(CharSequence table, CharSequence alias) {
    return Sql.create("DELETE").append(alias).append("FROM").append(table).append(alias);
  }

  @Override
  public String toString() {
    return this.sql.toString();
  }
}
