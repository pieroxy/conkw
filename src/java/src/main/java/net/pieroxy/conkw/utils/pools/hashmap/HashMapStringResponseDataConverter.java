package net.pieroxy.conkw.utils.pools.hashmap;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.IOException;
import java.util.Map;

public class HashMapStringResponseDataConverter extends AbstractMapConverter<ResponseData> {

  private static HashMapStringResponseDataConverter instance = new HashMapStringResponseDataConverter();

  public static final JsonReader.ReadObject<Map<String, ResponseData>> JSON_READER = reader -> reader.wasNull() ? null : instance.deserializeMap(reader);
  public static final JsonWriter.WriteObject<Map<String, ResponseData>> JSON_WRITER = (writer, value) -> instance.serializeNullableMap(value, writer);

  private com.dslplatform.json.JsonReader.ReadObject<net.pieroxy.conkw.webapp.model.ResponseData> reader;
  private com.dslplatform.json.JsonReader.ReadObject<net.pieroxy.conkw.webapp.model.ResponseData> reader() {
    if (reader == null) {
      java.lang.reflect.Type manifest = net.pieroxy.conkw.webapp.model.ResponseData.class;
      reader = (JsonReader.ReadObject<ResponseData>) JsonHelper.getJson().tryFindReader(manifest);
      if (reader == null) {
        throw new com.dslplatform.json.ConfigurationException("Unable to find reader for " + manifest + ". Enable runtime conversion by initializing DslJson with new DslJson<>(Settings.basicSetup())");
      }
    }
    return reader;
  }

  public HashMapStringResponseDataConverter() {
  }

  @Override
  protected ResponseData deserializeNullableValue(JsonReader reader) throws IOException {
    return reader().read(reader);
  }

  @Override
  protected int getMapKey() {
    return -12;
  }
}
