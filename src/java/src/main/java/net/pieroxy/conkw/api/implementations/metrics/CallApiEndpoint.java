package net.pieroxy.conkw.api.implementations.metrics;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.webapp.model.MetricsApiResponse;
import net.pieroxy.conkw.webapp.servlets.GrabberInput;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Endpoint(method = ApiMethod.GET, role = UserRole.VIEWER)
public class CallApiEndpoint extends AbstractApiEndpoint<CallApiInput, CallApiOutput> {
  private final static Logger LOGGER = Logger.getLogger(CallApiEndpoint.class.getName());
  private final Services services;

  public CallApiEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public CallApiOutput process(CallApiInput input, User user) throws Exception {
    long now = System.currentTimeMillis();
    MetricsApiResponse r =
             services
                 .getApiManager()
                 .buildResponse(now, Arrays.stream(input.getGrabbers())
                     .map(s -> new GrabberInput(s))
                     .collect(Collectors.toList()));
      return new CallApiOutput(r);
  }
}


@CompiledJson
@TypeScriptType
class CallApiInput {
  private String[]grabbers;

  public String[] getGrabbers() {
    return grabbers;
  }

  public void setGrabbers(String[] grabbers) {
    this.grabbers = grabbers;
  }
}

@CompiledJson
@TypeScriptType
class CallApiOutput implements Closeable {
  private MetricsApiResponse data;

  public CallApiOutput(MetricsApiResponse data) {
    this.data = data;
  }

  public CallApiOutput() {
  }

  public MetricsApiResponse getData() {
    return data;
  }

  public void setData(MetricsApiResponse data) {
    this.data = data;
  }

  @Override
  public void close() throws IOException {
    data.close();
  }
}