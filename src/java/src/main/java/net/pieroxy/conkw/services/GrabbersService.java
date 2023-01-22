package net.pieroxy.conkw.services;

import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.config.CredentialsStore;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.grabbersBase.GrabberListener;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.config.GrabberConfigReader;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.servlets.ApiManager;
import net.pieroxy.conkw.webapp.servlets.Emi;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GrabbersService {
  private final static Logger LOGGER = Logger.getLogger(GrabbersService.class.getName());

  private final Services services;

  private List<Grabber> grabbers = new ArrayList<>();
  private Map<String, Grabber> grabbersByName;

  public GrabbersService(Services services) {
    this.services = services;
  }

  public synchronized void replaceGrabbers(List<Grabber> grabbers) {
    List<Grabber> oldGrabbers = new ArrayList<>(this.grabbers);
    this.grabbers.clear();
    this.grabbers.addAll(grabbers);
    this.grabbersByName = HashMapPool.getInstance().borrow(grabbers.size());
    grabbers.forEach(g -> this.grabbersByName.put(g.getName(), g));

    oldGrabbers.forEach(this::dispose);
  }


  public synchronized Grabber getGrabberByName(String name) {
    return grabbersByName.get(name);
  }

  public synchronized Stream<Grabber> getAllGrabbers() {
    return grabbers.stream();
  }

  public synchronized int count() {
    return grabbers.size();
  }

  public void destroy() {
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
  }

  public void loadConfig() {
    if (this.count()>0) {
      LOGGER.info("Reloading configuration.");
    } else {
      LOGGER.info("Loading configuration.");
    }
    Config config = ConfigReader.getConfig();
    Listener.setInstanceName(config.getInstanceName());
    CredentialsStore creds = ConfigReader.getCredentials();
    services.setCredentialsStore(creds);
    try {
      List<Grabber> newg = new ArrayList<>();
      Set<String> newgnames = new HashSet<>();
      Emi.clearGrabbers();
      for (GrabberConfig gc : config.getGrabbers()) {
        newg.add(instanciateGrabber(creds, newgnames, gc));
      }

      newg.forEach((gr) -> {
        if (gr instanceof GrabberListener) {
          ((GrabberListener) gr).setGrabberList(new ArrayList<>(newg));
          if (LOGGER.isLoggable(Level.FINE)) LOGGER.log(Level.FINE, "Setting grabbers to class "+ gr.getClass().getName());
        } else {
          if (LOGGER.isLoggable(Level.FINE)) LOGGER.log(Level.INFO, "NOT Setting grabbers to class "+ gr.getClass().getName());
        }
      });

      this.replaceGrabbers(newg);
      // This forces newly generated garbage to be recycled. Benefits:
      // - The user expects some slowliness when reloading the configuration, so the cost is low.
      // - The heap size by default (when the JVM starts) is around 1GB, which is 20x what this program needs. Forcing a
      //   GC shrinks it back closer to whatever is needed.
      System.gc();

      services.getApiAuthManager().applyConfig(config.getApiAuth(), creds.getFor(net.pieroxy.conkw.webapp.servlets.Api.class), ConfigReader.getDataDir());
      services.setApiManager(new ApiManager(services));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading and applying configuration.", e);
    }
  }

  private Grabber instanciateGrabber(CredentialsStore creds, Set<String> newgnames, GrabberConfig gc) {
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
        g.setLogLevel(Grabber.DEFAULT_LOG_LEVEL);
      }
      Object defaultConf = g.getDefaultConfig();
      if (defaultConf == null && gc.getConfig()!=null) {
        throw new RuntimeException("Grabber " + g.getGrabberFQN() + " did not provide a default configuration but the config file has one.");
      }
      g.setDefaultAccumulator(new AccumulatorExpressionParser().parse( gc.getDefaultAccumulator()));
      g.setConfig(GrabberConfigReader.fillObject(g.getDefaultConfig(), gc.getConfig(), null), creds.getFor(g.getClass()));
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
          newgnames.add(g.getName());
          return g;
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Initializing grabber " + gc.getImplementation() + " with name " + gc.getName(), e);
      throw new RuntimeException(e);
    }
  }

  public void updateGrabberConfig(String grabberName, GrabberConfig config) {
    Set<String> grabbersNames = new HashSet<>();
    CredentialsStore creds = ConfigReader.getCredentials();
    getAllGrabbers().forEach(g -> grabbersNames.add(g.getName()));
    grabbersNames.remove(grabberName);
    Grabber g = instanciateGrabber(creds, grabbersNames, config);
    for (int i=0 ; i<grabbers.size() ; i++) {
      if (grabbers.get(i).getName().equals(grabberName)) {
        LOGGER.info("Grabber " + grabberName + " replaced");
        dispose(grabbers.set(i, g));
        return;
      }
    }
    throw new RuntimeException("Something weird happened.");
  }

  private void dispose(Grabber grabber) {
    if (grabber == null) return;
    new Thread(() -> {
      try {Thread.sleep(500);} catch (Exception e) {} // Give a bit of time to the grabber instance
      try {
        grabber.dispose();
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Failed disposing a grabber (" + grabber.getName() + ")", e);
      }
    }).start();
  }
}
