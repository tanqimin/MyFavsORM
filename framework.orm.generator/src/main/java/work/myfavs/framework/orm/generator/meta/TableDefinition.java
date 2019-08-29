package work.myfavs.framework.orm.generator.meta;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import work.myfavs.framework.orm.generator.meta.column.ColumnDefinition;

@Data
public class TableDefinition {

  private String                 tableName;
  private String                 className;
  private List<ColumnDefinition> columns = new ArrayList<>();

}
