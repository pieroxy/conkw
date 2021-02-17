package net.pieroxy.conkw;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;

public class JsonHelper {

  private static DslJson<Object> dslJson;
  private static JsonWriter writer;

  public static DslJson<Object> getJson() {
    if (dslJson == null) {
      dslJson = new DslJson<>(Settings.basicSetup());
      writer = dslJson.newWriter();
    }
    return dslJson;
  }
  public static JsonWriter getWriter() {
    if (dslJson == null) getJson();
    return writer;
  }
}
