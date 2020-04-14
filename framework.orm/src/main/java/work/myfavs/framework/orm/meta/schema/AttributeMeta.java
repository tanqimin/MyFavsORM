package work.myfavs.framework.orm.meta.schema;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;

/**
 * 数据库列元数据
 */
@SuppressWarnings("unchecked")
public class AttributeMeta
    implements Serializable {

  //region Attributes
  /**
   * 数据库 列名称
   */
  private String columnName;
  /**
   * 类 属性名称
   */
  private String fieldName;
  /**
   * 类 属性类型
   */
  private Class<?> fieldType;
  /**
   * 是否只读？
   */
  private boolean readonly = false;
  /**
   * 是否主键？
   */
  private boolean primaryKey = false;

  /**
   * 类型处理器
   */
  private PropertyHandler propertyHandler = null;
  //endregion

  //region Getter && Setter
  public String getColumnName() {

    return columnName;
  }

  public void setColumnName(String columnName) {

    this.columnName = columnName;
  }

  public String getFieldName() {

    return fieldName;
  }

  public void setFieldName(String fieldName) {

    this.fieldName = fieldName;
  }

  public Class<?> getFieldType() {

    return fieldType;
  }

  public void setFieldType(Class<?> fieldType) {

    this.fieldType = fieldType;
  }

  public boolean isReadonly() {

    return readonly;
  }

  public void setReadonly(boolean readonly) {

    this.readonly = readonly;
  }

  public boolean isPrimaryKey() {

    return primaryKey;
  }

  public void setPrimaryKey(boolean primaryKey) {

    this.primaryKey = primaryKey;
  }

  public PropertyHandler getPropertyHandler() {

    return propertyHandler;
  }

  public void setPropertyHandler(PropertyHandler propertyHandler) {

    this.propertyHandler = propertyHandler;
  }
  //endregion

  //region Constructor
  private AttributeMeta() {
  }
  //endregion

  /**
   * 把指定字段解析为属性元数据
   *
   * @param field 指定字段
   * @return 属性元数据
   */
  static AttributeMeta createInstance(Field field) {

    AttributeMeta attributeMeta = null;

    Column column = field.getAnnotation(Column.class);
    if (column != null) {
      attributeMeta = new AttributeMeta();

      final PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
      final String fieldName = field.getName();
      final Class<?> fieldType = field.getType();
      final PropertyHandler propertyHandler = PropertyHandlerFactory.getInstance(fieldType);
      final String columnName = column.value()
          .isEmpty()
          ? fieldName
          : column.value();

      attributeMeta.setFieldName(fieldName);
      attributeMeta.setFieldType(fieldType);
      attributeMeta.setReadonly(column.readonly());
      attributeMeta.setPrimaryKey(primaryKey != null);
      attributeMeta.setColumnName(columnName);
      attributeMeta.setPropertyHandler(propertyHandler);
    }
    return attributeMeta;
  }

  public Object convert(ResultSet rs)
      throws SQLException {

    return this.propertyHandler.convert(rs, columnName, fieldType);
  }

}
