package work.myfavs.framework.orm.generator.meta.column;

import java.io.Serializable;
import lombok.Data;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;
import work.myfavs.framework.orm.util.StringUtil;

@Data
public class ColumnDefinition
    implements Serializable {

  /**
   * 表名
   */
  private String         table;
  /**
   * 字段名
   */
  private String         column;
  /**
   * 是否主键？
   */
  private Boolean        primaryKey;
  /**
   * 是否可为Null
   */
  private Boolean        nullable;
  /**
   * 排序ID
   */
  private Integer        index;
  /**
   * 数据库类型
   */
  private String         dataType;
  /**
   * 类型定义
   */
  private TypeDefinition typeDefinition;
  /**
   * 注释
   */
  private String         comment;

  public String getClassName() {

    return GeneratorUtil.toClass(table);
  }

  public String getFieldName() {

    if (column == null) {
      return null;
    }
    return StringUtil.camel(column);
  }

  public String getComment() {

    if (comment == null) {
      return null;
    }
    if (comment.contains("#")) {
      return comment.split("#")[0];
    }
    return comment;
  }

}
