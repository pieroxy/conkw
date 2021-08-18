package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import net.pieroxy.conkw.utils.JsonHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaxComputer {
  public static String FILENAME = ".autoMax.json";

  private File store;
  private Grabber grabber;
  Map<String, Map<String, Double>> database;

  public MaxComputer(Grabber g) {
    store = new File(g.getStorage(), g.getName() + FILENAME);
    grabber = g;
    grabber.log(Level.INFO, "maxComputer: MaxComputer store is " + store.getAbsolutePath());
    load();
  }

  public double getMax(Grabber grabber, String metricName, double currentValue) {

    if (grabber.canLogFiner()) grabber.log(Level.FINER, "maxComputer: Getting data for " + grabber.getName() + " " + metricName + " " + currentValue);
    String cn = grabber.getClass().getName();
    Map<String, Double> grabberDb = database.get(cn);
    if (grabberDb == null) {
      if (grabber.canLogFiner()) grabber.log(Level.FINER, "maxComputer: creating grabber db for " + cn);
      Map<String, Map<String, Double>> newdb = new HashMap<>(database);
      newdb.put(cn, grabberDb = new HashMap<>());
      grabberDb.put(metricName, currentValue);
      database = newdb;
      save();
      return currentValue;
    }
    Object maxO = grabberDb.get(metricName);
    if (maxO == null) {
      if (grabber.canLogFiner()) grabber.log(Level.FINER, "maxComputer: creating metric " + cn + " -- " + metricName);
      Map<String, Double> newGrabberDb = new HashMap<>(grabberDb);
      newGrabberDb.put(metricName, currentValue);
      database.put(cn, newGrabberDb);
      save();
      return currentValue;
    }
    double max = ((Number)maxO).doubleValue();
    if (max < currentValue) {
      if (grabber.canLogFiner()) grabber.log(Level.FINER, "maxComputer: Updating max for " + cn + " -- " + metricName);
      grabberDb.put(metricName, max=currentValue);
      save();
    }

    return max;
  }

  private synchronized void save() {
    try (FileOutputStream fos = new FileOutputStream(store)) {
      DslJson<Object> json = JsonHelper.getJson();
      JsonWriter w = JsonHelper.getWriter();

      synchronized (w) {
        w.reset(fos);
        json.serialize(w, database);
        w.flush();
      }
    } catch (IOException e) {
      grabber.log(Level.SEVERE, "maxComputer: Unable to save " + store.getAbsolutePath(), e);
    }
  }

  private void load() {
    if (store.exists()) {
      try (InputStream is = new FileInputStream(store)) {
        database = new DslJson<>(Settings.withRuntime()).deserialize(Map.class, is);
      } catch (Exception e) {
        database = new HashMap<>();
        grabber.log(Level.SEVERE, "maxComputer: Unable to parse " + store.getAbsolutePath(), e);
      }
    } else {
      database = new HashMap<>();
    }
  }
}
