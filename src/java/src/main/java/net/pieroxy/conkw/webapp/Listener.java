package net.pieroxy.conkw.webapp;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.grabbersBase.GrabberListener;
import net.pieroxy.conkw.utils.config.GrabberConfigReader;
import net.pieroxy.conkw.webapp.servlets.Api;
import net.pieroxy.conkw.webapp.servlets.ApiManager;
import net.pieroxy.conkw.webapp.servlets.Emi;

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
  private ApiManager apiManager;
  private boolean toDestroy = false;

  public void loadConfig() {
    Config config = ConfigReader.getConfig();
    try {
      List<Grabber> newg = new ArrayList<>();
      Set<String> newgnames = new HashSet<>();
      Emi.clearGrabbers();
      for (GrabberConfig gc : config.getGrabbers()) {
        try {
          Grabber g = (Grabber) Class.forName(gc.getImplementation()).newInstance();

          // Name
          if (gc.getName() != null) {
            g.setName(gc.getName());
          }
          // logLevel
          String llas = gc.getLogLevel();
          if (llas != null) {
            Level logLevel = Level.parse(llas);
            if (logLevel == null) {
              logLevel = Level.INFO;
              LOGGER.severe("Could not parse log level " + llas + ". Using INFO.");
            }
            g.setLogLevel(logLevel);
          } else {
            g.setLogLevel(Level.INFO);
          }
          Object defaultConf = g.getDefaultConfig();
          if (defaultConf == null && gc.getConfig()!=null) {
            throw new RuntimeException("Grabber " + g.getGrabberFQN() + " did not provide a default configuration but the config file has one.");
          }
          g.setConfig(GrabberConfigReader.fillObject(g.getDefaultConfig(), gc.getConfig()));
          if (g.getConfig() != null && (gc.getParameters()!=null || gc.getNamedParameters()!=null)) {
            throw new RuntimeException("Grabber " + g.getGrabberFQN() + " has a mix of old and new config.");
          }

          g.initializeGrabber(ConfigReader.getHomeDir());

          if (g.getName() == null) {
            throw new IllegalArgumentException("Grabber name must be defined for grabber " + g.getClass().getName());
          } else {
            if (newgnames.contains(g.getName())) {
              throw new IllegalArgumentException("At least two grabbers share the same name: " + g.getName());
            } else {
              newg.add(g);
              newgnames.add(g.getName());
            }
          }
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Initializing grabber " + gc.getImplementation() + " with name " + gc.getName(), e);
        }
      }

      newg.forEach((gr) -> {
        if (gr instanceof GrabberListener) {
          ((GrabberListener) gr).setGrabberList(new ArrayList<>(newg));
          if (LOGGER.isLoggable(Level.FINE)) LOGGER.log(Level.FINE, "Setting grabbers to class "+ gr.getClass().getName());
        } else {
          if (LOGGER.isLoggable(Level.FINE)) LOGGER.log(Level.INFO, "NOT Setting grabbers to class "+ gr.getClass().getName());
        }
      });

      Collection<Grabber> old= grabbers;
      if (old!=null) {
        LOGGER.info("Reloading configuration.");
        // this is a hot swap :
        // Start threads and all.
        // Stupid? Maybe there is no UI... newg.forEach((gr) -> gr.grab());
        // Replace the grabbers.
        grabbers = newg;
        // Recycle the old grabbers.
        old.forEach((gr) -> gr.dispose());
      } else {
        grabbers = newg;
      }
      // This forces newly generated garbage to be recycled. Benefits:
      // - The user expects some slowliness when reloading the configuration, so the cost is low.
      // - The heap size by default (when the JVM starts) is around 1GB, which is 20x what this program needs. Forcing a
      //   GC shrinks it back closer to whatever is needed.
      System.gc();

      if (apiManager!=null) apiManager.close();
      Api.setContext(apiManager=new ApiManager(grabbers), config.getApiAuth(), ConfigReader.getDataDir());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading and applying configuration.", e);
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
      apiManager.close();
    } catch (Exception e) {
      LOGGER.log(Level.FINE, "", e);
    }
    try {
      watchService.close();
    } catch (IOException e) {
      LOGGER.log(Level.FINE, "", e);
    }
    try {
      Api.close();
    } catch (Exception e) {
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
