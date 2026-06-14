package work.myfavs.framework.orm.meta;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * {@link TableAlias} 分表别名工具的单元测试.
 *
 * <p>覆盖以下场景：</p>
 * <ul>
 *   <li>默认未设置时返回 {@code null}</li>
 *   <li>设置与获取的一致性</li>
 *   <li>手动清理后恢复到 {@code null}</li>
 *   <li>{@link TableAlias#runnable(String, Runnable)} 便捷方法的作用域管理</li>
 *   <li>{@link TableAlias#supplier(String, java.util.function.Supplier)} 便捷方法的作用域管理</li>
 * </ul>
 *
 * @see TableAlias
 */
public class TableAliasTest {

  /**
   * 每个测试方法执行后清理 ThreadLocal，避免测试间相互污染.
   */
  @After
  public void tearDown() {
    TableAlias.clear();
  }

  /**
   * 验证未设置分表别名时，{@link TableAlias#get()} 返回 {@code null}.
   */
  @Test
  public void shouldGetDefaultNull() {
    assertNull("Default alias must be null", TableAlias.get());
  }

  /**
   * 验证设置分表别名后能通过 {@link TableAlias#get()} 正确读取.
   */
  @Test
  public void shouldSetAndGet() {
    TableAlias.set("tb_user_2024");
    assertEquals("tb_user_2024", TableAlias.get());
  }

  /**
   * 验证 {@link TableAlias#clear()} 后 ThreadLocal 恢复到未设置状态.
   */
  @Test
  public void shouldClear() {
    TableAlias.set("tb_user_2024");
    TableAlias.clear();
    assertNull("Alias must be null after clear", TableAlias.get());
  }

  /**
   * 验证 {@link TableAlias#runnable(String, Runnable)} 在作用域内设置别名，
   * 执行完成后自动清理。
   */
  @Test
  public void shouldExecuteRunnable() {
    final boolean[] executed = {false};
    TableAlias.runnable("tb_order_2024", () -> {
      assertEquals("Alias must be set inside runnable", "tb_order_2024", TableAlias.get());
      executed[0] = true;
    });
    assertTrue("Runnable must have been executed", executed[0]);
    assertNull("Alias must be cleared after runnable completes", TableAlias.get());
  }

  /**
   * 验证 {@link TableAlias#supplier(String, java.util.function.Supplier)} 在作用域内设置别名，
   * 执行完成后自动清理，并正确返回供应值。
   */
  @Test
  public void shouldExecuteSupplier() {
    String result = TableAlias.supplier("tb_log_2024", () -> {
      assertEquals("Alias must be set inside supplier", "tb_log_2024", TableAlias.get());
      return "done";
    });
    assertEquals("Supplier must return expected value", "done", result);
    assertNull("Alias must be cleared after supplier completes", TableAlias.get());
  }
}
