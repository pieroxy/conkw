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
public class GetGrabbersSummaryEndpoint extends AbstractApiEndpoint<Object, GetGrabbersSummaryOutput> {

  private final Services services;

  public GetGrabbersSummaryEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public GetGrabbersSummaryOutput process(Object input, User user) throws Exception {
    return new GetGrabbersSummaryOutput(services.getConfiguration().getGrabbers());
  }
}

@CompiledJson
@TypeScriptType
class GetGrabbersSummaryOutput {
  List<GrabberConfigSummary> configs;

  public GetGrabbersSummaryOutput() {
  }

  public GetGrabbersSummaryOutput(GrabberConfig[]configs) {
    this.configs = Arrays.stream(configs).map(GrabberConfigSummary::new).collect(Collectors.toList());
  }

  public List<GrabberConfigSummary> getConfigs() {
    return configs;
  }

  public void setConfigs(List<GrabberConfigSummary> configs) {
    this.configs = configs;
  }
}

@CompiledJson
@TypeScriptType
class GrabberConfigSummary {
  private String implementation;
  private String name;
  private String logLevel;

  public GrabberConfigSummary() {
  }

  public GrabberConfigSummary(GrabberConfig config) {
    this.implementation = config.getImplementation();
    this.logLevel = config.getLogLevel();
    this.name = config.getName();
  }

  public String getImplementation() {
    return implementation;
  }

  public void setImplementation(String implementation) {
    this.implementation = implementation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }
}