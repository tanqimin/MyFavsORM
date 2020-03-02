package work.myfavs.framework.orm.generator.meta;

import java.util.ArrayList;
import java.util.List;
import work.myfavs.framework.orm.generator.meta.column.ColumnDefinition;

public class TableDefinition {

  private String                 tableName;
  private String                 className;
  private List<ColumnDefinition> columns = new ArrayList<>();

  public String getTableName() {

    return tableName;
  }

  public void setTableName(String tableName) {

    this.tableName = tableName;
  }

  public String getClassName() {

    return className;
  }

  public void setClassName(String className) {

    this.className = className;
  }

  public List<ColumnDefinition> getColumns() {

    return columns;
  }

  public void setColumns(List<ColumnDefinition> columns) {

    this.columns = columns;
  }

}
