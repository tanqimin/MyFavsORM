package work.myfavs.framework.orm.entity;

import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Table(value = "tb_uuid", strategy = GenerationType.UUID)
public class Uuid extends BaseEntity {

  /** ID */
  @Column(value = "id")
  @PrimaryKey
  private String id = null;

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }
}
