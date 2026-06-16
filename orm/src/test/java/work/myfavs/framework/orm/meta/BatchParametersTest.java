package work.myfavs.framework.orm.meta;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BatchParametersTest {

  @BeforeClass
  public static void beforeClass() {
    PropertyHandlerFactory.registerDefault();
  }

  final BatchParameters batchParameters = new BatchParameters();

  @Before
  public void setUp() {
    // Reset batchParameters before each test by clearing
    batchParameters.clear();
  }

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
  public void isBatch_ShouldReturnFalse_WhenSingleBatch() {
    assertFalse(batchParameters.isBatch());
  }

  @Test
  public void isBatch_ShouldReturnTrue_AfterAddBatch() {
    batchParameters.addBatch();
    assertTrue(batchParameters.isBatch());
  }

  @Test
  public void addBatch() {
    batchParameters.addBatch();
    assertEquals(2, batchParameters.getBatchParameters().size());
  }

  @Test
  public void addBatch_MultipleTimes() {
    batchParameters.addBatch();
    batchParameters.addBatch();
    batchParameters.addBatch();
    assertEquals(4, batchParameters.getBatchParameters().size());
    assertTrue(batchParameters.isBatch());
  }

  @Test
  public void clear() {
    batchParameters.addBatch();
    batchParameters.addBatch();
    batchParameters.clear();
    assertEquals(1, batchParameters.getBatchParameters().size());
    assertFalse(batchParameters.isBatch());
  }

  @Test
  public void isEmpty_ShouldReturnTrue_WhenNoParams() {
    batchParameters.clear();
    assertTrue(batchParameters.isEmpty());
  }

  @Test
  public void isEmpty_ShouldReturnFalse_WhenHasParams() {
    batchParameters.addParameter(1, "A");
    assertFalse(batchParameters.isEmpty());
  }

  // region applyParameters

  @Test
  public void applyParameters_ShouldApplyCurrentBatchParams() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    batchParameters.addParameter(1, "hello");
    batchParameters.applyParameters(ps);
    // StringPropertyHandler is registered by registerDefault()
    verify(ps).setString(1, "hello");
  }

  @Test
  public void applyParameters_ShouldDoNothing_WhenNoParams() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    batchParameters.applyParameters(ps);
    verify(ps, never()).setObject(anyInt(), any());
  }

  // endregion

  // region applyBatchParameters

  @Test
  public void applyBatchParameters_ShouldReturnEmptyArray_WhenNoBatches() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    int[] result = batchParameters.applyBatchParameters(ps, -1);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void applyBatchParameters_WithOneBatch_ShouldAddBatchOnly() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    batchParameters.addParameter(1, "A");
    when(ps.executeBatch()).thenReturn(new int[]{1});

    int[] result = batchParameters.applyBatchParameters(ps, -1);

    verify(ps).addBatch();
    // With batchSize <= 0, executeBatch() is not called by applyBatchParameters;
    // the caller (Query.executeBatch) calls it separately
    verify(ps, never()).executeBatch();
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void applyBatchParameters_WithMultipleBatches_ShouldAddBatchForEach() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    batchParameters.addParameter(1, "A");
    batchParameters.addBatch();
    batchParameters.addParameter(1, "B");
    batchParameters.addBatch();
    batchParameters.addParameter(1, "C");

    int[] result = batchParameters.applyBatchParameters(ps, -1);

    verify(ps, times(3)).addBatch();
    // With batchSize <= 0, executeBatch() is not called by applyBatchParameters
    verify(ps, never()).executeBatch();
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void applyBatchParameters_WithBatchSize_ShouldExecuteIntermittently() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    // Add 3 parameters with batchSize = 2, so should trigger executeBatch only at batch 2 (key=2, 2%2==0)
    batchParameters.addParameter(1, "A");
    batchParameters.addBatch(); // batch 2
    batchParameters.addParameter(1, "B");
    batchParameters.addBatch(); // batch 3
    batchParameters.addParameter(1, "C");

    when(ps.executeBatch()).thenReturn(new int[]{1, 1});

    int[] result = batchParameters.applyBatchParameters(ps, 2);

    // executeBatch called once: at batch 2 (key=2, 2%2=0)
    // batch 3 (key=3, 3%2=1) does NOT trigger executeBatch in this method
    verify(ps, times(1)).executeBatch();
    assertNotNull(result);
  }

  @Test
  public void applyBatchParameters_ShouldSkipEmptyBatches() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    batchParameters.addParameter(1, "A");
    batchParameters.addBatch();
    // second batch is empty

    when(ps.executeBatch()).thenReturn(new int[]{1});

    int[] result = batchParameters.applyBatchParameters(ps, -1);

    verify(ps, times(1)).addBatch(); // only non-empty batch
    assertNotNull(result);
  }

  // endregion
}