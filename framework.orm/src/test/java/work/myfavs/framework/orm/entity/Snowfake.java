package work.myfavs.framework.orm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.Condition;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.enumeration.Operator;

@Data
@Table(value = "tb_snowfake", strategy = GenerationType.SNOW_FLAKE)
public class Snowfake
    implements Serializable {

  /**
   * ID
   */
  @Column(value = "id")
  @PrimaryKey
  private Long       id      = null;
  /**
   * 创建时间
   */
  @Column(value = "created")
  private Date       created = null;
  /**
   * 名称
   */
  @Column(value = "name")
  @Condition(value = "name", operator = Operator.LIKE, order = 1)
  private String     name    = null;
  /**
   * 是否停用
   */
  @Column(value = "disable")
  private Boolean    disable = false;
  /**
   * 价格
   */
  @Column(value = "price")
  private BigDecimal price   = BigDecimal.ZERO;
  /**
   * 类型
   */
  @Column(value = "type")
  @Condition(order = 2)
  private TypeEnum   type    = null;
  /**
   * 配置
   */
  @Column(value = "config")
  private String     config  = null;

}