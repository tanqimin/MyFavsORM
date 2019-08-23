package work.myfavs.framework.orm.generator.meta.column;

import java.sql.*;
import java.util.List;
import java.util.Map.Entry;
import work.myfavs.framework.orm.generator.GeneratorConfig;
import work.myfavs.framework.orm.generator.meta.GeneratorMeta;
import work.myfavs.framework.orm.generator.meta.TableDefinition;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.generator.meta.sql.MetaSql;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.ResultSetUtil;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

public class MySQLGeneratorMetaFactory
    extends GeneratorMetaFactory {

  private GeneratorConfig generatorConfig;

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
      Sql sql = MetaSql.get(conn);
      ps = DBUtil.getPs(conn, sql.getSql().toString(), sql.getParams());
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
      DBUtil.close(conn, ps, rs);
    }
  }

  private TableDefinition getTableDefinition(String table, List<TableDefinition> tableDefinitions) {

    TableDefinition tableDefinition = null;
    for (TableDefinition definition : tableDefinitions) {
      if (StringUtil.eq(table, definition.getTableName(), true)) {
        tableDefinition = definition;
        break;
      }
    }

    if (tableDefinition == null) {
      tableDefinition = new TableDefinition();
      tableDefinition.setTableName(table);
      tableDefinition.setClassName(GeneratorUtil.toClass(table));
      tableDefinitions.add(tableDefinition);
    }
    return tableDefinition;
  }


  private ColumnDefinition getColumnDefinition(ResultSet rs)
      throws SQLException {

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

    if (comment != null) {
      if (comment.contains("#")) {
        String         className      = comment.split("#")[1];
        TypeDefinition typeDefinition = new TypeDefinition(className);
        generatorConfig.getTypeMapper().put(className, typeDefinition);
        return typeDefinition;
      }
    }

    for (Entry<String, TypeDefinition> entry : generatorConfig.getTypeMapper().entrySet()) {
      if (StringUtil.eq(dataType, entry.getKey(), true)) {
        return entry.getValue();
      }
    }

    throw new DBException(StringUtil.format("未注册类型 {}", dataType));
  }

}
