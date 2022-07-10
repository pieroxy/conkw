package net.pieroxy.conkw.api.model;

import net.pieroxy.conkw.api.metadata.TypeScriptType;

@TypeScriptType
public class ClientInfo {
  private String appVersion;
  private String gitRevision;
  private long gitDepth;

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getGitRevision() {
    return gitRevision;
  }

  public void setGitRevision(String gitRevision) {
    this.gitRevision = gitRevision;
  }

  public long getGitDepth() {
    return gitDepth;
  }

  public void setGitDepth(long gitDepth) {
    this.gitDepth = gitDepth;
  }
}
