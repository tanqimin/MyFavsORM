package work.myfavs.framework.orm.meta;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

public class BatchParametersTest {

  BatchParameters batchParameters = new BatchParameters();

  @Test
  public void getCurrentBatchParameters() {
    Parameters parameters = batchParameters.getCurrentBatchParameters();

    assertEquals(0, parameters.size());
  }

  @Test
  public void getBatchParameters() {
    Map<Integer, Parameters> batchParameters1 = batchParameters.getBatchParameters();
    assertEquals(1, batchParameters1.size());
  }

  @Test
  public void addParameters() {
    Parameters parameters = batchParameters.getCurrentBatchParameters();
    parameters.getParameters().clear();
    Collection<Object> params = new ArrayList<>();
    params.add("A");
    params.add("B");
    batchParameters.addParameters(params);
    assertEquals(2, parameters.getParameters().size());
  }

  @Test
  public void addParameter() {
    batchParameters.getCurrentBatchParameters().getParameters().clear();
    batchParameters.addParameter(1, "A");
    batchParameters.addParameter(2, "B");
    assertEquals(2, batchParameters.getCurrentBatchParameters().getParameters().size());
  }

  @Test
  public void applyParameters() {
  }

  @Test
  public void applyBatchParameters() {
  }

  @Test
  public void isBatch() {
    assertFalse(batchParameters.isBatch());
  }

  @Test
  public void addBatch() {
    batchParameters.addBatch();
    assertEquals(2, batchParameters.getBatchParameters().size());
  }

  @Test
  public void clear() {
    batchParameters.clear();
    assertEquals(1, batchParameters.getBatchParameters().size());
  }

  @Test
  public void isEmpty() {
    batchParameters.clear();
    assertTrue(batchParameters.isEmpty());
  }
}