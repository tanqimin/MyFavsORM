//package work.myfavs.framework.example.config.serializer.jackson;
//
//import com.fasterxml.jackson.core.JacksonException;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import work.myfavs.framework.orm.util.lang.NVarchar;
//
//import java.io.IOException;
//
//public class NVarcharJsonDeserializer extends JsonDeserializer<NVarchar> {
//
//  @Override
//  public NVarchar deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
//    String text = jsonParser.getText();
//    return new NVarchar(text);
//  }
//}
