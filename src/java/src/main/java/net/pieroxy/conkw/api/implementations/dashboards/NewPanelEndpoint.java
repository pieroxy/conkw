package net.pieroxy.conkw.api.implementations.dashboards;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.Dashboard;
import net.pieroxy.conkw.api.model.DashboardPanelType;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.api.model.panels.SimpleGaugeWithValueAndLabelPanel;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

@Endpoint(
    method = ApiMethod.POST,
    role = UserRole.DESIGNER)
public class NewPanelEndpoint extends AbstractApiEndpoint<NewPanelInput, NewPanelOutput> {
  private final Services services;

  public NewPanelEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public NewPanelOutput process(NewPanelInput input, User user) throws Exception {
    Dashboard dashboard = services.getDashboardService().getDashboardById(input.getDashboardId());

    String newId = null;
    switch (input.getType()) {
      case SIMPLE_GAUGE_WITH_VALUE_AND_LABEL:
        newId = dashboard.addPanel(new SimpleGaugeWithValueAndLabelPanel());
        services.getDashboardService().saveDashboard(dashboard);
        break;
    }
    return new NewPanelOutput(dashboard.getId(), newId);
  }
}

@CompiledJson
@TypeScriptType
class NewPanelInput {
  private String dashboardId;
  private DashboardPanelType type;

  public String getDashboardId() {
    return dashboardId;
  }

  public void setDashboardId(String dashboardId) {
    this.dashboardId = dashboardId;
  }

  public DashboardPanelType getType() {
    return type;
  }

  public void setType(DashboardPanelType type) {
    this.type = type;
  }
}

@CompiledJson
@TypeScriptType
class NewPanelOutput {
  private String dashboardId;
  private String panelId;

  public NewPanelOutput() {
  }

  public NewPanelOutput(String dashboardId, String panelId) {
    this.dashboardId = dashboardId;
    this.panelId = panelId;
  }

  public String getDashboardId() {
    return dashboardId;
  }

  public void setDashboardId(String dashboardId) {
    this.dashboardId = dashboardId;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }
}