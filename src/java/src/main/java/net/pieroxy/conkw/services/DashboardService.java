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
    folder = localStorageManager.getWebappDataDir();
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

  public void saveDashboard(Dashboard d) {
    try {
      JsonHelper.writeToFile(d, getFile(d));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File getFile(Dashboard d) {
    return getFileForId(d.getId());
  }
  private File getFileForId(String id) {
    return new File(folder, id+".json");
  }

  public List<DashboardSummary> getSummaries() {
    File[] files = folder.listFiles();
    if (files == null) files = new File[0];
    return Arrays.stream(files).map(this::loadDashboard).map(DashboardSummary::new).sorted().collect(Collectors.toList());
  }

  public Dashboard getDashboardById(String id) {
    return loadDashboard(getFileForId(id));
  }

  public Dashboard loadDashboard(File f) {
    try {
      return JsonHelper.readFromFile(Dashboard.class, f);
    } catch (Exception e) {
      LOGGER.severe("Could not read dashboard " + f.getAbsolutePath());
      throw new RuntimeException(e);
    }
  }
}
