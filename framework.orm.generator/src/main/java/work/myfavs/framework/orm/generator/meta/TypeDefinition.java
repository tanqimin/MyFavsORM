package work.myfavs.framework.orm.generator.meta;

/**
 * 类型定义
 */
public class TypeDefinition {

  /**
   * 类型全称，如：java.lang.Long
   */
  private String name;
  /**
   * 类型名称，如：Long
   */
  private String simpleName;
  /**
   * 值类型，如：long
   */
  private String valueType;
  /**
   * 默认值
   */
  private String defValue;

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getSimpleName() {

    return simpleName;
  }

  public void setSimpleName(String simpleName) {

    this.simpleName = simpleName;
  }

  public String getValueType() {

    return valueType;
  }

  public void setValueType(String valueType) {

    this.valueType = valueType;
  }

  public String getDefValue() {

    return defValue;
  }

  public void setDefValue(String defValue) {

    this.defValue = defValue;
  }

  private TypeDefinition() {

  }

  public TypeDefinition(String name) {

    this(name, null);
  }

  public TypeDefinition(String name,
                        String defValue) {

    this(name, name, defValue);
  }

  public TypeDefinition(String name,
                        String valueType,
                        String defValue) {

    this.name = name;
    this.valueType = valueType;
    this.defValue = defValue;

    if (name.contains(".")) {
      String[] s = name.split("\\.");
      this.simpleName = s[s.length - 1];
    } else {
      this.simpleName = name;
    }
  }


}
