package work.myfavs.framework.orm.meta.enumeration;

import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

/**
 * 主键生成策略，定义实体对象主键值的生成方式。
 * <p>通过 {@link work.myfavs.framework.orm.meta.annotation.Table#strategy()} 配置到实体类上。</p>
 * <ul>
 *   <li>{@link #UUID} — 程序自动生成 UUID 字符串；</li>
 *   <li>{@link #SNOW_FLAKE} — 雪花算法生成分布式唯一 ID（默认策略）；</li>
 *   <li>{@link #IDENTITY} — 数据库自增，适用于有自增列的数据库（如 MySQL AUTO_INCREMENT、SQL Server IDENTITY）；</li>
 *   <li>{@link #ASSIGNED} — 自然主键，由用户在插入前手动赋值。</li>
 * </ul>
 *
 * @see work.myfavs.framework.orm.meta.annotation.Table
 */
public enum GenerationType {
  /**
   * UUID，值由系统字段生成
   */
  UUID,
  /**
   * 雪花算法生成，由程序生成字段
   */
  SNOW_FLAKE,
  /**
   * 数据库自增，值由数据库生成
   */
  IDENTITY,
  /**
   * 自然主键，值由用户自定义
   */
  ASSIGNED;

  /**
   * 获取当前主键生成策略的名称。
   *
   * @return 策略名称字符串
   */
  public String getName() {

    switch (this) {
      case SNOW_FLAKE:
        return "GenerationType.SNOW_FLAKE";
      case IDENTITY:
        return "GenerationType.IDENTITY";
      case UUID:
        return "GenerationType.UUID";
      case ASSIGNED:
        return "GenerationType.ASSIGNED";
      default:
        throw new InvalidDataAccessException("不支持的主键策略.");
    }
  }
}
