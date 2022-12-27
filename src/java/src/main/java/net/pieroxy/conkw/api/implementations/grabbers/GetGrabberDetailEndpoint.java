package net.pieroxy.conkw.api.implementations.grabbers;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.NonNull;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.metadata.grabberConfig.ConfigurationObjectMetadata;
import net.pieroxy.conkw.api.metadata.grabberConfig.ConfigurationObjectMetadataBuilder;
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
          .filter(c -> c.getClass().getName().equals(input.getImplementation()))
          .filter(c->
              Utils.objectEquals(c.getName(), input.getName()) ||
                  (StringUtil.isNullOrEmptyTrimmed(input.getName()) && c.getName().equals(c.getDefaultName()))
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

  private String defaultName;
  private ConfigurationObjectMetadata detailsMetadata;

  public GetGrabberDetailOutput() {
  }

  public GetGrabberDetailOutput(GrabberConfig config, Grabber grabber) {
    this.defaultName = grabber.getDefaultName();
    this.config = new GrabberConfigDetail(config);
    try {
      this.detailsMetadata = ConfigurationObjectMetadataBuilder.buildMetadata(((Grabber)Class.forName(config.getImplementation()).newInstance()).getConfigClass());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public GrabberConfigDetail getConfig() {
    return config;
  }

  public void setConfig(GrabberConfigDetail config) {
    this.config = config;
  }

  public String getDefaultName() {
    return defaultName;
  }

  public void setDefaultName(String defaultName) {
    this.defaultName = defaultName;
  }

  public ConfigurationObjectMetadata getDetailsMetadata() {
    return detailsMetadata;
  }

  public void setDetailsMetadata(ConfigurationObjectMetadata detailsMetadata) {
    this.detailsMetadata = detailsMetadata;
  }
}

