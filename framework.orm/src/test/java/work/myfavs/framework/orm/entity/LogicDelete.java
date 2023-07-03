package work.myfavs.framework.orm.entity;

import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Table(
    value = "tb_logic_delete",
    strategy = GenerationType.SNOW_FLAKE,
    logicalDeleteField = "deleted")
public class LogicDelete extends Snowflake {
  private boolean deleted = false;

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
