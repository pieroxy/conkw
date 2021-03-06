package net.pieroxy.conkw.config;

import com.dslplatform.json.CompiledJson;

import java.util.Map;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class GrabberConfig {
  private String implementation;
  private String name;
  private String extract;
  private String logLevel;
  private Map<String, String> parameters;

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public String getExtract() {
    return extract;
  }

  public void setExtract(String extract) {
    this.extract = extract;
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
}
