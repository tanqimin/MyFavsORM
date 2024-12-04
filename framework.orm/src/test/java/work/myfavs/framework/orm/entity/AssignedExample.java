package work.myfavs.framework.orm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "tb_assigned", strategy = GenerationType.ASSIGNED)
public class AssignedExample {
  @PrimaryKey
  @Column
  private String epc;
}
