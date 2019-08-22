package work.myfavs.framework.orm.generator.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class TemplateDto {

  private String              tableName;
  private String              className;
  private List<SchemaColumn>  schemaColumns = new ArrayList<>();
  private Map<String, String> typeMappers   = new HashMap<>();

}
