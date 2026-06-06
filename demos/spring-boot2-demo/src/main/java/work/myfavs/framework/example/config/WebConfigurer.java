package work.myfavs.framework.example.config;

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
import work.myfavs.framework.example.config.serializer.fastjson.NVarcharObjectDeserializer;
import work.myfavs.framework.example.config.serializer.fastjson.NVarcharObjectSerializer;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    configurer.defaultContentType(MediaType.APPLICATION_JSON).favorParameter(true);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowedOrigins("*");
  }

  /**
   * 使用 Fastjson 需定义以下方法，使用 JSON 不用
   *
   * @param converters
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
