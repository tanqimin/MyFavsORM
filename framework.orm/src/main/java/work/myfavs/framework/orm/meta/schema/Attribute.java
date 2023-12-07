package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.LogicDelete;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.reflection.FieldVisitor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库列元数据
 *
 * @author tanqimin
 */
@SuppressWarnings("unchecked")
public class Attribute implements Serializable {

  private static final long serialVersionUID = 6913045257426812101L;

  // region Attributes
  /**
   * 数据库 列名称
   */
  private String       columnName;
  /**
   * Field访问器
   */
  private FieldVisitor fieldVisitor;
  /**
   * java.sql.Types 类型
   */
  private int          sqlType;
  /**
   * 是否只读？
   */
  private boolean      readonly    = false;
  /**
   * 是否主键？
   */
  private boolean      primaryKey  = false;
  /**
   * 是否逻辑删除字段？
   */
  private boolean      logicDelete = false;

  /**
   * 类型处理器
   */
  @SuppressWarnings("rawtypes")
  private PropertyHandler propertyHandler = null;
  // endregion

  // region Getter && Setter

  public String getColumnName() {
    return columnName;
  }

  public FieldVisitor getFieldVisitor() {
    return fieldVisitor;
  }

  public int getSqlType() {
    return sqlType;
  }

  public boolean isReadonly() {
    return readonly;
  }

  public boolean isPrimaryKey() {
    return primaryKey;
  }

  public boolean isLogicDelete() {
    return logicDelete;
  }


  // endregion

  // region Constructor

  private Attribute() {}

  // endregion

  /**
   * 把指定字段解析为属性元数据
   *
   * @param field 指定字段
   * @return 属性元数据
   */
  static Attribute createInstance(Field field) {
    Attribute    attribute = null;
    final Column column    = field.getAnnotation(Column.class);
    if (column != null) {
      attribute = new Attribute();
      attribute.fieldVisitor = new FieldVisitor(field);
      attribute.readonly = column.readonly();
      attribute.primaryKey = isPrimaryKey(field);
      attribute.logicDelete = isLogicDelete(field);
      attribute.columnName =
          StrUtil.isEmpty(column.value())
              ? StringUtil.toUnderlineCase(field.getName())
              : column.value();
      attribute.propertyHandler = PropertyHandlerFactory.getInstance(field.getType());
      attribute.sqlType = attribute.propertyHandler.getSqlType();
    }
    return attribute;
  }

  private static boolean isPrimaryKey(Field field) {
    return field.getAnnotation(PrimaryKey.class) != null;
  }

  private static boolean isLogicDelete(Field field) {
    return field.getAnnotation(LogicDelete.class) != null;
  }

  /**
   * 把 {@link ResultSet} 中对应字段的值转换为指定的对象
   *
   * @param rs {@link ResultSet}
   * @return Object
   * @throws SQLException 异常
   */
  public Object convert(ResultSet rs) throws SQLException {
    return this.propertyHandler.convert(rs, columnName, fieldVisitor.getType());
  }
}
