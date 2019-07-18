package work.myfavs.framework.orm.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.DBConvert;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.exception.DBException;

@Slf4j
public class QueryRepository {

  private DBTemplate dbTemplate;

  private QueryRepository() {}

  public QueryRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
  }

  protected <TView> List<TView> find(Class<TView> tViewClass, String sql, Object[] params) {

    Metadata.get(tViewClass);

    Connection        conn  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;

    try {
      conn = this.dbTemplate.createConnection();
      pstmt = DBUtil.getPs(conn, sql, params);
      rs = pstmt.executeQuery();
      return DBConvert.toList(tViewClass, rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.dbTemplate.release(conn, pstmt, rs);
    }
  }

}
