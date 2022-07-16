package net.pieroxy.conkw.api.model;

import net.pieroxy.conkw.api.metadata.TypeScriptType;

import java.io.File;

@TypeScriptType
public class DashboardSummary {
  String name;

  public DashboardSummary(File dashboard) {
    this.name = dashboard.getName();
  }
}
