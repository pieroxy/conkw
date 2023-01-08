package net.pieroxy.conkw.config;

import java.io.File;

public class LocalStorageManager {
  private File homeDir;

  public LocalStorageManager(File homeDir) {
    this.homeDir = homeDir;
  }

  public File getDataDir() {
    return new File(getHomeDir(), "data");
  }

  public File getConfDir() {
    return new File(getHomeDir(), "config");
  }

  public File getTmpDir() {
    return new File(getHomeDir(), "tmp");
  }

  public File getBinDir() {
    return new File(getHomeDir(), "bin");
  }

  public File getLogDir() {
    return new File(getHomeDir(), "log");
  }

  public File getWebappDir() {
    return new File(getHomeDir(), "webapp/ROOT");
  }

  public File getUiDir() {
    return new File(getHomeDir(), "ui");
  }

  public File getWebappDataDir() {
    return new File(getHomeDir(), "data/webapp");
  }

  public File getHomeDir() {
    return homeDir;
  }
}
