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
    long l = Long.parseLong(s.substring(0, s.length()-1));
    switch (s.charAt(s.length()-1)) {
      case 's':
        return new CDuration(l);
      case 'm':
        return new CDuration(l*60);
      case 'h':
        return new CDuration(l*3600);
      case 'd':
        return new CDuration(l*86400);
      case 'y':
        return new CDuration(l*31536000);
    }
    throw new RuntimeException("CDuration not recognized: " + s);
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
