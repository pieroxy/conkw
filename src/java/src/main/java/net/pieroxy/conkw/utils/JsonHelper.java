package net.pieroxy.conkw.utils;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import net.pieroxy.conkw.webapp.grabbers.spotify.CurrentlyPlayingResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class JsonHelper {

  private static DslJson<Object> dslJson;
  private static JsonWriter writer;

  public static DslJson<Object> getJson() {
    if (dslJson == null) {
      DslJson<Object> nj = new DslJson<>(Settings.basicSetup());
      writer = nj.newWriter();
      dslJson = nj;
    }
    return dslJson;
  }
  public static JsonWriter getWriter() {
    if (dslJson == null) getJson();
    return writer;
  }

  public static void writeToFile(Object o, File f) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(f)) {
      DslJson<Object> json = JsonHelper.getJson();
      JsonWriter w = JsonHelper.getWriter();

      synchronized (w) {
        w.reset(fos);
        json.serialize(w, o);
        w.flush();
      }
    }
  }


  /**
   *  For debug purposes only, performances are not optimal to say the least
   */
  public static String toString(Object toDebug) {
    try {
      DslJson<Object> json = JsonHelper.getJson();
      JsonWriter w = JsonHelper.getWriter();
      synchronized (w) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        w.reset(baos);
        json.serialize(w, toDebug);
        w.flush();
        return baos.toString(StandardCharsets.UTF_8.toString());
      }
    } catch (IOException e) {
      return "Could not serialize object.";
    }
  }
}
