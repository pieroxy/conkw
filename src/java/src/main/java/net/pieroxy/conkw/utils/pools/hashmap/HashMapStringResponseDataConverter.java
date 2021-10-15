package net.pieroxy.conkw.utils.pools.hashmap;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.model._ResponseData_DslJsonConverter;

import java.io.IOException;
import java.util.Map;

public class HashMapStringResponseDataConverter extends AbstractMapConverter<ResponseData> {
  private static HashMapStringResponseDataConverter instance = new HashMapStringResponseDataConverter();
  private static _ResponseData_DslJsonConverter.ObjectFormatConverter converter = new _ResponseData_DslJsonConverter.ObjectFormatConverter(JsonHelper.getJson());

  public static final JsonReader.ReadObject<Map<String, ResponseData>> JSON_READER = reader -> reader.wasNull() ? null : instance.deserializeMap(reader);
  public static final JsonWriter.WriteObject<Map<String, ResponseData>> JSON_WRITER = (writer, value) -> instance.serializeNullableMap(value, writer);

  public HashMapStringResponseDataConverter() {
  }

  @Override
  protected ResponseData deserializeNullableValue(JsonReader reader) throws IOException {
    return converter.read(reader);
  }

  @Override
  protected int getMapKey() {
    return -12;
  }
}
