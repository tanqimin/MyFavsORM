package work.myfavs.framework.orm.generator.meta.column;

import work.myfavs.framework.orm.generator.GeneratorConfig;
import work.myfavs.framework.orm.generator.meta.GeneratorMeta;
import work.myfavs.framework.orm.generator.meta.TableDefinition;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;
import work.myfavs.framework.orm.generator.util.ResultSetUtil;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.sql.*;
import java.util.List;
import java.util.Map.Entry;

public class MySQLGeneratorMetaFactory extends GeneratorMetaFactory {

  private final GeneratorConfig generatorConfig;

  public MySQLGeneratorMetaFactory(GeneratorConfig generatorConfig) {

    this.generatorConfig = generatorConfig;
  }

  @Override
  public GeneratorMeta createGeneratorMeta() {

    GeneratorMeta generatorMeta = new GeneratorMeta();
    generatorMeta.getTypeMapper().putAll(generatorConfig.getTypeMapper());
    List<TableDefinition> tableDefinitions = generatorMeta.getTableDefinitions();

    String url  = generatorConfig.getJdbcUrl();
    String user = generatorConfig.getJdbcUser();
    String pwd  = generatorConfig.getJdbcPwd();

    Connection        conn = null;
    PreparedStatement ps   = null;
    ResultSet         rs   = null;

    try {
      conn = DriverManager.getConnection(url, user, pwd);
      String dbName = conn.getCatalog();
      Sql    sql    = getSql(dbName);
      ps = conn.prepareStatement(sql.toString());
      List<Object> params = sql.getParams();
      if (CollectionUtil.isNotEmpty(params)) {
        int pid = 1;
        for (Object param : params) {
          if (null == param)
            ps.setObject(pid, null);
          else
            //noinspection unchecked
            PropertyHandlerFactory.getInstance(param.getClass()).addParameter(ps, pid, param);
          pid++;
        }
      }
      rs = ps.executeQuery();

      while (rs.next()) {
        String table = ResultSetUtil.getString(rs, "table");

        TableDefinition  tableDefinition = getTableDefinition(table, tableDefinitions);
        ColumnDefinition column          = getColumnDefinition(rs);

        tableDefinition.getColumns().add(column);
      }

      return generatorMeta;
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      close(rs, ps, conn);
    }
  }

  private static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
    try {
      if (null != rs) {
        if (!rs.isClosed())
          rs.close();
      }
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      close(ps, conn);
    }
  }

  private static void close(PreparedStatement ps, Connection conn) {
    try {
      if (null != ps) {
        if (!ps.isClosed())
          ps.close();
      }
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      close(conn);
    }
  }

  private static void close(Connection conn) {
    try {
      if (null != conn) {
        if (!conn.isClosed())
          conn.close();
      }
    } catch (SQLException e) {
      throw new DBException(e);
    }
  }

  public Sql getSql(String dbName) {

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

    return Sql.Select("table_name AS `table`,")
              .append(" column_name AS `column`,")
              .append(" data_type AS `type`,")
              .append(" CASE is_nullable WHEN 'YES' THEN 1 ELSE 0 END AS `nullable`,")
              .append(" CASE column_key WHEN 'PRI' THEN 1 ELSE 0 END AS `pk`,")
              .append(" ordinal_position AS `idx`,")
              .append(" column_comment AS `comment`")
              .from("information_schema.`COLUMNS`")
              .where(Cond.eq("table_schema", dbName));
  }

  private TableDefinition getTableDefinition(String table, List<TableDefinition> tableDefinitions) {

    TableDefinition tableDefinition = null;
    for (TableDefinition definition : tableDefinitions) {
      if (StringUtil.equalsIgnoreCase(table, definition.getTableName())) {
        tableDefinition = definition;
        break;
      }
    }

    if (null == tableDefinition) {
      tableDefinition = new TableDefinition();
      tableDefinition.setTableName(table);
      tableDefinition.setClassName(GeneratorUtil.toClass(table));
      tableDefinitions.add(tableDefinition);
    }
    return tableDefinition;
  }

  private ColumnDefinition getColumnDefinition(ResultSet rs) throws SQLException {

    String  table    = ResultSetUtil.getString(rs, "table");
    String  column   = ResultSetUtil.getString(rs, "column");
    Boolean pk       = ResultSetUtil.getBoolean(rs, "pk");
    Boolean nullable = ResultSetUtil.getBoolean(rs, "nullable");
    Integer idx      = ResultSetUtil.getInt(rs, "idx");
    String  dataType = ResultSetUtil.getString(rs, "type");
    String  comment  = ResultSetUtil.getString(rs, "comment");

    ColumnDefinition columnDefinition = new ColumnDefinition();

    columnDefinition.setTable(table);
    columnDefinition.setColumn(column);
    columnDefinition.setPrimaryKey(pk);
    columnDefinition.setNullable(nullable);
    columnDefinition.setIndex(idx);
    columnDefinition.setDataType(dataType);
    columnDefinition.setTypeDefinition(createTypeDefinition(dataType, comment));
    columnDefinition.setComment(comment);
    return columnDefinition;
  }

  private TypeDefinition createTypeDefinition(String dataType, String comment) {

    if (null != comment) {
      if (comment.contains("#")) {
        String         className      = comment.split("#")[1];
        TypeDefinition typeDefinition = new TypeDefinition(className);
        generatorConfig.getTypeMapper().put(className, typeDefinition);
        return typeDefinition;
      }
    }

    for (Entry<String, TypeDefinition> entry : generatorConfig.getTypeMapper().entrySet()) {
      if (StringUtil.equalsIgnoreCase(dataType, entry.getKey())) {
        return entry.getValue();
      }
    }

    throw new DBException("类型 %s 未注册. ", dataType);
  }
}
