package work.myfavs.framework.sb2.demo.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置，包括资源处理、内容协商、跨域和消息转换器.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  /**
   * 添加静态资源处理器.
   *
   * @param registry 资源处理器注册表
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
  }

  /**
   * 配置内容协商，默认返回 JSON 格式.
   *
   * @param configurer 内容协商配置器
   */
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    configurer.defaultContentType(MediaType.APPLICATION_JSON).favorParameter(true);
  }

  /**
   * 配置跨域访问.
   *
   * @param registry 跨域注册表
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowedOrigins("*");
  }
}
