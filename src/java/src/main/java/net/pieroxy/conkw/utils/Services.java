package net.pieroxy.conkw.utils;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.CredentialsStore;
import net.pieroxy.conkw.config.LocalStorageManager;
import net.pieroxy.conkw.services.DashboardService;
import net.pieroxy.conkw.services.GrabbersService;
import net.pieroxy.conkw.services.UserService;
import net.pieroxy.conkw.services.UserSessionService;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;
import net.pieroxy.conkw.webapp.servlets.ApiManager;

public class Services {
  private final DashboardService dashboardService;
  private ApiAuthManager apiAuthManager;
  private CredentialsStore credentialsStore;
  private Config configuration;
  private ApiManager apiManager;
  private LocalStorageManager localStorageManager;
  private final UserService userService;
  private final UserSessionService userSessionService;
  private final GrabbersService grabbersService;

  public Services(LocalStorageManager localStorageManager) {
    this.localStorageManager = localStorageManager;
    this.userService = new UserService(this);
    this.userSessionService = new UserSessionService(localStorageManager);
    this.dashboardService = new DashboardService(localStorageManager);
    this.grabbersService = new GrabbersService(this);
  }

  public void dispose() {
    userSessionService.dispose();
  }

  public ApiAuthManager getApiAuthManager() {
    return apiAuthManager;
  }

  public void setApiAuthManager(ApiAuthManager apiAuthManager) {
    this.apiAuthManager = apiAuthManager;
  }

  public CredentialsStore getCredentialsStore() {
    return credentialsStore;
  }

  public void setCredentialsStore(CredentialsStore credentialsStore) {
    this.credentialsStore = credentialsStore;
  }

  public Config getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Config configuration) {
    this.configuration = configuration;
  }

  public ApiManager getApiManager() {
    return apiManager;
  }

  public void setApiManager(ApiManager apiManager) {
    this.apiManager = apiManager;
  }

  public void close() {
    apiAuthManager.close();
  }

  public LocalStorageManager getLocalStorageManager() {
    return localStorageManager;
  }

  public void setLocalStorageManager(LocalStorageManager localStorageManager) {
    this.localStorageManager = localStorageManager;
  }

  public UserService getUserService() {
    return userService;
  }

  public UserSessionService getUserSessionService() {
    return userSessionService;
  }

  public DashboardService getDashboardService() {
    return dashboardService;
  }

  public GrabbersService getGrabbersService() {
    return grabbersService;
  }
}
