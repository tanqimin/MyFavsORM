package work.myfavs.framework.orm.meta.clause;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import lombok.Data;

@Data
public class Sql
    extends Clause
    implements Serializable {

  public Sql() {

  }

  public Sql(String sql) {

    super(sql);
  }

  public Sql(String sql,
             List params) {

    super(sql, params);
  }

  public Sql append(Sql sql) {

    this.sql.append(sql.sql);
    this.params.addAll(sql.params);
    return this;
  }

  public Sql append(String sql) {

    this.sql.append(sql);
    return this;
  }

  public Sql append(String sql,
                    Object param,
                    Object... params) {

    this.sql.append(sql);
    this.params.add(param);
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public Sql append(String sql,
                    List params) {

    this.sql.append(sql);
    this.params.addAll(params);
    return this;
  }

  public static Sql select(String sql) {

    return new Sql(StrUtil.format("SELECT {}", sql));
  }

  public Sql from(String tableName) {

    return this.append(StrUtil.format(" FROM {}", tableName));
  }

  public Sql from(String tableName,
                  String alias) {

    return this.append(StrUtil.format(" FROM {} {}", tableName, alias));
  }

  public Sql from(Sql sql,
                  String alias) {

    return this.append(StrUtil.format(" FROM ({}) {}", sql.sql, alias), sql.params);
  }

  public Sql from(Supplier<Sql> supplier,
                  String alias) {

    return this.from(supplier.get(), alias);
  }

  private Sql join(String tableName,
                   String alias,
                   String onClause) {

    return this.append(StrUtil.format(" JOIN {} {} ON {}", tableName, alias, onClause));
  }

  private Sql join(Sql sql,
                   String alias,
                   String onClause) {

    return this.append(StrUtil.format(" JOIN ({}) {} ON {}", sql.sql, alias, onClause), sql.params);
  }

  public Sql leftJoin(String tableName,
                      String alias,
                      String onClause) {

    return this.append(" LEFT")
               .join(tableName, alias, onClause);
  }

  public Sql leftJoin(Sql sql,
                      String alias,
                      String onClause) {

    return this.append(" LEFT")
               .join(sql, alias, onClause);
  }

  public Sql leftJoin(Supplier<Sql> supplier,
                      String alias,
                      String onClause) {

    return this.append(" LEFT")
               .join(supplier.get(), alias, onClause);
  }

  public Sql rightJoin(String tableName,
                       String alias,
                       String onClause) {

    return this.append(" RIGHT")
               .join(tableName, alias, onClause);
  }

  public Sql rightJoin(Sql sql,
                       String alias,
                       String onClause) {

    return this.append(" RIGHT")
               .join(sql, alias, onClause);
  }

  public Sql rightJoin(Supplier<Sql> supplier,
                       String alias,
                       String onClause) {

    return this.append(" RIGHT")
               .join(supplier.get(), alias, onClause);
  }

  public Sql innerJoin(String tableName,
                       String alias,
                       String onClause) {

    return this.append(" INNER")
               .join(tableName, alias, onClause);
  }

  public Sql innerJoin(Sql sql,
                       String alias,
                       String onClause) {

    return this.append(" INNER")
               .join(sql, alias, onClause);
  }

  public Sql innerJoin(Supplier<Sql> supplier,
                       String alias,
                       String onClause) {

    return this.append(" INNER")
               .join(supplier.get(), alias, onClause);
  }

  public Sql fullJoin(String tableName,
                      String alias,
                      String onClause) {

    return this.append(" FULL")
               .join(tableName, alias, onClause);
  }

  public Sql fullJoin(Sql sql,
                      String alias,
                      String onClause) {

    return this.append(" FULL")
               .join(sql, alias, onClause);
  }

  public Sql fullJoin(Supplier<Sql> supplier,
                      String alias,
                      String onClause) {

    return this.append(" FULL")
               .join(supplier.get(), alias, onClause);
  }

  public Sql where() {

    return this.append(" WHERE 1 = 1");
  }

  public Sql where(String sql) {

    return this.append(StrUtil.format(" WHERE {}", sql));
  }

  public Sql where(String sql,
                   List params) {

    return this.append(StrUtil.format(" WHERE {}", sql), params);
  }

  public Sql and(Cond cond) {

    return this.append(StrUtil.format(" AND {}", cond.sql), cond.params);
  }

  public Sql and(Supplier<Cond> supplier) {

    Cond cond = supplier.get();
    return this.append(StrUtil.format(" AND ({})", cond.sql), cond.params);
  }

  public Sql or(Cond cond) {

    return this.append(StrUtil.format(" OR {}", cond.sql), cond.params);
  }

  public Sql or(Supplier<Cond> supplier) {

    Cond cond = supplier.get();
    return this.append(StrUtil.format(" OR ({})", cond.sql), cond.params);
  }

  public Sql union() {

    return this.append(" UNION ");
  }

  public Sql unionAll() {

    return this.append(" UNION ALL ");
  }

  public Sql groupBy(String field,
                     String... fields) {

    this.append(StrUtil.format(" GROUP BY {}", field));
    if (fields != null && fields.length > 0) {
      for (int i = 0;
           i < fields.length;
           i++) {
        this.append(StrUtil.format(",{}", fields[i]));
      }
    }
    return this;
  }

  public Sql having() {

    return this.append(" HAVING 1 = 1");
  }

  public Sql having(String sql) {

    return this.append(StrUtil.format(" HAVING {}", sql));
  }

  public Sql having(String sql,
                    List params) {

    return this.append(StrUtil.format(" HAVING {}", sql), params);
  }

  public Sql orderBy(String field,
                     String... fields) {

    this.append(StrUtil.format(" ORDER BY {}", field));
    if (fields != null && fields.length > 0) {
      for (int i = 0;
           i < fields.length;
           i++) {
        this.append(StrUtil.format(",{}", fields[i]));
      }
    }
    return this;
  }

  public Sql limit(int row) {

    return this.append(StrUtil.format(" LIMIT {}", row));
  }

  public Sql limit(int offset,
                   int row) {

    return this.append(StrUtil.format(" LIMIT {},{}", offset, row));
  }

  public static Sql insert(String table,
                           String field,
                           String... fields) {

    Sql sql = new Sql(StrUtil.format("INSERT INTO {} ({}", table, field));
    if (fields != null && fields.length > 0) {
      for (int i = 0;
           i < fields.length;
           i++) {
        sql.append(StrUtil.format(",{}", fields[i]));
      }
    }
    return sql.append(")");
  }

  public static Sql update(String table) {

    return new Sql(StrUtil.format("UPDATE {}", table));
  }

  public Sql set(String field,
                 Object param) {

    return this.append(StrUtil.format(" SET {} = ?", field), param);
  }

  public Sql set(Map<String, Object> map) {

    if (map == null || map.size() == 0) {
      return this;
    }

    this.sql.append(" SET");
    for (Entry<String, Object> param : map.entrySet()) {
      this.sql.append(StrUtil.format(" {} = ?,", param.getKey()));
      this.params.add(param.getValue());
    }

    this.sql.deleteCharAt(this.sql.lastIndexOf(","));
    return this;
  }

}
