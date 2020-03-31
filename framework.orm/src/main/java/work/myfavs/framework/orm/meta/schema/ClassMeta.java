package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 类元数据
 */
public class ClassMeta {

  //region Attributes
  private String         className;
  private String         tableName;
  private GenerationType strategy;

  private AttributeMeta              primaryKey;                                  //主键
  private boolean                    enableLogicalDelete;                         //是否启用逻辑删除？
  private String                     logicalDeleteField;                          //逻辑删除字段（数据库字段）
  private List<AttributeMeta>        updateAttributes = new LinkedList<>();       //更新字段
  private Map<String, AttributeMeta> queryAttributes  = new HashMap<>();          //查询字段
  //endregion

  //region Getter && Setter
  public String getClassName() {

    return className;
  }

  public void setClassName(String className) {

    this.className = className;
  }

  public String getTableName() {

    return tableName;
  }

  public void setTableName(String tableName) {

    this.tableName = tableName;
  }

  public GenerationType getStrategy() {

    return strategy;
  }

  public void setStrategy(GenerationType strategy) {

    this.strategy = strategy;
  }

  public AttributeMeta getPrimaryKey() {

    return primaryKey;
  }

  public void setPrimaryKey(AttributeMeta primaryKey) {

    this.primaryKey = primaryKey;
  }

  public List<AttributeMeta> getUpdateAttributes() {

    return updateAttributes;
  }

  public void setUpdateAttributes(List<AttributeMeta> updateAttributes) {

    this.updateAttributes = updateAttributes;
  }

  public Map<String, AttributeMeta> getQueryAttributes() {

    return queryAttributes;
  }

  public void setQueryAttributes(Map<String, AttributeMeta> queryAttributes) {

    this.queryAttributes = queryAttributes;
  }

  public boolean isEnableLogicalDelete() {

    return enableLogicalDelete;
  }

  public void setEnableLogicalDelete(boolean enableLogicalDelete) {

    this.enableLogicalDelete = enableLogicalDelete;
  }

  public String getLogicalDeleteField() {

    return logicalDeleteField;
  }

  public void setLogicalDeleteField(String logicalDeleteField) {

    this.logicalDeleteField = logicalDeleteField;
  }

  //endregion

  //region Constructor
  private ClassMeta() {

  }
  //endregion

  /**
   * 解析指定类为类元数据
   *
   * @param clazz 指定类
   *
   * @return 列元数据
   */
  public static ClassMeta createInstance(Class<?> clazz) {

    Table     table;
    ClassMeta classMeta;
    Field[]   fields;

    table = clazz.getAnnotation(Table.class);
    classMeta = new ClassMeta();

    if (table != null) {
      classMeta.setClassName(clazz.getName());
      classMeta.setStrategy(table.strategy());
      classMeta.setTableName(table.value()
                                  .isEmpty()
                                 ? clazz.getSimpleName()
                                 : table.value());
      if (table.logicalDeleteField()
               .isEmpty() == false) {
        classMeta.setEnableLogicalDelete(true);
        classMeta.setLogicalDeleteField(table.logicalDeleteField());
      }
    }

    fields = ReflectUtil.getFields(clazz);

    for (Field field : fields) {
      AttributeMeta attr = AttributeMeta.createInstance(field);
      if (attr == null) {
        continue;
      }

      final String queryKey = attr.getColumnName()
                                  .toUpperCase();
      final boolean readonly   = attr.isReadonly();
      final boolean primaryKey = attr.isPrimaryKey();

      classMeta.queryAttributes.put(queryKey, attr);
      if (readonly) {
        continue;
      }
      if (primaryKey) {
        classMeta.setPrimaryKey(attr);
      } else {
        classMeta.updateAttributes.add(attr);
      }
    }

    return classMeta;
  }

  /**
   * 检查主键，如果不为 null，则返回主键
   *
   * @return 主键
   */
  public AttributeMeta checkPrimaryKey() {

    if (primaryKey == null) {
      throw new DBException("The view class [{}] could not contain primary key", getClassName());
    }
    return primaryKey;
  }

}
