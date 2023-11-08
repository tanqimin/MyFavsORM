package work.myfavs.framework.orm.entity;

import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Table(
    value = "tb_logic_delete",
    strategy = GenerationType.SNOW_FLAKE)
public class LogicDelete extends Snowflake {

  @Column("deleted")
  @work.myfavs.framework.orm.meta.annotation.LogicDelete
  private long deleted = 0;

  public long getDeleted() {
    return deleted;
  }

  public void setDeleted(long deleted) {
    this.deleted = deleted;
  }
}
