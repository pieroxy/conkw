package net.pieroxy.conkw.config;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class Config {
  private GrabberConfig[]grabbers;
  private int httpPort;
  private boolean disableDefaultUI;
  private boolean disableCustomUI;
  private boolean disableApi;
  private boolean disableEmi;

  public boolean disableTomcat() {
    return isDisableApi() && isDisableEmi() && isDisableDefaultUI() && isDisableCustomUI();
  }


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

  public boolean isDisableDefaultUI() {
    return disableDefaultUI;
  }

  public void setDisableDefaultUI(boolean disableDefaultUI) {
    this.disableDefaultUI = disableDefaultUI;
  }

  public boolean isDisableCustomUI() {
    return disableCustomUI;
  }

  public void setDisableCustomUI(boolean disableCustomUI) {
    this.disableCustomUI = disableCustomUI;
  }

  public boolean isDisableApi() {
    return disableApi;
  }

  public void setDisableApi(boolean disableApi) {
    this.disableApi = disableApi;
  }

  public boolean isDisableEmi() {
    return disableEmi;
  }

  public void setDisableEmi(boolean disableEmi) {
    this.disableEmi = disableEmi;
  }
}
