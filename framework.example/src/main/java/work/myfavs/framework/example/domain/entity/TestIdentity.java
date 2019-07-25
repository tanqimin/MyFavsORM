package work.myfavs.framework.example.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Data
@Table(value = "tb_identity", strategy = GenerationType.IDENTITY)
public class TestIdentity
    implements Serializable {

  @PrimaryKey
  @Column
  private Long       id;
  @Column
  private Date       created;
  @Column
  private String     name;
  @Column
  private boolean    disable;
  @Column
  private BigDecimal price = BigDecimal.ZERO;
  @Column
  private TypeEnum   type;

}
