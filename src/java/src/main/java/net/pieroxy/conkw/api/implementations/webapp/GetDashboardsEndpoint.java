package net.pieroxy.conkw.api.implementations.webapp;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.DashboardSummary;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

import java.util.List;

@Endpoint(
    method = ApiMethod.GET,
    role = UserRole.VIEWER)
public class GetDashboardsEndpoint extends AbstractApiEndpoint<GetDashboardsInput, GetDashboardsOutput> {
  private final Services services;

  public GetDashboardsEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public GetDashboardsOutput process(GetDashboardsInput input, User user) throws Exception {
    return new GetDashboardsOutput(services.getDashboardService().getSummaries());
  }
}

@TypeScriptType
@CompiledJson
class GetDashboardsInput{
  String root;

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }
}

@TypeScriptType
@CompiledJson
class GetDashboardsOutput {
  private List<DashboardSummary> list;

  public GetDashboardsOutput(List<DashboardSummary> list) {
    this.list = list;
  }

  public List<DashboardSummary> getList() {
    return list;
  }

  public void setList(List<DashboardSummary> list) {
    this.list = list;
  }
}