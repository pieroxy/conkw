package net.pieroxy.conkw.webapp;

import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.config.CredentialsStore;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.grabbersBase.GrabberListener;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.config.GrabberConfigReader;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;
import net.pieroxy.conkw.webapp.servlets.ApiManager;
import net.pieroxy.conkw.webapp.servlets.Emi;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Listener implements ServletContextListener {
  private final static Logger LOGGER = Logger.getLogger(Listener.class.getName());

  private List<Grabber> grabbers;
  private WatchService watchService;
  private ApiManager apiManager;
  private boolean toDestroy = false;
  private final Services services;
  private final Runnable onDestroy;
  private static String instanceName;

  public Listener(Services services, Runnable onDestroy) {
    this.services = services;
    this.onDestroy = onDestroy;
    services.setApiAuthManager(new ApiAuthManager());
  }

  public void loadConfig() {
    Config config = ConfigReader.getConfig();
    instanceName = config.getInstanceName();
    CredentialsStore creds = ConfigReader.getCredentials();
    services.setCredentialsStore(creds);
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
          g.setDefaultAccumulator(new AccumulatorExpressionParser().parse( gc.getDefaultAccumulator()));
          g.setConfig(GrabberConfigReader.fillObject(g.getDefaultConfig(), gc.getConfig()), creds.getFor(g.getClass()));
          if (gc.getParameters()!=null || gc.getNamedParameters()!=null || gc.getExtract()!=null) {
            throw new RuntimeException("Grabber " + g.getGrabberFQN() + " uses the old configuration. The properties 'parameters', 'namedParameters' and 'extract' cannot be used anymore in a grabber configuration section. Please refer to the documentation to see how to configure your grabber.");
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

      services.getApiAuthManager().applyConfig(config.getApiAuth(), creds.getFor(net.pieroxy.conkw.webapp.servlets.Api.class), ConfigReader.getDataDir());
      services.setApiManager(new ApiManager(grabbers));
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
    try {
      toDestroy = true;
      try {
        services.close();
      } catch (Exception e) {
        LOGGER.log(Level.INFO, "", e);
      }
      try {
        if (watchService!=null) {
          watchService.close();
        }
      } catch (Exception e) {
        LOGGER.log(Level.INFO, "", e);
      }
      if (grabbers!=null) {
        for (Grabber g : grabbers) {
          try {
            if (g!=null) {
              g.dispose();
            }
          } catch (Exception e) {
            LOGGER.log(Level.INFO, "", e);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "While shutting down", e);
    } finally {
      try {
        if (onDestroy!=null) onDestroy.run();
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "While shutting down", e);
      }
    }
  }

  public void runThread() {
    WatchKey key;
    while (true) {
      if (toDestroy) return;
      try {
        key = watchService.take();
        boolean shouldReloadConfig = false;
        boolean shouldReloadLoggingConfig = false;
        for (WatchEvent<?> event : key.pollEvents()) {
          if (ConfigReader.NAME.equals(event.context().toString()))
            shouldReloadConfig = true;
          if (ConfigReader.LOGGING_NAME.equals(event.context().toString()))
            shouldReloadLoggingConfig = true;
        }
        if (shouldReloadConfig) loadConfig();
        if (shouldReloadLoggingConfig) LogManager.getLogManager().readConfiguration();
        key.reset();
      } catch (java.nio.file.ClosedWatchServiceException e) {
        if (toDestroy) return;
      } catch (Exception e) {
      }
    }
  }

  public static String getInstanceName() {
    return instanceName;
  }
}
