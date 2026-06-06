package work.myfavs.framework.sb2.demo.config;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import work.myfavs.framework.sb2.demo.Application;

/**
 * Servlet 容器初始化器，用于 WAR 包部署时配置 Spring Boot 应用源.
 */
public class ServletInitializer extends SpringBootServletInitializer {

  /**
   * 配置 Spring Boot 应用源.
   *
   * @param builder Spring 应用构建器
   * @return 配置后的 Spring 应用构建器
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

    return builder.sources(Application.class);
  }
}
