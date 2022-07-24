package net.pieroxy.conkw.api.implementations.dashboards;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.Dashboard;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.api.model.panels.DashboardPanel;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

@Endpoint(
    method = ApiMethod.GET,
    role = UserRole.DESIGNER)
public class GetDashboardPanelEndpoint extends AbstractApiEndpoint<GetDashboardPanelInput, GetDashboardPanelOutput> {
  private final Services services;

  public GetDashboardPanelEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public GetDashboardPanelOutput process(GetDashboardPanelInput input, User user) throws Exception {
    Dashboard dashboard = services.getDashboardService().getDashboardById(input.getDashboardId());
    DashboardPanel panel = dashboard.getPanel(input.getPanelId());
    return new GetDashboardPanelOutput(dashboard.getId(), panel);
  }
}

@CompiledJson
@TypeScriptType
class GetDashboardPanelInput {
  private String dashboardId;
  private String panelId;

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

@CompiledJson
@TypeScriptType
class GetDashboardPanelOutput {
  private String dashboardId;
  private DashboardPanel panel;

  public GetDashboardPanelOutput() {
  }

  public GetDashboardPanelOutput(String dashboardId, DashboardPanel panel) {
    this.dashboardId = dashboardId;
    this.panel = panel;
  }

  public String getDashboardId() {
    return dashboardId;
  }

  public void setDashboardId(String dashboardId) {
    this.dashboardId = dashboardId;
  }

  public DashboardPanel getPanel() {
    return panel;
  }

  public void setPanel(DashboardPanel panel) {
    this.panel = panel;
  }
}