package net.pieroxy.conkw.api.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.DashboardPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@TypeScriptType
@CompiledJson
public class Dashboard {
  private String id;
  private String name;
  public List<DashboardPanel> getPanels() {
    return panels;
  }

  public void setPanels(List<DashboardPanel> panels) {
    this.panels = panels;
  }

  private List<DashboardPanel> panels;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   * @param panel
   * @return the ID of the newly added panel
   */
  public String addPanel(DashboardPanel panel) {
    panel.setId(UUID.randomUUID().toString());
    if (panels == null) panels = new ArrayList<>();
    panels.add(0, panel);
    return panel.getId();
  }

  public DashboardPanel getPanel(String panelId) {
    if (panels == null) return null;
    for (DashboardPanel panel : panels) {
      if (panel.getId().equals(panelId)) return panel;
    }
    return null;
  }

  public void replacePanel(DashboardPanel panel) {
    for (int i=0 ; i< panels.size() ; i++) {
      if (panels.get(i).getId().equals(panel.getId())) {
        panels.set(i, panel);
        return;
      }
    }
  }
}
