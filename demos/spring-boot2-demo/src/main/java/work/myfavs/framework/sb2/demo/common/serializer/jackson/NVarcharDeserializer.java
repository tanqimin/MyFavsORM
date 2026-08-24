package work.myfavs.framework.sb2.demo.common.serializer.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.io.IOException;

/**
 * Jackson {@link NVarchar} 反序列化器.
 *
 * <p>等价于原 Fastjson 的 {@code NVarcharObjectDeserializer}，将 JSON 字符串（或数值）还原为
 * {@link NVarchar}.
 */
public class NVarcharDeserializer extends StdDeserializer<NVarchar> {

  public NVarcharDeserializer() {
    super(NVarchar.class);
  }

  @Override
  public NVarchar deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String text = p.getValueAsString();
    return text == null ? null : new NVarchar(text);
  }
}
