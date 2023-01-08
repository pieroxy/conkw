package net.pieroxy.conkw.api.implementations.webapp;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.Dashboard;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

@Endpoint(
    method = ApiMethod.GET,
    role = UserRole.VIEWER)
public class GetDashboardEndpoint extends AbstractApiEndpoint<GetDashboardInput, GetDashboardOutput> {
  private final Services services;

  public GetDashboardEndpoint(Services services) {
    this.services = services;
  }


  @Override
  public GetDashboardOutput process(GetDashboardInput input, User user) throws Exception {
    return new GetDashboardOutput(services.getDashboardService().getDashboardById(input.getId()));
  }
}

@TypeScriptType
@CompiledJson
class GetDashboardInput {
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}

@TypeScriptType
@CompiledJson
class GetDashboardOutput {
  private Dashboard dashboard;

  GetDashboardOutput(Dashboard dashboard) {
    this.dashboard = dashboard;
  }

  public Dashboard getDashboard() {
    return dashboard;
  }

  public void setDashboard(Dashboard dashboard) {
    this.dashboard = dashboard;
  }
}