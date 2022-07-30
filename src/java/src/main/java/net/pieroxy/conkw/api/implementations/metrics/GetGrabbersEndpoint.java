package net.pieroxy.conkw.api.implementations.metrics;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.IdLabelPair;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

import java.util.List;

@Endpoint(
    method = ApiMethod.GET,
    role = UserRole.DESIGNER)
public class GetGrabbersEndpoint  extends AbstractApiEndpoint<GetGrabbersInput, GetGrabbersOutput> {

  private final Services services;

  public GetGrabbersEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public GetGrabbersOutput process(GetGrabbersInput input, User user) throws Exception {
    return new GetGrabbersOutput(services.getApiManager().getGrabbersList());
  }
}

@CompiledJson
@TypeScriptType
class GetGrabbersInput {
}

@CompiledJson
@TypeScriptType
class GetGrabbersOutput {
  private List<IdLabelPair> grabbers;

  public GetGrabbersOutput(List<IdLabelPair> grabbers) {
    this.grabbers = grabbers;
  }

  public List<IdLabelPair> getGrabbers() {
    return grabbers;
  }

  public void setGrabbers(List<IdLabelPair> grabbers) {
    this.grabbers = grabbers;
  }
}