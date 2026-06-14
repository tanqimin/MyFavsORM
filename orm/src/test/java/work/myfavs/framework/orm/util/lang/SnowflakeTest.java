package work.myfavs.framework.orm.util.lang;

import org.junit.Test;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import static org.junit.Assert.*;

/**
 * {@link Snowflake} 分布式 ID 算法的单元测试.
 *
 * <p>覆盖以下场景：</p>
 * <ul>
 *   <li>workerId/dataCenterId 边界值（0 和 31）</li>
 *   <li>ID 唯一性和自增性</li>
 *   <li>从生成的 ID 中反向提取 workerId 和 dataCenterId</li>
 *   <li>参数合法性校验（负数、超范围）</li>
 *   <li>字符串形式 ID 生成</li>
 * </ul>
 *
 * @see Snowflake
 */
public class SnowflakeTest {

  /**
   * 验证 workerId 和 dataCenterId 为最小值 0 时能正常生成 ID.
   * <p>这是 {@code checkBetween} 的下边界场景，
   * 验证 5-bit 字段的合法范围下限（0）不被错误拦截。</p>
   */
  @Test
  public void shouldGenerateIdWithBoundaryWorkerId() {
    Snowflake sf   = new Snowflake(0, 0);
    long      id1  = sf.nextId();
    long      id2  = sf.nextId();
    assertTrue("ID must be positive", id1 > 0);
    assertTrue("Subsequent ID must be larger", id2 > id1);
  }

  /**
   * 验证 workerId 和 dataCenterId 为最大值 31 时能正常生成 ID.
   * <p>这是 {@code checkBetween} 的上边界场景，
   * 验证 5-bit 字段的合法范围上限（31）不被错误拦截。</p>
   */
  @Test
  public void shouldGenerateIdWithMaxWorkerId() {
    Snowflake sf  = new Snowflake(31, 31);
    long      id1 = sf.nextId();
    long      id2 = sf.nextId();
    assertTrue("ID must be positive", id1 > 0);
    assertTrue("Subsequent ID must be larger", id2 > id1);
  }

  /**
   * 验证同一实例连续生成的 ID 不重复.
   */
  @Test
  public void shouldGenerateUniqueIds() {
    Snowflake sf   = new Snowflake(1, 1);
    long      id1  = sf.nextId();
    long      id2  = sf.nextId();
    assertNotEquals("IDs must be unique", id1, id2);
  }

  /**
   * 验证从生成的 ID 中能正确反向提取 workerId 和 dataCenterId.
   */
  @Test
  public void shouldExtractWorkerIdFromGeneratedId() {
    Snowflake sf = new Snowflake(15, 10);
    long      id = sf.nextId();
    assertEquals("Extracted workerId must match", 15, sf.getWorkerId(id));
    assertEquals("Extracted dataCenterId must match", 10, sf.getDataCenterId(id));
  }

  /**
   * 验证负数的 workerId 会被构造器拒绝.
   *
   * @throws InvalidDataAccessException 预期异常
   */
  @Test(expected = InvalidDataAccessException.class)
  public void shouldRejectNegativeWorkerId() {
    new Snowflake(-1, 0);
  }

  /**
   * 验证超出最大值的 workerId 会被构造器拒绝.
   * <p>5-bit 字段最大值 31，{@code workerId = 32} 应在构造阶段抛出异常。</p>
   *
   * @throws InvalidDataAccessException 预期异常
   */
  @Test(expected = InvalidDataAccessException.class)
  public void shouldRejectWorkerIdExceedMax() {
    new Snowflake(32, 0);
  }

  /**
   * 验证 {@link Snowflake#nextIdStr()} 能生成非空字符串形式的 ID.
   */
  @Test
  public void shouldGenerateStringId() {
    Snowflake sf  = new Snowflake(1, 1);
    String    ids = sf.nextIdStr();
    assertNotNull("String ID must not be null", ids);
    assertTrue("String ID must not be empty", ids.length() > 0);
  }
}
