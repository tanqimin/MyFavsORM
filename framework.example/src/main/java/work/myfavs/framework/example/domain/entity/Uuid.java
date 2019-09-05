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
@Table(value = Uuid.META.TABLE, strategy = GenerationType.UUID)
public class Uuid implements Serializable {

    public enum META {
      ;
      public static final String TABLE = "tb_uuid";

      public interface COLUMNS {
        String id = "id";
        String created = "created";
        String name = "name";
        String disable = "disable";
        String price = "price";
        String type = "type";
      }
    }

    /**
     * ID
     */
    @Column(value = Uuid.META.COLUMNS.id)
    @PrimaryKey
    private String id = null;
    /**
     * 创建时间
     */
    @Column(value = Uuid.META.COLUMNS.created)
    private Date created = null;
    /**
     * 名称
     */
    @Column(value = Uuid.META.COLUMNS.name)
    private String name = null;
    /**
     * 是否停用
     */
    @Column(value = Uuid.META.COLUMNS.disable)
    private Boolean disable = false;
    /**
     * 价格
     */
    @Column(value = Uuid.META.COLUMNS.price)
    private BigDecimal price = BigDecimal.ZERO;
    /**
     * 类型
     */
    @Column(value = Uuid.META.COLUMNS.type)
    private TypeEnum type = null;
}