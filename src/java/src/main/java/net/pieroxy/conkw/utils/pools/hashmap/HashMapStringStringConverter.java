package net.pieroxy.conkw.utils.pools.hashmap;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.StringConverter;

import java.io.IOException;
import java.util.Map;

public class HashMapStringStringConverter extends AbstractMapConverter<String> {
  private static HashMapStringStringConverter instance = new HashMapStringStringConverter();

  public static final JsonReader.ReadObject<Map<String, String>> JSON_READER = reader -> reader.wasNull() ? null : instance.deserializeMap(reader);
  public static final JsonWriter.WriteObject<Map<String, String>> JSON_WRITER = (writer, value) -> instance.serializeNullableMap(value, writer);

  public HashMapStringStringConverter() {
  }

  @Override
  protected String deserializeNullableValue(JsonReader reader) throws IOException {
    return StringConverter.deserializeNullable(reader);
  }

  @Override
  protected int getMapKey() {
    return -13;
  }
}
