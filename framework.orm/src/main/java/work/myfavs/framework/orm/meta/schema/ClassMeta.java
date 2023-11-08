package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 类元数据
 *
 * @author tanqimin
 */
public class ClassMeta implements Serializable {

  private static final long serialVersionUID = -540703036198571358L;

  // region Attributes

  /**
   * 类型
   */
  private final Class<?>       clazz;
  /**
   * 数据表名
   */
  private       String         tableName;
  /**
   * 主键生成策略
   */
  private       GenerationType strategy;
  /**
   * 主键
   */
  private       Attribute      primaryKey;
  /**
   * 逻辑删除字段，null为不使用逻辑删除
   */
  private       Attribute      logicDelete;
  /**
   * 更新字段
   */
  private final Attributes     updateAttributes = new Attributes();
  /**
   * 查询字段
   */
  private final Attributes     queryAttributes  = new Attributes();

  // endregion

  // region Getter && Setter

  public Class<?> getClazz() {
    return clazz;
  }

  public String getTableName() {
    return tableName;
  }

  public GenerationType getStrategy() {
    return strategy;
  }

  public Attribute getPrimaryKey() {
    return primaryKey;
  }

  public Attribute getLogicDelete() {
    return logicDelete;
  }

  public Attributes getUpdateAttributes() {
    return updateAttributes;
  }

  public List<Attribute> getUpdateAttributes(String[] columns) {
    return updateAttributes.getAttributes(columns);
  }


  public Attributes getQueryAttributes() {
    return queryAttributes;
  }


  // endregion

  // region Constructor

  /**
   * 构造方法
   */
  private ClassMeta(Class<?> clazz) {
    this.clazz = clazz;

    final Table table = clazz.getAnnotation(Table.class);
    if (table != null) {
      this.strategy = table.strategy();
      this.tableName =
          StrUtil.isEmpty(table.value())
              ? StringUtil.toUnderlineCase(clazz.getSimpleName())
              : table.value();
    }

    final Field[] fields = ReflectUtil.getFields(clazz);

    if (fields == null) {
      return;
    }

    for (Field field : fields) {
      final Attribute attr = Attribute.createInstance(field);
      if (attr == null) {
        continue;
      }

      this.queryAttributes.put(attr.getColumnName(), attr);

      if (attr.isReadonly()) {
        continue;
      }

      if (attr.isPrimaryKey()) {
        this.primaryKey = attr;
      } else if (attr.isLogicDelete()) {
        this.logicDelete = attr;
      } else {
        this.updateAttributes.put(attr.getColumnName(), attr);
      }
    }
  }
  // endregion

  /**
   * 解析指定类为类元数据
   *
   * @param clazz 指定类
   * @return 列元数据
   */
  public static ClassMeta createInstance(Class<?> clazz) {
    return new ClassMeta(clazz);
  }

  /**
   * 检查主键，如果不为 null，则返回主键
   *
   * @return 主键
   */
  public Attribute checkPrimaryKey() {

    if (primaryKey == null) {
      throw new DBException("The view class [{}] could not contain primary key", this.clazz.getName());
    }
    return primaryKey;
  }

  /**
   * 检查主键，如果不为 null，则返回主键字段名
   *
   * @return 主键字段名
   */
  public String getPrimaryKeyFieldName() {
    return checkPrimaryKey().getFieldName();
  }

  /**
   * 检查主键，如果不为 null，返回主键数据库列名
   *
   * @return 主键数据库列名
   */
  public String getPrimaryKeyColumnName() {
    return checkPrimaryKey().getColumnName();
  }
}
