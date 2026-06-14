package work.myfavs.framework.orm.meta.pagination;

import org.junit.Test;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import static org.junit.Assert.*;

/**
 * {@link Order} 排序对象的单元测试.
 *
 * <p>覆盖以下场景：</p>
 * <ul>
 *   <li>排序字符串解析（默认升序 / 显式降序 / 大小写不敏感方向）</li>
 *   <li>排序语句生成（{@link Order#getClause()}）</li>
 *   <li>SQL 注入检测</li>
 *   <li>非法输入（空字符串、非法方向、空字段）</li>
 * </ul>
 *
 * @see Order
 * @see work.myfavs.framework.orm.util.common.SqlUtil#checkInjection(String)
 */
public class OrderTest {

  /**
   * 验证 {@link Order#parse(String)} 在不指定方向时默认升序.
   * <p>生成的排序语句应为纯字段名，无 {@code ASC} 后缀。</p>
   */
  @Test
  public void shouldParseAscendingByDefault() {
    Order order = Order.parse("name");
    assertTrue("Default direction should be ascending", order.isAscending());
    assertEquals("name", order.getClause());
  }

  /**
   * 验证 {@link Order#parse(String)} 解析显式降序排序.
   */
  @Test
  public void shouldParseDescending() {
    Order order = Order.parse("name DESC");
    assertFalse("Direction should be descending", order.isAscending());
    assertEquals("name DESC", order.getClause());
  }

  /**
   * 验证 {@link Order#parse(String)} 对方向关键字大小写不敏感.
   * <p>输入 {@code desc} 应被转换为标准的大写 {@code DESC}。</p>
   */
  @Test
  public void shouldParseMixedCaseDirection() {
    Order order = Order.parse("name desc");
    assertEquals("Clause should normalize direction to uppercase", "name DESC", order.getClause());
  }

  /**
   * 验证空字符串的排序参数被拒绝.
   *
   * @throws InvalidDataAccessException 预期异常
   */
  @Test(expected = InvalidDataAccessException.class)
  public void shouldRejectEmptyOrderBy() {
    Order.parse("");
  }

  /**
   * 验证包含 SQL 注入字符的分号被拒绝.
   * <p>排序字段中包含 {@code ;} 应触发 {@link InvalidDataAccessException}。</p>
   *
   * @throws InvalidDataAccessException 预期异常
   */
  @Test(expected = InvalidDataAccessException.class)
  public void shouldRejectSqlInjectionWithSemicolon() {
    Order.parse("name; DROP TABLE users");
  }

  /**
   * 验证通过构造器设置升序方向时，{@link Order#getClause()} 返回纯字段名（无 ASC）.
   */
  @Test
  public void shouldReturnClauseForAscending() {
    Order order = new Order("fieldName", "ASC");
    assertEquals("fieldName", order.getClause());
  }

  /**
   * 验证非法方向值在调用 {@link Order#getClause()} 时被拒绝.
   *
   * @throws InvalidDataAccessException 预期异常
   */
  @Test(expected = InvalidDataAccessException.class)
  public void shouldRejectInvalidDirection() {
    Order order = new Order("name", "INVALID");
    order.getClause();
  }

  /**
   * 验证空字段名在调用 {@link Order#getClause()} 时被拒绝.
   *
   * @throws InvalidDataAccessException 预期异常
   */
  @Test(expected = InvalidDataAccessException.class)
  public void shouldRejectEmptyField() {
    Order order = new Order("", "ASC");
    order.getClause();
  }
}
