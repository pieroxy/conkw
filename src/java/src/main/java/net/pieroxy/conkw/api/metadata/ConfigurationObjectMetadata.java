package net.pieroxy.conkw.api.metadata;

import com.dslplatform.json.CompiledJson;

@CompiledJson
@TypeScriptType
public class ConfigurationObjectMetadata {
  private final String className;

  public ConfigurationObjectMetadata(String className) {
    this.className = className;
  }

  public String getClassName() {
    return className;
  }
}
