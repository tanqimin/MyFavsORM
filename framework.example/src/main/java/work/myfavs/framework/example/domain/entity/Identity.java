package work.myfavs.framework.example.domain.entity;

import java.io.Serializable;
import lombok.Data;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.String;
import java.math.BigDecimal;
import java.util.Date;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Data
@Table(value = "tb_identity", strategy = GenerationType.IDENTITY)
public class Identity implements Serializable {
    /**
     * ID
     */
    @Column(value = "id")
    @PrimaryKey
    private Long id = null;
    /**
     * 创建时间
     */
    @Column(value = "created")
    private Date created = null; 
    /**
     * 名称
     */
    @Column(value = "name")
    private String name = null; 
    /**
     * 是否停用？
     */
    @Column(value = "disable")
    private Boolean disable = false; 
    /**
     * 价格
     */
    @Column(value = "price")
    private BigDecimal price = BigDecimal.ZERO; 
    /**
     * 类型
     */
    @Column(value = "type")
    private TypeEnum type = null; 
}