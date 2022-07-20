package net.pieroxy.conkw.api.implementations.dashboards;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.Dashboard;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

import java.util.logging.Logger;

@Endpoint(
    method = ApiMethod.POST,
    role = UserRole.VIEWER)
public class CreateNewDashboardEndpoint  extends AbstractApiEndpoint<CreateNewDashboardInput, CreateNewDashboardOutput> {
  private final static Logger LOGGER = Logger.getLogger(CreateNewDashboardEndpoint.class.getName());

  private final Services services;

  public CreateNewDashboardEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public CreateNewDashboardOutput process(CreateNewDashboardInput input, User user) throws Exception {
    Dashboard d = services.getDashboardService().createNewDashboard(input.getName());
    CreateNewDashboardOutput doutput = new CreateNewDashboardOutput();
    doutput.setId(d.getId());
    return doutput;
  }
}

@CompiledJson
@TypeScriptType
class CreateNewDashboardInput {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

@CompiledJson
@TypeScriptType
class CreateNewDashboardOutput {
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}