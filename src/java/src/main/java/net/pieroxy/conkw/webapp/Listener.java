package net.pieroxy.conkw.webapp;

import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Listener implements ServletContextListener {
  private final static Logger LOGGER = Logger.getLogger(Listener.class.getName());

  private boolean toDestroy = false;
  private final Runnable onDestroy;
  private final Services services;
  private WatchService watchService;
  private static String instanceName;


  public Listener(Services services, Runnable onDestroy) {
    this.services = services;
    this.onDestroy = onDestroy;
    services.setApiAuthManager(new ApiAuthManager());
  }

  public static void setInstanceName(String instanceName) {
    Listener.instanceName = instanceName;
  }


  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    services.getGrabbersService().loadConfig();
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
      this.services.getGrabbersService().destroy();
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
        boolean shouldReloadLoggingConfig = false;
        for (WatchEvent<?> event : key.pollEvents()) {
          if (ConfigReader.LOGGING_NAME.equals(event.context().toString()))
            shouldReloadLoggingConfig = true;
        }
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
