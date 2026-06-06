package work.myfavs.framework.sb2.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot 应用入口类.
 */
@EnableTransactionManagement
@ServletComponentScan
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

  /**
   * 启动 Spring Boot 应用.
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {

    SpringApplication.run(Application.class, args);
  }

  /**
   * 配置 Spring Boot 应用源（Servlet 容器部署时调用）.
   *
   * @param builder Spring 应用构建器
   * @return 配置后的 Spring 应用构建器
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

    return builder.sources(Application.class);
  }
}
