package net.pieroxy.conkw.api.implementations.grabbers;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint(
    method = ApiMethod.GET,
    role = UserRole.ADMIN)
public class GetGrabbersDetailEndpoint  extends AbstractApiEndpoint<Object, GetGrabbersDetailOutput> {

  private final Services services;

  public GetGrabbersDetailEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public GetGrabbersDetailOutput process(Object input, User user) throws Exception {
    return new GetGrabbersDetailOutput(services.getConfiguration().getGrabbers());
  }
}

@CompiledJson
@TypeScriptType
class GetGrabbersDetailOutput {
  List<GrabberConfig> configs;

  public GetGrabbersDetailOutput() {
  }

  public GetGrabbersDetailOutput(GrabberConfig[]configs) {
    this.configs = Arrays.stream(configs).collect(Collectors.toList());
  }

  public List<GrabberConfig> getConfigs() {
    return configs;
  }

  public void setConfigs(List<GrabberConfig> configs) {
    this.configs = configs;
  }
}