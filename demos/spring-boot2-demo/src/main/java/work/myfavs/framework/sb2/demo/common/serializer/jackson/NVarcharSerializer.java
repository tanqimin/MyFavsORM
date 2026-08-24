package work.myfavs.framework.sb2.demo.common.serializer.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.io.IOException;

/**
 * Jackson {@link NVarchar} 序列化器，将 {@link NVarchar} 输出为普通 JSON 字符串.
 *
 * <p>等价于原 Fastjson 的 {@code NVarcharObjectSerializer}，保证返回体中的 {@link NVarchar}
 * 字段序列化为字符串而不是对象.
 */
public class NVarcharSerializer extends StdSerializer<NVarchar> {

  public NVarcharSerializer() {
    super(NVarchar.class);
  }

  @Override
  public void serialize(NVarchar value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    if (value == null) {
      gen.writeNull();
      return;
    }
    gen.writeString(value.toString());
  }
}
