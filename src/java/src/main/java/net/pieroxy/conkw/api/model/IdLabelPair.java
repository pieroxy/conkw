package net.pieroxy.conkw.api.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@TypeScriptType
@CompiledJson
public class IdLabelPair {
  private String id;
  private String label;

  public IdLabelPair() {
  }

  public IdLabelPair(String id, String label) {
    this.id = id;
    this.label = label;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
