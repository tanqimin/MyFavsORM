package work.myfavs.framework.sb2.demo.common.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import work.myfavs.framework.orm.util.lang.NVarchar;
import work.myfavs.framework.sb2.demo.common.serializer.fastjson.NVarcharObjectDeserializer;
import work.myfavs.framework.sb2.demo.common.serializer.fastjson.NVarcharObjectSerializer;

import java.util.List;

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

  /**
   * 配置消息转换器，使用 Fastjson 替换 Jackson.
   *
   * @param converters HTTP 消息转换器列表
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);

    converters.add(createFastJsonHttpMessageConverter());
  }

  private FastJsonHttpMessageConverter createFastJsonHttpMessageConverter() {
    FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
    // 配置Fastjson的相关属性
    FastJsonConfig config = new FastJsonConfig();
    config.setSerializerFeatures(
        SerializerFeature.WriteMapNullValue,        // 是否输出值为null的字段,默认为false,我们将它打开
        SerializerFeature.WriteNullListAsEmpty,     // 将Collection类型字段的字段空值输出为[]
        SerializerFeature.WriteNullStringAsEmpty,   // 将字符串类型字段的空值输出为空字符串
        SerializerFeature.WriteNullNumberAsZero,    // 将数值类型字段的空值输出为0
        SerializerFeature.WriteDateUseDateFormat,
        SerializerFeature.DisableCircularReferenceDetect    // 禁用循环引用
    );

    config.getSerializeConfig().put(NVarchar.class, new NVarcharObjectSerializer());
    config.getParserConfig().putDeserializer(NVarchar.class, new NVarcharObjectDeserializer());

    // ...配置config（如：serializerFeatures、datePattern等）
//    List<MediaType> fastMediaTypes = new ArrayList<>();
//    fastMediaTypes.add(MediaType.APPLICATION_JSON);
//    fastJsonConverter.setSupportedMediaTypes(fastMediaTypes);
    fastJsonConverter.setFastJsonConfig(config);
    return fastJsonConverter;
  }
}
