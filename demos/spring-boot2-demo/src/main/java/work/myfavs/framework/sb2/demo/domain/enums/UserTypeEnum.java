package work.myfavs.framework.sb2.demo.domain.enums;

/**
 * 用户类型枚举.
 */
public enum UserTypeEnum {
  ADMIN("管理员"),
  USER("普通用户");

  UserTypeEnum(String description) {

    this.description = description;
  }

  private String description;

  public String getDescription() {

    return description;
  }

  public void setDescription(String description) {

    this.description = description;
  }
}
