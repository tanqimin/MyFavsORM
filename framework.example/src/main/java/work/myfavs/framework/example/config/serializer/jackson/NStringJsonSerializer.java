package work.myfavs.framework.example.config.serializer.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import work.myfavs.framework.orm.util.lang.NString;

import java.io.IOException;

public class NStringJsonSerializer extends JsonSerializer<NString> {
  @Override
  public void serialize(NString nString, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(nString.toString());
  }
}
