package net.pieroxy.conkw.webapp;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.webapp.grabbers.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Listener implements ServletContextListener {

  Config config;

  private List<Grabber> grabbers;
  private WatchService watchService;
  private boolean toDestroy = false;

  public void loadConfig() {
    Config config = ConfigReader.getConfig();
    try {
      grabbers = new ArrayList<>();
      for (GrabberConfig gc : config.getGrabbers()) {
        Grabber g = (Grabber) Class.forName("net.pieroxy.conkw.webapp.grabbers." + gc.getId() + "Grabber").newInstance();
        g.initConfig(ConfigReader.getHomeDir(), gc.getParameters());
        grabbers.add(g);
      }

      Api.setGrabbers(grabbers);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {

    loadConfig();

    try {
      watchService = FileSystems.getDefault().newWatchService();
      Path dir = ConfigReader.getConfDir().toPath();
      dir.register(watchService,
          StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_DELETE,
          StandardWatchEventKinds.ENTRY_MODIFY,
          StandardWatchEventKinds.OVERFLOW);
    } catch (IOException e) {
      System.out.println("Cannot watch config directory. Config file will not be hot swappable.");
      e.printStackTrace();
    }

    new Thread(() -> runThread(), "ConfigListener").start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    toDestroy = true;
    try {
      Api.close();
    } catch (Exception e) {
    }
    try {
      watchService.close();
    } catch (IOException e) {
    }
    for (Grabber g: grabbers) {
      g.dispose();
    }
  }

  public void runThread() {
    WatchKey key;
    while (true) {
      if (toDestroy) return;
      try {
        key = watchService.take();
        boolean shouldReload = false;
        for (WatchEvent<?> event : key.pollEvents()) {
          if (ConfigReader.NAME.equals(event.context().toString()))
            shouldReload = true;
        }
        if (shouldReload) loadConfig();
        key.reset();
      } catch (java.nio.file.ClosedWatchServiceException e) {
        if (toDestroy) return;
      } catch (Exception e) {
      }
    }
  }
}
