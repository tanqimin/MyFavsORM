package work.myfavs.framework.example.domain.enums;

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
