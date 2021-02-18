package net.pieroxy.conkw.config;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class Config {
  private GrabberConfig[]grabbers;
  private int httpPort;

  public GrabberConfig[] getGrabbers() {
    return grabbers;
  }

  public int getHttpPort() {
    return httpPort;
  }

  public void setHttpPort(int httpPort) {
    this.httpPort = httpPort;
  }

  public void setGrabbers(GrabberConfig[] grabbers) {
    this.grabbers = grabbers;
  }
}
