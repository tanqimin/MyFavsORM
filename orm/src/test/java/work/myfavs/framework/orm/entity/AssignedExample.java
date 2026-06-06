package work.myfavs.framework.orm.entity;

import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Table(value = "tb_assigned", strategy = GenerationType.ASSIGNED)
public class AssignedExample {
  @PrimaryKey
  @Column
  private String epc;

  public AssignedExample() {
  }

  public AssignedExample(String epc) {
    this.epc = epc;
  }

  public String getEpc() {
    return epc;
  }

  public void setEpc(String epc) {
    this.epc = epc;
  }
}
