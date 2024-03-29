package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.collectors.Collector;
import net.pieroxy.conkw.utils.LongHolder;
import net.pieroxy.conkw.utils.collections.BlackHoleMap;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;

public abstract class SimpleGrabber<T extends Collector, C> extends Grabber<T, C> {
  private volatile MaxComputer _maxComputer = null;
  Map<String, T> cachedResponses = HashMapPool.getInstance().borrow(HashMapPool.SIZE_UNKNOWN);
  Map<String, LongHolder> maxValuesLastComputed = HashMapPool.getInstance().borrow(HashMapPool.getUniqueCode(SimpleGrabber.class.getName(), getName()));


  public void extract(T toFill, String extractName, ExtractMethod<T> method, CDuration delay) {
    if (shouldExtract(extractName)) {
      if (delay.equals(Duration.ZERO)) {
        method.extract(toFill);
        return;
      }
      long now = System.currentTimeMillis();
      T cached = cachedResponses.get(extractName);
      if (cached==null || (now-cached.getTimestamp() > delay.asMilliseconds())) {
        if (canLogFiner()) {
          if (cached == null)
            log(Level.FINER,now +  " Grabbing " + extractName + " cached on null with delay " + delay.asMilliseconds());
          else
            log(Level.FINER,now + " Grabbing " + extractName + " cached on " + cached.getTimestamp() + " with delay " + delay.asMilliseconds());
        }
        try (T tmp = cached) {
          cached = getDefaultCollector(false);
          method.extract(cached);
          cached.collectionDone();
          cachedResponses.put(extractName, cached);
        }
      }

      cached.fillCollector(toFill);
    }
  }

  private MaxComputer getMaxComputer() {
    if (_maxComputer == null) _maxComputer = new MaxComputer(this);
    return _maxComputer;
  }

  protected void computeAutoMaxPerSecond(T c, String metricName, double value) {
    c.collect(metricName, value);
    LongHolder lh = maxValuesLastComputed.get(metricName);
    if (lh == null) {
      maxValuesLastComputed.put(metricName, new LongHolder(System.currentTimeMillis()));
    } else {
      double ratio = (System.currentTimeMillis() - lh.value)/1000.;
      if (ratio>0.50) { // Below 0.5s things might get out of whack
        double mv = getMaxComputer().getMax(this, metricName, value/ratio);
        c.collect("max$" + metricName, mv);
      } else {
        log(Level.INFO, "Ignoring value of " +value + " for metric " + metricName + " because ratio is "  + ratio);
      }
      lh.value = System.currentTimeMillis();
    }
  }

  /**
   *
   * @param c
   * @param metricName
   * @param value
   * @return The current maximum for the value provided
   */
  protected double computeAutoMaxMinAbsolute(T c, String metricName, double value) {
    double res = value;
    c.collect(metricName, value);
    {
      LongHolder lh = maxValuesLastComputed.get(metricName);
      if (lh == null) {
        maxValuesLastComputed.put(metricName, new LongHolder(System.currentTimeMillis()));
      } else {
        res = getMaxComputer().getMax(this, metricName, value);
        c.collect("max$" + metricName, res);
        lh.value = System.currentTimeMillis();
      }
    }
    {
      metricName = "min$" + metricName;
      value = -value;
      LongHolder lh = maxValuesLastComputed.get(metricName);
      if (lh == null) {
        maxValuesLastComputed.put(metricName, new LongHolder(System.currentTimeMillis()));
      } else {
        double mv = getMaxComputer().getMax(this, metricName, value);
        c.collect(metricName, -mv);
        lh.value = System.currentTimeMillis();
      }
    }
    return res;
  }

  @Override
  public void dispose() {
    Map tmp = cachedResponses;
    cachedResponses = new BlackHoleMap();
    HashMapPool.getInstance().giveBack(tmp);
  }
}
