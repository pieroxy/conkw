package net.pieroxy.conkw.webapp;

import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.ConfigReader;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.webapp.grabbers.*;
import net.pieroxy.conkw.webapp.servlets.Api;
import net.pieroxy.conkw.webapp.servlets.HtmlTemplates;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Listener implements ServletContextListener {

  Config config;

  private List<Grabber> grabbers;
  private WatchService watchService;
  private boolean toDestroy = false;

  public void loadConfig() {
    Config config = ConfigReader.getConfig();
    try {
      List<Grabber> newg = new ArrayList<>();
      for (GrabberConfig gc : config.getGrabbers()) {
        Grabber g = (Grabber) Class.forName("net.pieroxy.conkw.webapp.grabbers." + gc.getId() + "Grabber").newInstance();
        g.initConfig(ConfigReader.getHomeDir(), gc.getParameters());
        newg.add(g);
      }

      Collection<Grabber> old= grabbers;
      if (old!=null) {
        System.out.println("Reloading configuration.");
        // this is a hot swap :
        // Start threads and all.
        newg.forEach((gr) -> gr.grab());
        // Replace the grabbers.
        grabbers = newg;
        // Recycle the old grabbers.
        old.forEach((gr) -> gr.dispose());
        old = null;
      }
      // This forces newly generated garbage to be recycled. Benefits:
      // - The user expects some slowliness when reloading the configuration, so the cost is low.
      // - The heap size by default (when the JVM starts) is around 1GB, which is 20x what this program needs. Forcing a
      //   GC shrinks it back closer to whatever is needed.
      System.gc();

      Api.setGrabbers(grabbers);
      HtmlTemplates.setGrabbers(grabbers);
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
      HtmlTemplates.close();
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
