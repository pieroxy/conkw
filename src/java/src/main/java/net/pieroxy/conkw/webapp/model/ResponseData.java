package net.pieroxy.conkw.webapp.model;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.grabbers.Grabber;

import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class ResponseData {
  private final static Logger LOGGER = Logger.getLogger(ResponseData.class.getName());

  private long timestamp;
  private long elapsedToGrab;
  @JsonAttribute(ignore = true)
  private Collection<String> errors=new LinkedList<>();
  private String name,extractor;
  private Map<String, Double> num = new HashMap<>();
  private Map<String, String> str = new HashMap<>();
  private Map<String, Long> timestamps = new HashMap<>();
  private Set<String> extracted   = new HashSet<>();

  public ResponseData() {
  }

  public ResponseData(Grabber grabber, long timestamp) {
    this.timestamp = timestamp;
    if (grabber!=null) {
      this.name = grabber.getName();
      this.extractor = grabber.getClass().getSimpleName();
    }
  }

  public void addExtracted(String e) {
    extracted.add(e);
  }

  public void addError(String e) {
    errors.add(e);
  }

  public void addMetric(String name, Double value) {
    num.put(name, value);
  }

  public void addMetric(String name, String value) {
    str.put(name, value);
  }

  public void addMetric(String name, int value) {
    addMetric(name, (double)value);
  }
  public void addMetric(String name, long value) {
    addMetric(name, (double)value);
  }

  public synchronized void atomicAddMetricWithTimestamp(String name, Double value) {
    String tsName = "num." + name;
    if (!num.containsKey(name)) {
      // Change the map structurally, needs to do it atomically.
      Map<String, Double> nnum = new HashMap<>();
      nnum.putAll(num);
      nnum.put(name, value);
      num = nnum;
    } else {
      num.put(name, value);
    }
    if (!timestamps.containsKey(tsName)) {
      // Change the map structurally, needs to do it atomically.
      Map<String, Long> nts = new HashMap<>();
      nts.putAll(timestamps);
      nts.put(tsName, System.currentTimeMillis());
      timestamps = nts;
    } else {
      timestamps.put(tsName, System.currentTimeMillis());
    }
  }

  public synchronized void atomicAddMetricWithTimestamp(String name, String value) {
    String tsName = "str." + name;
    if (!str.containsKey(name)) {
      // Change the map structurally, needs to do it atomically.
      Map<String, String> nstr = new HashMap<>();
      nstr.putAll(str);
      nstr.put(name, value);
      str = nstr;
    } else {
      str.put(name, value);
    }
    if (!timestamps.containsKey(tsName)) {
      // Change the map structurally, needs to do it atomically.
      Map<String, Long> nts = new HashMap<>();
      nts.putAll(timestamps);
      nts.put(tsName, System.currentTimeMillis());
      timestamps = nts;
    } else {
      timestamps.put(tsName, System.currentTimeMillis());
    }
  }

  /**
   *
   * @param timespan
   * @return true if something was deleted.
   */
  public synchronized boolean purgeDataOlderThan(Duration timespan) {
    long now = System.currentTimeMillis();
    boolean changed = false;
    Map<String, String> nstr = new HashMap<>();
    nstr.putAll(str);
    Map<String, Double> nnum = new HashMap<>();
    nnum.putAll(num);
    Map<String, Long> nts = new HashMap<>();
    nts.putAll(timestamps);

    HashSet<String> keys = new HashSet<>();
    keys.addAll(nstr.keySet());
    for (String key : keys) {
      String tsKey = "str." + key;
      Long d = nts.get(tsKey);
      if (d!=null) {
        if (now - d > timespan.toMillis()) {
          nstr.remove(key);
          nts.remove(tsKey);
          changed = true;
          LOGGER.info("PURGED " + key);
        }
      }
    }

    keys = new HashSet<>();
    keys.addAll(nnum.keySet());
    for (String key : keys) {
      String tsKey = "num." + key;
      Long d = nts.get(tsKey);
      if (d!=null) {
        if (now - d > timespan.toMillis()) {
          nnum.remove(key);
          nts.remove(tsKey);
          changed = true;
          LOGGER.info("PURGED " + key);
        }
      }
    }
    if (changed) {
      num = nnum;
      str = nstr;
      timestamps = nts;
    }
    return changed;
  }

  public Double getNumMetric(String name) {
    return num.get(name);
  }

  public String getStrMetric(String name) {
    return str.get(name);
  }

  public String getName() {
    return name;
  }

  public Collection<String> getErrors() {
    return errors;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long getElapsedToGrab() {
    return elapsedToGrab;
  }

  public void setElapsedToGrab(long elapsedToGrab) {
    this.elapsedToGrab = elapsedToGrab;
  }

  public Map<String, Double> getNum() {
    return num;
  }

  public Map<String, String> getStr() {
    return str;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNum(Map<String, Double> num) {
    this.num = num;
  }

  public void setStr(Map<String, String> str) {
    this.str = str;
  }

  public Set<String> getExtracted() {
    return extracted;
  }

  public void setExtracted(Set<String> extracted) {
    this.extracted = extracted;
  }

  public String getExtractor() {
    return extractor;
  }

  public void setExtractor(String extractor) {
    this.extractor = extractor;
  }

  public void setErrors(Collection<String> errors) {
    this.errors = errors;
  }

  public Map<String, Long> getTimestamps() {
    return timestamps;
  }

  public void setTimestamps(Map<String, Long> timestamps) {
    this.timestamps = timestamps;
  }
}
