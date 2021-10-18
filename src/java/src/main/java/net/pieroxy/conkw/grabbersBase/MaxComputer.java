package net.pieroxy.conkw.grabbersBase;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
      Map tmp = database;
      Map<String, Map<String, Double>> newdb = HashMapPool.getInstance().borrow(database, 1);
      newdb.put(cn, grabberDb = new HashMap<>());
      grabberDb.put(metricName, currentValue);
      database = newdb;
      save();
      HashMapPool.getInstance().giveBack(tmp);
      return currentValue;
    }
    Object maxO = grabberDb.get(metricName);
    if (maxO == null) {
      if (grabber.canLogFiner()) grabber.log(Level.FINER, "maxComputer: creating metric " + cn + " -- " + metricName);
      Map<String, Double> newGrabberDb = HashMapPool.getInstance().borrow(grabberDb, 1);
      newGrabberDb.put(metricName, currentValue);
      HashMapPool.getInstance().giveBack(database.put(cn, newGrabberDb));
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
      try {
        database = JsonHelper.readFromFile(Map.class, store);
      } catch (Exception e) {
        database = HashMapPool.getInstance().borrow(1);
        grabber.log(Level.SEVERE, "maxComputer: Unable to parse " + store.getAbsolutePath(), e);
      }
    } else {
      database = HashMapPool.getInstance().borrow(1);
    }
  }
}