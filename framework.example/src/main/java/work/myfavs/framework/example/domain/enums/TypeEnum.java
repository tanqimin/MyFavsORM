package work.myfavs.framework.example.domain.enums;

public enum TypeEnum {
  FOOD("食品"),
  DRINK("饮品");

  TypeEnum(String description) {

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
