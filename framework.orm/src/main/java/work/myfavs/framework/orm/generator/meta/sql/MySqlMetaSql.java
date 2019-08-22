package work.myfavs.framework.orm.generator.meta.sql;

import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;

public class MySqlMetaSql
    extends MetaSql {

  @Override
  public Sql getSql() {

    /*
    select
      table_name as `table`,
      column_name as `column`,
      data_type as `type`,
      case is_nullable when 'YES' then 1 else 0 end as `nullable`,
      case column_key when 'PRI' then 1 else 0 end as `pk`,
      ordinal_position as `idx`,
      column_comment as `comment`
    from information_schema.`COLUMNS`
    where table_schema = 'myfavs_test'
     */

    Sql sql = Sql.Select("table_name AS `table`,")
                          .append(" column_name AS `column`,")
                          .append(" data_type AS `type`,")
                          .append(" CASE is_nullable WHEN 'YES' THEN 1 ELSE 0 END AS `nullable`,")
                          .append(" CASE column_key WHEN 'PRI' THEN 1 ELSE 0 END AS `pk`,")
                          .append(" ordinal_position AS `idx`,")
                          .append(" column_comment AS `comment`")
                          .from("information_schema.`COLUMNS`")
                          .where(Cond.eq("table_schema", super.dbName));
    return sql;
  }

  public MySqlMetaSql(String dbName) {

    super(dbName);
  }

}
