package net.pieroxy.conkw.utils.pools.hashmap;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractMapConverter<V> {
  protected abstract V deserializeNullableValue(JsonReader reader) throws IOException;
  protected abstract int getMapKey();

  public void serializeNullableMap(@Nullable Map<String,V> value, JsonWriter sw) {
    if (value == null) {
      sw.writeNull();
    } else {
      serializeMap(value, sw);
    }

  }

  public void serializeMap(Map<String,V> value, JsonWriter sw) {
    sw.writeByte((byte)123);
    int size = value.size();
    if (size > 0) {
      Iterator<Map.Entry<String,V>> iterator = value.entrySet().iterator();
      Map.Entry<String, Object> kv = (Map.Entry)iterator.next();
      sw.writeString(kv.getKey());
      sw.writeByte((byte)58);
      sw.serializeObject(kv.getValue());

      for(int i = 1; i < size; ++i) {
        sw.writeByte((byte)44);
        kv = (Map.Entry)iterator.next();
        sw.writeString(kv.getKey());
        sw.writeByte((byte)58);
        sw.serializeObject(kv.getValue());
      }
    }

    sw.writeByte((byte)125);
  }


  public Map<String,V> deserializeMap(JsonReader reader) throws IOException {
    if (reader.last() != 123) {
      throw reader.newParseError("Expecting '{' for map start");
    } else {
      byte nextToken = reader.getNextToken();
      if (nextToken == 125) {
        return new TreeMap();
      } else {
        Map<String,V> res = HashMapPool.getInstance().borrow(getMapKey()); // This is the important part. The rest is boilerplate copied and pasted from DslJson project.
        String key = reader.readKey();
        res.put(key, deserializeNullableValue(reader));

        while((nextToken = reader.getNextToken()) == 44) {
          reader.getNextToken();
          key = reader.readKey();
          res.put(key, deserializeNullableValue(reader));
        }

        if (nextToken != 125) {
          throw reader.newParseError("Expecting '}' for map end");
        } else {
          return res;
        }
      }
    }
  }
}
