package work.myfavs.framework.orm.entity;

import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Table(value = "tb_identity", strategy = GenerationType.IDENTITY)
public class Identity extends BaseEntity {

  /** ID */
  @Column(value = "id")
  @PrimaryKey
  private Long id = null;

  public Long getId() {

    return id;
  }

  public void setId(Long id) {

    this.id = id;
  }
}
