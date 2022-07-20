package net.pieroxy.conkw.services;

import net.pieroxy.conkw.api.model.Dashboard;
import net.pieroxy.conkw.api.model.DashboardSummary;
import net.pieroxy.conkw.config.LocalStorageManager;
import net.pieroxy.conkw.utils.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DashboardService {
  private final static Logger LOGGER = Logger.getLogger(UserSessionService.class.getName());
  private final File folder;

  public DashboardService(LocalStorageManager localStorageManager) {
    folder = localStorageManager.getDashboardsDir();
    if (!folder.exists()) {
      folder.mkdir();
      LOGGER.info("Created the dashboard directory.");
    }

  }

  public Dashboard createNewDashboard(String name) {
    String id = UUID.randomUUID().toString();
    Dashboard d = new Dashboard();
    d.setId(id);
    d.setName(name);
    saveDashboard(d);
    return d;
  }

  private void saveDashboard(Dashboard d) {
    try {
      JsonHelper.writeToFile(d, getFile(d));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File getFile(Dashboard d) {
    return new File(folder, d.getId()+".json");
  }

  public List<DashboardSummary> getSummaries() {
    File[] files = folder.listFiles();
    if (files == null) files = new File[0];
    return Arrays.stream(files).map(this::loadDashboard).map(DashboardSummary::new).collect(Collectors.toList());
  }

  public Dashboard loadDashboard(File f) {
    try {
      return JsonHelper.readFromFile(Dashboard.class, f);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
