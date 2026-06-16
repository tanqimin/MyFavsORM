package work.myfavs.framework.orm.meta;

import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ParametersUnitTest {

  private Parameters parameters;

  @Before
  public void setUp() {
    parameters = new Parameters();
  }

  // region addParameter(int, Object)

  @Test
  public void addParameter_WithIndexAndValue_ShouldAdd() {
    parameters.addParameter(1, "hello");
    assertEquals(1, parameters.size());
    assertEquals("hello", parameters.getParameters().get(1));
  }

  @Test(expected = InvalidDataAccessException.class)
  public void addParameter_WithDuplicateIndex_ShouldThrow() {
    parameters.addParameter(1, "hello");
    parameters.addParameter(1, "world");
  }

  @Test
  public void addParameter_WithMultipleIndices_ShouldMaintainOrder() {
    parameters.addParameter(1, "first");
    parameters.addParameter(3, "third");
    parameters.addParameter(2, "second");

    Map<Integer, Object> map = parameters.getParameters();
    assertEquals(3, map.size());
    assertEquals("first", map.get(1));
    assertEquals("second", map.get(2));
    assertEquals("third", map.get(3));
  }

  // endregion

  // region addParameter(Object)

  @Test
  public void addParameter_WithoutIndex_ShouldAutoIncrement() {
    parameters.addParameter("a");
    parameters.addParameter("b");
    parameters.addParameter("c");

    assertEquals(3, parameters.size());
    assertEquals("a", parameters.getParameters().get(1));
    assertEquals("b", parameters.getParameters().get(2));
    assertEquals("c", parameters.getParameters().get(3));
  }

  @Test
  public void addParameter_WithoutIndex_AfterIndexedAdd_UsesCountNotMaxIndex() {
    parameters.addParameter(5, "five");
    // addParameter(Object) uses parameters.size() + 1, which is 1 + 1 = 2
    parameters.addParameter("six");

    assertEquals(2, parameters.size());
    assertEquals("five", parameters.getParameters().get(5));
    assertEquals("six", parameters.getParameters().get(2));
  }

  // endregion

  // region addParameters(Collection)

  @Test
  public void addParameters_WithCollection_ShouldAddSequentially() {
    parameters.addParameters(List.of("a", "b", "c"));

    assertEquals(3, parameters.size());
    assertEquals("a", parameters.getParameters().get(1));
    assertEquals("b", parameters.getParameters().get(2));
    assertEquals("c", parameters.getParameters().get(3));
  }

  @Test
  public void addParameters_WithEmptyCollection_ShouldDoNothing() {
    parameters.addParameters(Collections.emptyList());
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void addParameters_WithNullCollection_ShouldDoNothing() {
    parameters.addParameters((Collection<?>) null);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void addParameters_ShouldContinueAfterExistingParams() {
    parameters.addParameter("existing");
    parameters.addParameters(List.of("a", "b"));

    assertEquals(3, parameters.size());
    assertEquals("existing", parameters.getParameters().get(1));
    assertEquals("a", parameters.getParameters().get(2));
    assertEquals("b", parameters.getParameters().get(3));
  }

  // endregion

  // region isEmpty / size / getParameters

  @Test
  public void isEmpty_ShouldReturnTrue_WhenNoParams() {
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void isEmpty_ShouldReturnFalse_WhenHasParams() {
    parameters.addParameter("test");
    assertFalse(parameters.isEmpty());
  }

  @Test
  public void size_ShouldReturnZero_WhenEmpty() {
    assertEquals(0, parameters.size());
  }

  @Test
  public void size_ShouldReturnCorrectCount() {
    parameters.addParameter("a");
    parameters.addParameter("b");
    assertEquals(2, parameters.size());
  }

  @Test
  public void getParameters_ShouldReturnUnmodifiableLiveView() {
    parameters.addParameter("a");
    Map<Integer, Object> map = parameters.getParameters();
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
  }

  // endregion

  // region applyParameters

  @Test
  public void applyParameters_ShouldDoNothing_WhenEmpty() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    parameters.applyParameters(ps);
    verify(ps, never()).setObject(anyInt(), any());
  }

  @Test
  public void applyParameters_ShouldSetNull_WhenValueIsNull() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    parameters.addParameter(1, null);
    parameters.applyParameters(ps);
    verify(ps).setObject(1, null);
  }

  @Test
  public void applyParameters_ShouldSetObject_WhenValueIsString() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    parameters.addParameter(1, "hello");
    parameters.applyParameters(ps);
    // Default handler is ObjectPropertyHandler which uses setObject
    verify(ps).setObject(1, "hello");
  }

  @Test
  public void applyParameters_ShouldSetObject_WhenValueIsInteger() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    parameters.addParameter(1, 42);
    parameters.applyParameters(ps);
    verify(ps).setObject(1, 42);
  }

  @Test
  public void applyParameters_ShouldSetObject_WhenValueIsLong() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    parameters.addParameter(1, 99L);
    parameters.applyParameters(ps);
    verify(ps).setObject(1, 99L);
  }

  @Test
  public void applyParameters_ShouldSetObject_WhenValueIsBoolean() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    parameters.addParameter(1, true);
    parameters.applyParameters(ps);
    verify(ps).setObject(1, true);
  }

  @Test
  public void applyParameters_ShouldSetMultipleParams() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    parameters.addParameter(1, "a");
    parameters.addParameter(2, 42);
    parameters.addParameter(3, null);
    parameters.applyParameters(ps);
    verify(ps).setObject(1, "a");
    verify(ps).setObject(2, 42);
    verify(ps).setObject(3, null);
  }

  // endregion
}
