package work.myfavs.framework.orm.generator.meta;

import lombok.Data;
import lombok.NonNull;

/**
 * 类型定义
 */
@Data
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

  private TypeDefinition() {

  }

  public TypeDefinition(@NonNull String name) {

    this(name, name);
  }

  public TypeDefinition(@NonNull String name, String valueType) {

    this.name = name;
    this.valueType = valueType;

    if (name.contains(".")) {
      String[] s = name.split(".");
      this.simpleName = s[s.length - 1];
    } else {
      this.simpleName = name;
    }
  }


}
