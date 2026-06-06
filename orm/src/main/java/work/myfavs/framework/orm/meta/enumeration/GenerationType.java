package work.myfavs.framework.orm.meta.enumeration;

import work.myfavs.framework.orm.util.exception.DBException;

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
        throw new DBException("不支持的主键策略.");
    }
  }
}
