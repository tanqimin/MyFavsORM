package work.myfavs.framework.orm.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
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

  public <TModel> List<TModel> find(Class<TModel> modelClass, String sql, Object[] params) {

    Metadata.get(modelClass);

    Connection        conn  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;

    try {
      conn = this.dbTemplate.createConnection();
      pstmt = DBUtil.getPs(conn, sql, params);
      rs = pstmt.executeQuery();
      return DBConvert.toList(modelClass, rs);
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      this.dbTemplate.release(conn, pstmt, rs);
    }
  }

  public <TModel> TModel get(Class<TModel> modelClass, String sql, Object[] params) {

    List<TModel> list = this.find(modelClass, sql, params);

    Iterator<TModel> iterator = list.iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

}
