package net.pieroxy.conkw.utils.duration;

import com.dslplatform.json.*;

import java.io.IOException;

@JsonConverter(target=CDuration.class)
public class CDurationParser {
  public static CDuration getOrDefault(CDuration dur, String defaultValue) {
    if (dur == null) return parse(defaultValue);
    return dur;
  }
  public static CDuration parse(String s) {
    if (s.endsWith("ms")) {
      return new CDuration(parse(s, 2), true);
    } else if (s.endsWith("s")) {
      return new CDuration(parse(s, 1));
    } else if (s.endsWith("m")) {
      return new CDuration(parse(s, 1)*60);
    } else if (s.endsWith("h")) {
      return new CDuration(parse(s, 1)*3600);
    } else if (s.endsWith("d")) {
      return new CDuration(parse(s, 1)*86400);
    } else if (s.endsWith("y")) {
      return new CDuration(parse(s, 1)*31536000);
    }
    return new CDuration(-1);
  }

  private static long parse(String s, int suffixSize) {
    try {
      return Long.parseLong(s.substring(0, s.length()-suffixSize));
    } catch (Exception e) {
      return -1;
    }
  }

  static String toString(CDuration d) {
    if (d.asSeconds() < 0) return  "-1s";
    if (d.asSeconds() == 0) return  "0s";
    if (d.asSeconds()%60 != 0) return d.asSeconds()+"s";
    if (d.asMinutes()%60 != 0) return d.asMinutes()+"m";
    if (d.asHours()%24 != 0) return d.asHours()+"h";
    if (d.asDays()%365 != 0) return d.asDays()+"d";
    return d.asYears()+"y";
  }

  public static final JsonReader.ReadObject<CDuration> JSON_READER = new JsonReader.ReadObject<CDuration>() {
    public CDuration read(JsonReader reader) throws IOException {
      String value = StringConverter.deserialize(reader);
      return parse(value);
    }
  };
  public static final JsonWriter.WriteObject<CDuration> JSON_WRITER = new JsonWriter.WriteObject<CDuration>() {
    public void write(JsonWriter writer, CDuration value) {
      if (value == null) {
        writer.writeNull();
      } else {
        StringConverter.serialize(CDurationParser.toString(value), writer);
      }
    }
  };
}
