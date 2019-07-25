package work.myfavs.framework.orm.repository.monitor;

import java.io.Serializable;
import lombok.Data;

@Data
public class SqlAnalysis
    implements Serializable {

  private long      elapsed        = -1L;
  private int       affectedRows   = 0;
  private long      mappingElapsed = -1L;
  private boolean   hasError       = false;
  private Throwable throwable      = null;

}
