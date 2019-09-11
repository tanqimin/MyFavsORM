package work.myfavs.framework.orm;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;

@Slf4j
public class SpringConnectionFactory
    extends JdbcConnectionFactory {


  public SpringConnectionFactory(DataSource dataSource) {

    super(dataSource);
  }

  @Override
  protected Connection createConnection()
      throws SQLException {

    return DataSourceUtils.getConnection(super.dataSource);
  }

  @Override
  protected void releaseConnection(Connection conn) {

    DataSourceUtils.releaseConnection(conn, super.dataSource);
  }

}
