package work.myfavs.framework.orm.generator.meta.column;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;

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
    return StrUtil.toCamelCase(column);
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

  public String getTable() {

    return table;
  }

  public void setTable(String table) {

    this.table = table;
  }

  public String getColumn() {

    return column;
  }

  public void setColumn(String column) {

    this.column = column;
  }

  public Boolean getPrimaryKey() {

    return primaryKey;
  }

  public void setPrimaryKey(Boolean primaryKey) {

    this.primaryKey = primaryKey;
  }

  public Boolean getNullable() {

    return nullable;
  }

  public void setNullable(Boolean nullable) {

    this.nullable = nullable;
  }

  public Integer getIndex() {

    return index;
  }

  public void setIndex(Integer index) {

    this.index = index;
  }

  public String getDataType() {

    return dataType;
  }

  public void setDataType(String dataType) {

    this.dataType = dataType;
  }

  public TypeDefinition getTypeDefinition() {

    return typeDefinition;
  }

  public void setTypeDefinition(TypeDefinition typeDefinition) {

    this.typeDefinition = typeDefinition;
  }

  public void setComment(String comment) {

    this.comment = comment;
  }

}
