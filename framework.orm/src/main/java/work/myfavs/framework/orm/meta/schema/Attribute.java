package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.LogicDelete;
import work.myfavs.framework.orm.meta.annotation.NVarchar;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.reflection.FieldVisitor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

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
  private final String          columnName;
  /**
   * Field访问器
   */
  private final FieldVisitor    fieldVisitor;
  /**
   * java.sql.Types 类型
   */
  private final int             sqlType;
  /**
   * 是否只读？
   */
  private final boolean         readonly;
  /**
   * 是否主键？
   */
  private final boolean         primaryKey;
  /**
   * 是否逻辑删除字段？
   */
  private final boolean         logicDelete;
  /**
   * 类型处理器
   */
  @SuppressWarnings("rawtypes")
  private final PropertyHandler propertyHandler;
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

  private Attribute(Field field) {
    Column column = field.getAnnotation(Column.class);
    this.readonly = column.readonly();
    this.fieldVisitor = new FieldVisitor(field);
    this.primaryKey = isPrimaryKey(field);
    this.logicDelete = isLogicDelete(field);
    this.columnName = StrUtil.isEmpty(column.value())
        ? StringUtil.toUnderlineCase(field.getName())
        : column.value();

    this.propertyHandler = getPropertyHandler(field);
    this.sqlType = this.propertyHandler.getSqlType();
  }

  private static PropertyHandler<?> getPropertyHandler(Field field) {
    if (isNVachar(field) && isStringField(field))
      return PropertyHandlerFactory.getNVarcharPropertyHandler();
    return PropertyHandlerFactory.getInstance(field.getType());
  }

  private static boolean isStringField(Field field) {
    return String.class.equals(field.getType());
  }

  private static boolean isNVachar(Field field) {
    return Objects.nonNull(field.getAnnotation(NVarchar.class));
  }

  // endregion

  /**
   * 把指定字段解析为属性元数据
   *
   * @param field 指定字段
   * @return 属性元数据
   */
  static Attribute createInstance(Field field) {

    if (Objects.isNull(field.getAnnotation(Column.class)))
      return null;

    return new Attribute(field);
  }

  private static boolean isPrimaryKey(Field field) {
    return field.getAnnotation(PrimaryKey.class) != null;
  }

  private static boolean isLogicDelete(Field field) {
    return field.getAnnotation(LogicDelete.class) != null;
  }

  public <TModel> void setValue(TModel model, ResultSet rs, int columnIndex) throws SQLException {
    this.setValue(model, this.propertyHandler.convert(rs, columnIndex, this.fieldVisitor.getType()));
  }

  public <TModel> void setValue(TModel model, Object value) {
    this.fieldVisitor.setValue(model, value);
  }
}
