package net.pieroxy.conkw.utils;

import java.util.ArrayList;
import java.util.List;

public class TextReplacer {
  List<String> keys = new ArrayList<>();
  List<String> values = new ArrayList<>();

  public TextReplacer add(String key, String value) {
    keys.add(key);
    values.add(value);
    return this;
  }

  public String replace(String input) {
    if (input==null) return null;
    for (int i=0 ; i< keys.size() ; i++) {
      input = input.replace(keys.get(i), values.get(i));
    }
    return input;
  }
}
