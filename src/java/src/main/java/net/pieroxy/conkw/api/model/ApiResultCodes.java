package net.pieroxy.conkw.api.model;

import net.pieroxy.conkw.api.metadata.TypeScriptType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TypeScriptType
public enum ApiResultCodes {
  OK(0),
  GO_TO_LOGIN(1),
  DISPLAY_ERROR(2),
  TECH_ERROR(3);

  final int value;

  ApiResultCodes(int value) {
    this.value = value;
  }

  private static final Map<Integer, ApiResultCodes> ENUM_MAP;

  static {
    Map<Integer, ApiResultCodes> map = new HashMap<>();
    for (ApiResultCodes
        instance : ApiResultCodes.values()) {
      map.put(instance.value, instance);
    }
    ENUM_MAP = Collections.unmodifiableMap(map);
  }

  public static ApiResultCodes fromCode(int code) {
    return ENUM_MAP.getOrDefault(code, null);
  }
}
