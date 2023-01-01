package net.pieroxy.conkw.utils;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;

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

  /**
   * Serialize the object provided into the file provided.
   * @param o
   * @param f
   * @throws IOException
   */
  public static void writeToFile(Object o, File f) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(f)) {
      writeToOutputStream(o, fos);
    }
  }

  /**
   * Serialize the object provided into the OutputStream provided.
   * @param o
   * @param os
   * @throws IOException
   */
  public static void writeToOutputStream(Object o, OutputStream os) throws IOException {
    DslJson<Object> json = JsonHelper.getJson();
    JsonWriter w = JsonHelper.getWriter();

    synchronized (w) {
      w.reset(os);
      json.serialize(w, o);
      w.flush();
    }
  }

  /**
   *
   * @param type
   * @param f
   * @param <T>
   * @return null if the file could not be found
   * @throws IOException
   */
  public static <T> T readFromFile(Class<T> type, File f) throws IOException {
    if (f.exists()) {
      try (InputStream is = new FileInputStream(f)) {
        return getJson().deserialize(type, is);
      }
    }
    return null;
  }

  /**
   *
   * @param type
   * @param is
   * @param <T>
   * @return The deserialized object
   * @throws IOException
   */
  public static <T> T readFromInputStream(Class<T> type, InputStream is) throws IOException {
    return getJson().deserialize(type, is);
  }

  /**
   *
   * @param type
   * @param input
   * @param <T>
   * @return The deserialized object
   * @throws IOException
   */
  public static <T> T readFromString(Class<T> type, String input) throws IOException {
    byte[]data = input.getBytes(StandardCharsets.UTF_8);
    return getJson().deserialize(type, data, data.length);
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

  /**
   * This method is pretty memory intensive, and so should seldom be used.
   * @param config
   * @return a clone of the input object, as per the JSON annotations. Some fields may be omitted.
   */
  public static <T> T clone(T config) throws Exception {
    return (T)readFromString(config.getClass(), toString(config));
  }
}
