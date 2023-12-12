package work.myfavs.framework.orm.meta;

import work.myfavs.framework.orm.util.exception.DBException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BatchParameters {
  private final Map<Integer/*batchIndex*/, Parameters> batchParameters = new LinkedHashMap<>();

  private int currentBatchSize;

  public BatchParameters() {
    currentBatchSize = 1;
  }

  public Parameters getCurrentBatchParameters() {
    return batchParameters.get(this.currentBatchSize);
  }

  public void addParameters(Collection<?> params) {
    Parameters parameters = batchParameters.get(currentBatchSize);
    parameters.addParameters(params);

  }

  public void addParameter(int paramIndex, Object param) {
    Parameters parameters = batchParameters.get(currentBatchSize);
    parameters.addParameter(paramIndex, param);
  }

  public void applyParameters(PreparedStatement statement) {
    getCurrentBatchParameters().applyParameters(statement);
  }

  public void applyBatchParameters(PreparedStatement statement, int batchSize) {
    try {
      for (Map.Entry<Integer, Parameters> entry : batchParameters.entrySet()) {
        Parameters parameters = entry.getValue();
        if (parameters.isEmpty()) continue;
        parameters.applyParameters(statement);

        statement.addBatch();
        if (batchSize > 0) {
          if (entry.getKey() % batchSize == 0)
            statement.executeBatch();
        }
      }
    } catch (SQLException ex) {
      throw new DBException(ex, "Error apply batch parameters: {}", ex.getMessage());
    }
  }

  public boolean isBatch() {
    return this.currentBatchSize > 1;
  }

  public void addBatch() {
    this.batchParameters.put(++this.currentBatchSize, new Parameters());
  }

  public void clear() {
    this.batchParameters.clear();
    this.put(this.currentBatchSize = 1, new Parameters());
  }

  private void put(int batchIndex, Parameters parameters) {
    this.batchParameters.put(batchIndex, parameters);
  }
}
