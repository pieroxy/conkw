package net.pieroxy.conkw.config;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonObject;

import java.util.Map;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class GrabberConfig {
  private String implementation;
  private String name;

  private String logLevel;
  private Object config;
  private Map<String, String> parameters;
  private Map<String, Map<String, String>> namedParameters;

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImplementation() {
    return implementation;
  }

  public void setImplementation(String implementation) {
    this.implementation = implementation;
  }

  public Map<String, Map<String, String>> getNamedParameters() {
    return namedParameters;
  }

  public void setNamedParameters(Map<String, Map<String, String>> namedParameters) {
    this.namedParameters = namedParameters;
  }

  public Object getConfig() {
    return config;
  }

  public void setConfig(Object config) {
    this.config = config;
  }
}
