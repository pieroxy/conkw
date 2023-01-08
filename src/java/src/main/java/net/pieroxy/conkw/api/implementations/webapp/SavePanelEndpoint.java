package net.pieroxy.conkw.api.implementations.webapp;

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
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;

@Endpoint(
    method = ApiMethod.POST,
    role = UserRole.DESIGNER)
public class SavePanelEndpoint extends AbstractApiEndpoint<SavePanelInput, SavePanelOutput> {

  private final Services services;

  public SavePanelEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public SavePanelOutput process(SavePanelInput input, User user) throws Exception {
    Dashboard dashboard = services.getDashboardService().getDashboardById(input.getDashboardId());
    DashboardPanel panel = dashboard.getPanel(input.getPanel().getId());
    if (panel == null)
      throw new DisplayMessageException("Panel not found.");
    else
      dashboard.replacePanel(input.getPanel());
    services.getDashboardService().saveDashboard(dashboard);
    return new SavePanelOutput();
  }
}

@CompiledJson
@TypeScriptType
class SavePanelInput {
  private String dashboardId;
  private DashboardPanel panel;

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

@CompiledJson
@TypeScriptType
class SavePanelOutput {

}