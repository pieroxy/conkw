package net.pieroxy.conkw.api.implementations.grabbers;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.NonNull;
import net.pieroxy.conkw.api.metadata.*;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.Utils;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.utils.streams.UtilityCollectors;

import java.util.Arrays;
import java.util.logging.Level;

@Endpoint(
    method = ApiMethod.GET,
    role = UserRole.ADMIN)
public class GetGrabberDetailEndpoint extends AbstractApiEndpoint<GetGrabberDetailInput, GetGrabberDetailOutput> {
  private final Services services;

  public GetGrabberDetailEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public GetGrabberDetailOutput process(GetGrabberDetailInput input, User user) throws Exception {
    GrabberConfig config = null;
    Grabber grabber = null;
    try {
      config = Arrays.stream(services
          .getConfiguration().getGrabbers())
          .filter(c -> c.getImplementation().equals(input.getImplementation()))
          .filter(c -> (
                  (StringUtil.isNullOrEmptyTrimmed(c.getName()) && StringUtil.isNullOrEmptyTrimmed(input.getName()))
                      ||
                      Utils.objectEquals(c.getName(), input.getName())
              ))
          .collect(UtilityCollectors.getOneItemOrNull());
      grabber = services
          .getGrabbersService()
          .getAllGrabbers()
          .filter(c -> c.getClass().getName().equals(input.getImplementation()) &&
              Utils.objectEquals(c.getName(), input.getName())
          )
          .collect(UtilityCollectors.getOneItemOrNull());
    } catch (Exception e) {
      super.getLogger().log(Level.SEVERE, "Could not load grabber from " + JsonHelper.toString(input), e);
    }
    if (config == null || grabber == null) throw new DisplayMessageException("This grabber could not be loaded.");
    return new GetGrabberDetailOutput(config, grabber);
  }
}

@TypeScriptType
@CompiledJson
class GetGrabberDetailInput {
  @NonNull
  private String implementation;
  @NonNull
  private String name;

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
}

@TypeScriptType
@CompiledJson
class GetGrabberDetailOutput {
  private GrabberConfigDetail config;

  public GetGrabberDetailOutput() {
  }

  public GetGrabberDetailOutput(GrabberConfig config, Grabber grabber) {
    this.config = new GrabberConfigDetail(config, grabber);
  }

  public GrabberConfigDetail getConfig() {
    return config;
  }

  public void setConfig(GrabberConfigDetail config) {
    this.config = config;
  }
}

@CompiledJson
@TypeScriptType
class GrabberConfigDetail{
  private String implementation;
  private String name;
  private String defaultName;
  private String logLevel;
  private Object details;
  private ConfigurationObjectMetadata detailsMetadata;

  public GrabberConfigDetail() {
  }

  public GrabberConfigDetail(GrabberConfig config, Grabber grabber) {
    this.implementation = config.getImplementation();
    this.logLevel = config.getLogLevel();
    if (logLevel == null) logLevel = Grabber.DEFAULT_LOG_LEVEL.getName();
    this.name = config.getName();
    this.details = config.getConfig();
    this.defaultName = grabber.getDefaultName();
    try {
      this.detailsMetadata = ConfigurationObjectMetadataBuilder.buildMetadata(((Grabber)Class.forName(config.getImplementation()).newInstance()).getConfigClass());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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

  public Object getDetails() {
    return details;
  }

  public void setDetails(Object details) {
    this.details = details;
  }

  public ConfigurationObjectMetadata getDetailsMetadata() {
    return detailsMetadata;
  }

  public void setDetailsMetadata(ConfigurationObjectMetadata detailsMetadata) {
    this.detailsMetadata = detailsMetadata;
  }

  public String getDefaultName() {
    return defaultName;
  }

  public void setDefaultName(String defaultName) {
    this.defaultName = defaultName;
  }
}