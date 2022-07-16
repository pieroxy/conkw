package net.pieroxy.conkw.api.model;

import net.pieroxy.conkw.api.metadata.TypeScriptType;

import java.io.File;

@TypeScriptType
public class DashboardSummary {
  private String name;

  public DashboardSummary(File dashboard) {
    this.name = dashboard.getName();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
