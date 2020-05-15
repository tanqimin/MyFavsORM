package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 类元数据
 */
public class ClassMeta {

  //region Attributes
  private Class<?> clazz;
  private String className;
  private String tableName;
  private GenerationType strategy;

  private AttributeMeta primaryKey;                                                         //主键
  private boolean enableLogicalDelete;                                                      //是否启用逻辑删除？
  private String logicalDeleteField;                                                        //逻辑删除字段（数据库字段）
  private List<AttributeMeta> updateAttributes = new Vector<>();                            //更新字段
  private Map<String, AttributeMeta> queryAttributes = new ConcurrentHashMap<>();           //查询字段
  //endregion

  //region Getter && Setter


  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

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

  public List<AttributeMeta> getUpdateAttributes(String[] columns) {
    if (columns == null || columns.length == 0) {
      return updateAttributes;
    }
    List<AttributeMeta> res = new ArrayList<>();
    for (String column : columns) {
      final AttributeMeta attributeMeta = this.queryAttributes.get(column.toUpperCase());
      if (attributeMeta == null || attributeMeta.isPrimaryKey() || attributeMeta.isReadonly()) {
        continue;
      }
      res.add(attributeMeta);
    }
    return res;
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

  /**
   * 构造方法
   *
   * @param clazz
   */
  private ClassMeta(Class<?> clazz) {
    this.clazz = clazz;
  }
  //endregion

  /**
   * 解析指定类为类元数据
   *
   * @param clazz 指定类
   * @return 列元数据
   */
  public static ClassMeta createInstance(Class<?> clazz) {

    ClassMeta classMeta;
    Table table;
    Field[] fields;

    classMeta = new ClassMeta(clazz);
    table = clazz.getAnnotation(Table.class);

    if (table != null) {
      classMeta.setClassName(clazz.getName());
      classMeta.setStrategy(table.strategy());
      classMeta.setTableName(table.value().isEmpty()
          ? clazz.getSimpleName()
          : table.value());
      if (table.logicalDeleteField().isEmpty() == false) {
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

      final String queryKey = attr.getColumnName().toUpperCase();
      final boolean readonly = attr.isReadonly();
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

  /**
   * 检查是否需要添加逻辑删除字段包含字段
   *
   * @param classMeta 类元数据
   * @return 如果不启用逻辑删除，返回false
   */
  public boolean needAppendLogicalDeleteField() {
    if (!enableLogicalDelete) {
      return false;
    }

    return !(queryAttributes.containsKey(logicalDeleteField.toLowerCase()));
  }
}
