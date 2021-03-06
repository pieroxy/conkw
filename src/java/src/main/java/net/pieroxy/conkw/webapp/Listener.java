package net.pieroxy.conkw.webapp;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.webapp.grabbers.*;
import net.pieroxy.conkw.webapp.servlets.Api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener implements ServletContextListener {
  private final static Logger LOGGER = Logger.getLogger(Listener.class.getName());

  Config config;

  private List<Grabber> grabbers;
  private WatchService watchService;
  private boolean toDestroy = false;

  public void loadConfig() {
    Config config = ConfigReader.getConfig();
    try {
      List<Grabber> newg = new ArrayList<>();
      Set<String> newgnames = new HashSet<>();
      for (GrabberConfig gc : config.getGrabbers()) {
        Grabber g = (Grabber) Class.forName("net.pieroxy.conkw.webapp.grabbers." + gc.getId() + "Grabber").newInstance();

        // Extract
        if (gc.getExtract()!=null && !gc.getExtract().equals("all") && !gc.getExtract().equals("")) {
          g.setExtractProperties(gc.getExtract().split(","));
        }
        // logLevel
        String llas = gc.getLogLevel();
        if (llas!=null) {
          Level logLevel = Level.parse(llas);
          if (logLevel == null) {
            logLevel = Level.INFO;
            LOGGER.severe("Could not parse log level " + llas + ". Using INFO.");
          }
          g.setLogLevel(logLevel);
        }
        // Name
        if (gc.getName() != null) {
          g.setName(gc.getName());
        }
        g.initConfig(ConfigReader.getHomeDir(), gc.getParameters());

        if (newgnames.contains(g.getName())) {
          throw new IllegalArgumentException("At least two grabbers share the same name: " + g.getName());
        } else {
          newg.add(g);
          newgnames.add(g.getName());
        }
      }

      Collection<Grabber> old= grabbers;
      if (old!=null) {
        LOGGER.info("Reloading configuration.");
        // this is a hot swap :
        // Start threads and all.
        newg.forEach((gr) -> gr.grab());
        // Replace the grabbers.
        grabbers = newg;
        // Recycle the old grabbers.
        old.forEach((gr) -> gr.dispose());
        old = null;
      } else {
        grabbers = newg;
      }
      // This forces newly generated garbage to be recycled. Benefits:
      // - The user expects some slowliness when reloading the configuration, so the cost is low.
      // - The heap size by default (when the JVM starts) is around 1GB, which is 20x what this program needs. Forcing a
      //   GC shrinks it back closer to whatever is needed.
      System.gc();

      Api.setAllGrabbers(grabbers);
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
      LOGGER.log(Level.SEVERE, "Cannot watch config directory. Config file will not be hot swappable.", e);
    }

    new Thread(() -> runThread(), "ConfigListener").start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    toDestroy = true;
    try {
      Api.close();
    } catch (Exception e) {
      LOGGER.log(Level.FINE, "", e);
    }
    try {
      watchService.close();
    } catch (IOException e) {
      LOGGER.log(Level.FINE, "", e);
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
