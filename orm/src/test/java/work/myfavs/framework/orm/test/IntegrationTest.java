package work.myfavs.framework.orm.test;

/**
 * JUnit {@link org.junit.experimental.categories.Category @Category} 标记接口。
 *
 * <p>标注了此接口的测试类属于<strong>集成测试</strong>，需要真实数据库连接。
 * 默认构建（{@code mvn test}）会跳过此类测试。</p>
 *
 * <p>如需运行集成测试，使用 Maven profile：</p>
 * <pre>{@code
 * mvn test -P integration
 * }</pre>
 */
public interface IntegrationTest {

}
