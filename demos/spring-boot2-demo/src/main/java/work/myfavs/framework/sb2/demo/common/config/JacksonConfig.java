package work.myfavs.framework.sb2.demo.common.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.myfavs.framework.orm.util.lang.NVarchar;
import work.myfavs.framework.sb2.demo.common.serializer.jackson.NVarcharDeserializer;
import work.myfavs.framework.sb2.demo.common.serializer.jackson.NVarcharSerializer;

/**
 * Jackson 配置，注册 {@link NVarchar} 的自定义序列化与反序列化.
 *
 * <p>原 WebConfig 使用 Fastjson 将 {@link NVarchar} 输出为普通 JSON 字符串；去除 Fastjson
 * 后改用 Spring Boot 默认的 Jackson 消息转换器，并通过 {@link Module} 注册等价行为.
 */
@Configuration
public class JacksonConfig {

  @Bean
  public Module nvarcharJacksonModule() {
    SimpleModule module = new SimpleModule("NVarcharModule");
    module.addSerializer(NVarchar.class, new NVarcharSerializer());
    module.addDeserializer(NVarchar.class, new NVarcharDeserializer());
    return module;
  }
}
