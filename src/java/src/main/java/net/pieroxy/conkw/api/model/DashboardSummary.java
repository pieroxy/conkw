package net.pieroxy.conkw.api.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

import java.io.File;

@TypeScriptType
@CompiledJson
public class DashboardSummary {
  private String name;

  public DashboardSummary(File dashboard) {
    this.name = dashboard.getName();
  }

  public DashboardSummary() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
