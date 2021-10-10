package net.pieroxy.conkw.utils.pools.hashmap;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;

import java.io.IOException;
import java.util.Map;

public class HashMapStringLongConverter  extends AbstractMapConverter<Long> {
  private static HashMapStringLongConverter instance = new HashMapStringLongConverter();

  public static final JsonReader.ReadObject<Map<String, Long>> JSON_READER = reader -> reader.wasNull() ? null : instance.deserializeMap(reader);
  public static final JsonWriter.WriteObject<Map<String, Long>> JSON_WRITER = (writer, value) -> instance.serializeNullableMap(value, writer);

  public HashMapStringLongConverter() {
  }

  @Override
  protected Long deserializeNullableValue(JsonReader reader) throws IOException {
    if (reader.last() == 110) {
      if (!reader.wasNull()) {
        throw reader.newParseErrorAt("Expecting 'null' for null constant", 0);
      }
      return null;
    }
    return NumberConverter.deserializeLong(reader);
  }
}
