package net.pieroxy.conkw.utils.pools.hashmap;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;

import java.io.IOException;
import java.util.Map;

public class HashMapStringDoubleConverter extends AbstractMapConverter<Double> {
  private static HashMapStringDoubleConverter instance = new HashMapStringDoubleConverter();

  public static final JsonReader.ReadObject<Map<String, Double>> JSON_READER = reader -> reader.wasNull() ? null : instance.deserializeMap(reader);
  public static final JsonWriter.WriteObject<Map<String, Double>> JSON_WRITER = (writer, value) -> instance.serializeNullableMap(value, writer);

  public HashMapStringDoubleConverter() {
  }

  @Override
  protected Double deserializeNullableValue(JsonReader reader) throws IOException {
    if (reader.last() == 110) {
      if (!reader.wasNull()) {
        throw reader.newParseErrorAt("Expecting 'null' for null constant", 0);
      }
      return null;
    }
    return NumberConverter.deserializeDouble(reader);
  }
}
