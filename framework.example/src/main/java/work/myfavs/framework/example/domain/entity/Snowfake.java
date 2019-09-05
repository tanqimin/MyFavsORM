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
@Table(value = Snowfake.META.TABLE, strategy = GenerationType.SNOW_FLAKE)
public class Snowfake implements Serializable {

    public enum META {
      ;
      public static final String TABLE = "tb_snowfake";

      public interface COLUMNS {
        String id = "id";
        String created = "created";
        String name = "name";
        String disable = "disable";
        String price = "price";
        String type = "type";
        String config = "config";
      }
    }

    /**
     * ID
     */
    @Column(value = Snowfake.META.COLUMNS.id)
    @PrimaryKey
    private Long id = null;
    /**
     * 创建时间
     */
    @Column(value = Snowfake.META.COLUMNS.created)
    private Date created = null; 
    /**
     * 名称
     */
    @Column(value = Snowfake.META.COLUMNS.name)
    private String name = null; 
    /**
     * 是否停用
     */
    @Column(value = Snowfake.META.COLUMNS.disable)
    private Boolean disable = false; 
    /**
     * 价格
     */
    @Column(value = Snowfake.META.COLUMNS.price)
    private BigDecimal price = BigDecimal.ZERO; 
    /**
     * 类型
     */
    @Column(value = Snowfake.META.COLUMNS.type)
    private TypeEnum type = null; 
    /**
     * 配置
     */
    @Column(value = Snowfake.META.COLUMNS.config)
    private String config = null; 
}