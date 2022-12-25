package net.pieroxy.conkw.api.metadata.grabberConfig;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

import java.util.ArrayList;
import java.util.List;

@CompiledJson
@TypeScriptType
public class ConfigurationObjectMetadata {
  private String className;
  private List<ConfigurationObjectFieldMetadata> fields = new ArrayList<>();

  public ConfigurationObjectMetadata() {
  }

  public ConfigurationObjectMetadata(String className) {
    this.className = className;
  }

  public String getClassName() {
    return className;
  }

  public List<ConfigurationObjectFieldMetadata> getFields() {
    return fields;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setFields(List<ConfigurationObjectFieldMetadata> fields) {
    this.fields = fields;
  }
}
